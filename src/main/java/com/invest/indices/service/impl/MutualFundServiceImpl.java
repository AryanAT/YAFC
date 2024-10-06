package com.invest.indices.service.impl;

import com.invest.indices.action.CalculateReturns;
import com.invest.indices.domain.errors.InvalidResponseException;
import com.invest.indices.domain.errors.MutualFundExistsException;
import com.invest.indices.domain.model.*;
import com.invest.indices.infra.repository.MutualFundRepository;
import com.invest.indices.infra.repository.SchemeNameAndCodeMapRepository;
import com.invest.indices.service.MutualFundService;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class MutualFundServiceImpl implements MutualFundService {

    private static Integer counter = 0;

    private final MutualFundRepository mutualFundRepository;
    private final CalculateReturns calculateReturns;
    private final SchemeNameAndCodeMapRepository schemeNameAndCodeMapRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public MutualFundServiceImpl(
            MutualFundRepository mutualFundRepository,
            CalculateReturns calculateReturns, SchemeNameAndCodeMapRepository schemeNameAndCodeMapRepository
    ) {
        this.mutualFundRepository = mutualFundRepository;
        this.calculateReturns = calculateReturns;
        this.schemeNameAndCodeMapRepository = schemeNameAndCodeMapRepository;
    }

    @Override
    public List<MutualFundEntity> getAll() {
        return mutualFundRepository.findAll();
    }

    @Override
    public ReturnOutput calculateReturn(ReturnInputs returnInputs) {
        return calculateReturns.with(returnInputs);
    }

    @Override
    public List<ReturnOutput> calculateReturnForListOfMutualFunds(List<ReturnInputs> returnInputs) {
        return returnInputs.stream().map(calculateReturns::with).toList();
    }

    @Override
    public List<SchemeNameAndCodeMapEntity> fuzzySearchMutualFund(String schemeName) {
        return schemeNameAndCodeMapRepository.findBySchemeNameContainingIgnoreCase(schemeName)
                .stream()
                .map(schemeNameAndCodeMapEntity -> new SchemeNameAndCodeMapEntity(
                        schemeNameAndCodeMapEntity.getSchemeCode(),
                        schemeNameAndCodeMapEntity.getSchemeName()
                        )
                )
                .collect(Collectors.toList());
    }

    @Override
    public ResponseEntity<MutualFund> getLatestNav(Integer id) {
        String url = String.format("https://api.mfapi.in/mf/%d/latest", id);
        MutualFund mutualFund = restTemplate.getForObject(url, MutualFund.class);
        return ResponseEntity.ok(mutualFund);
    }

    @Override
    public ResponseEntity<List<MutualFundEntity>> getHistoricalNav(Integer id) {

        System.out.printf("Getting Historical data for schemeCode %d \nThis is our %d mutualFund%n", id, ++counter);
        String url = String.format("https://api.mfapi.in/mf/%d", id);
        if (!mutualFundRepository.findBySchemeCode(id).isEmpty()) {
            throw new MutualFundExistsException("MUTUAL_FUND_ALREADY_SAVED", String.format("We already have details for this mutual fund in database schemeCode: %d", id));
        }
        MutualFund mutualFund = restTemplate.getForObject(url, MutualFund.class);
        if (mutualFund == null || mutualFund.getData().isEmpty()) {
            throw new InvalidResponseException("INVALID_RESPONSE_FROM_EXTERNAL_SERVICE", String.format("MF API returned null in response for this id %d", id));
        }
        List<MutualFundEntity> filteredMutualFunds = filterFirstNavOfMonth(mutualFund);
        System.out.println(filteredMutualFunds.size());
        return ResponseEntity.ok(filteredMutualFunds);
    }

    private List<MutualFundEntity> filterFirstNavOfMonth(MutualFund mutualFund) {

        HashMap<MonthYear, PriceData> priceDataPerMonthMap = new HashMap<>();
        List<MutualFundEntity> filteredListOfMutualFund = new ArrayList<>();

        for (PriceData priceData : mutualFund.getData()) {
            LocalDate date = parseDate(priceData.getDate());
            MonthYear monthYear = new MonthYear(date.getMonth(), date.getYear());
            if (!priceDataPerMonthMap.containsKey(monthYear) || date.isBefore(parseDate(priceDataPerMonthMap.get(monthYear).getDate()))) {
                priceDataPerMonthMap.put(monthYear, priceData);
            }
        }

        mutualFund.setData(priceDataPerMonthMap.values().stream().toList());

        for (PriceData priceData : mutualFund.getData()) {
            MutualFundEntity mutualFundEntity = new MutualFundEntity();
            mutualFundEntity.setUuid(UUID.randomUUID());
            mutualFundEntity.setNav(priceData.getNav());
            mutualFundEntity.setDate(priceData.getDate());
            mutualFundEntity.setFunHouse(mutualFund.getMeta().getFundHouse());
            mutualFundEntity.setSchemeName(mutualFund.getMeta().getSchemeName());
            mutualFundEntity.setSchemeCode(mutualFund.getMeta().getSchemeCode());
            mutualFundEntity.setSchemeType(mutualFund.getMeta().getSchemeType());
            mutualFundEntity.setSchemeCategory(mutualFund.getMeta().getSchemeCategory());
            filteredListOfMutualFund.add(mutualFundEntity);
        }

        return filteredListOfMutualFund;
    }

    private LocalDate parseDate(String dateString) {
        final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return LocalDate.parse(dateString, dateFormatter);
    }

    @Retryable(value = {RestClientException.class}, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public void saveAllMutualFunds() {
        List<SchemeNameAndCodeMapEntity> schemeNameAndCodeMapEntities = schemeNameAndCodeMapRepository.findAll();
        List<MutualFundEntity> mutualFundEntityList = new ArrayList<>();

        int batchSize = 100; // Choose a reasonable batch size
        int counter = 0;

        for (SchemeNameAndCodeMapEntity schemeNameAndCodeMapEntity : schemeNameAndCodeMapEntities) {
            Integer schemeCode = schemeNameAndCodeMapEntity.getSchemeCode();

            try {
                mutualFundEntityList.addAll(Objects.requireNonNull(getHistoricalNav(schemeCode).getBody()));
            } catch (MutualFundExistsException exception) {
                System.err.println(exception.getErrorMessage());
                continue;
            } catch (InvalidResponseException exception) {
                System.err.println(exception.getErrorMessage());
                continue;
            }

            if (++counter % batchSize == 0) {
                mutualFundRepository.saveAll(mutualFundEntityList);
                mutualFundEntityList.clear(); // Clear list to avoid memory issues
                System.out.println("Saved batch of mutual funds");

                // Throttle to avoid overloading external API
                try {
                    Thread.sleep(2000); // 2-second delay between batches
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        // Save any remaining entities
        if (!mutualFundEntityList.isEmpty()) {
            mutualFundRepository.saveAll(mutualFundEntityList);
        }
    }

    @Data
    private static final class MonthYear {
        private final Month month;
        private final int year;
    }
}
