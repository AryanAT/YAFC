package com.invest.indices.service.impl;

import com.invest.indices.action.CalculateReturns;
import com.invest.indices.domain.errors.InvalidResponseException;
import com.invest.indices.domain.errors.MutualFundExistsException;
import com.invest.indices.domain.model.*;
import com.invest.indices.infra.repository.*;
import com.invest.indices.service.MutualFundService;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.format.DateTimeFormatterBuilder;
import java.util.Comparator;
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
    private final AnnualReturnRepository annualReturnRepository;
    private final CalculateReturns calculateReturns;
    private final SchemeNameAndCodeMapRepository schemeNameAndCodeMapRepository;
    private final ThreeYearCAGRRepository threeYearCAGRRepository;
    private final FiveYearCAGRRepository fiveYearCAGRRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public MutualFundServiceImpl(
            MutualFundRepository mutualFundRepository,
            CalculateReturns calculateReturns,
            SchemeNameAndCodeMapRepository schemeNameAndCodeMapRepository,
            AnnualReturnRepository annualReturnRepository,
            ThreeYearCAGRRepository threeYearCAGRRepository,
            FiveYearCAGRRepository fiveYearCAGRRepository
    ) {
        this.mutualFundRepository = mutualFundRepository;
        this.calculateReturns = calculateReturns;
        this.schemeNameAndCodeMapRepository = schemeNameAndCodeMapRepository;
        this.annualReturnRepository = annualReturnRepository;
        this.threeYearCAGRRepository = threeYearCAGRRepository;
        this.fiveYearCAGRRepository = fiveYearCAGRRepository;
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
    public PortfolioReport calculateReturnForListOfMutualFunds(List<ReturnInputs> returnInputs) {
        List<ReturnOutput> returnOutputs = returnInputs.stream().map(calculateReturns::with).toList();
        Double portfolioInvestmentAmount = 0.0;
        Double portfolioFinalAmount = 0.0;
        Double portfolioAbsoluteReturns;
        for (ReturnOutput returnOutput: returnOutputs) {
            portfolioInvestmentAmount += returnOutput.getInvAmount();
            portfolioFinalAmount += returnOutput.getFinalAmount();
        }
        portfolioAbsoluteReturns = ((portfolioFinalAmount * 100) / portfolioInvestmentAmount) - 100;
        Double portfolioProfitOrLoss = portfolioFinalAmount - portfolioInvestmentAmount;
        for (ReturnOutput returnOutput: returnOutputs) {
            double fundsProfitOrLoss = returnOutput.getFinalAmount() - returnOutput.getInvAmount();
            double weightedContribution = (returnOutput.getFinalAmount() / portfolioFinalAmount) * 100;
            double returnsPercentageShareInPortfolio = (fundsProfitOrLoss / portfolioProfitOrLoss) * 100;
            returnOutput.setAmountPercentageShareInPortfolio(weightedContribution);
            returnOutput.setReturnsPercentageShareInPortfolio(returnsPercentageShareInPortfolio);
        }
        return new PortfolioReport(returnOutputs, portfolioAbsoluteReturns, portfolioInvestmentAmount, portfolioFinalAmount, portfolioProfitOrLoss);
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
    private void calculateAnnualGrowth(List<MutualFundEntity> mutualFundEntities) {
        if (mutualFundEntities.size() < 12) {
            System.out.println("Less than 12 months cant calculate annual return");
            return;
        }
        double lastNav = Double.NaN;
        for (int i = 0; i < mutualFundEntities.size(); i++) {
            MutualFundEntity mutualFundEntity = mutualFundEntities.get(i);
            LocalDate parsedDate = parseDate(mutualFundEntity.getDate());
            if (parsedDate.getMonth().equals(Month.JANUARY)) {
               lastNav = mutualFundEntity.getNav();
               break;
            }
        }

        double annualReturn;

        List<AnnualReturnEntity> annualReturnEntities = new ArrayList<>();
        for (int i = 0; i < mutualFundEntities.size(); i++) {
            MutualFundEntity mutualFundEntity = mutualFundEntities.get(i);
            LocalDate date = parseDate(mutualFundEntity.getDate());
            if (date.getMonth().equals(Month.DECEMBER)) {
                if (!Double.isNaN(lastNav)) {
                    annualReturn = ((mutualFundEntity.getNav() - lastNav) / lastNav) * 100;
                    lastNav = mutualFundEntity.getNav();
                    annualReturnEntities.add(new AnnualReturnEntity(UUID.randomUUID(), date.getYear(), mutualFundEntity.getSchemeName(),  mutualFundEntity.getSchemeCode(), annualReturn));
                }
            }
        }
        annualReturnRepository.saveAll(annualReturnEntities);
    }

    private void calculateCAGR(List<MutualFundEntity> mutualFundEntities, int term) {
        if (mutualFundEntities.isEmpty()) {
            // TODO Throw exception
            System.out.println("Empty List Provided");
            return;
        }
        if (mutualFundEntities.size() < term * 12) {
            System.out.println(String.format("Less than %d years, cannot calculate CAGR", term));
            return;
        }

        List<ThreeYearCAGR> threeYearCAGRS = new ArrayList<>();
        List<FiveYearCAGR> fiveYearCAGRS = new ArrayList<>();
        //  - (term * 12) in loop to avoid out of bound access in mutualFundEntities.get(i + (term * 12) - 1).getNav()
        for (int i = 0; i < mutualFundEntities.size() - (term * 12); i++) {
            MutualFundEntity startEntity = mutualFundEntities.get(i);
            MutualFundEntity endEntity = mutualFundEntities.get(i + (term * 12) - 1);
            double startNav = startEntity.getNav();
            double endNav = endEntity.getNav();
            Double compoundedAnnualGrowthRate = (Math.pow((endNav / startNav), (1.0 / term)) - 1) * 100;
            if (term == 3) {
                threeYearCAGRS.add(new ThreeYearCAGR(UUID.randomUUID(), endEntity.getDate(), startEntity.getSchemeName(),  startEntity.getSchemeCode(), compoundedAnnualGrowthRate));
            }
            if (term == 5) {
                fiveYearCAGRS.add(new FiveYearCAGR(UUID.randomUUID(), endEntity.getDate(), startEntity.getSchemeName(),  startEntity.getSchemeCode(), compoundedAnnualGrowthRate));
            }
        }
        if (term == 3) {
            threeYearCAGRRepository.saveAll(threeYearCAGRS);
        }
        if (term == 5) {
            fiveYearCAGRRepository.saveAll(fiveYearCAGRS);
        }
    }

    @Override
    public void updateAnnualReturn() {
        List<SchemeNameAndCodeMapEntity> schemeNameAndCodeMapEntities = schemeNameAndCodeMapRepository.findAll();
        for (SchemeNameAndCodeMapEntity schemeNameAndCodeMapEntity : schemeNameAndCodeMapEntities) {
            Integer schemeCode = schemeNameAndCodeMapEntity.getSchemeCode();
            List<MutualFundEntity> mutualFundEntities = mutualFundRepository.findBySchemeCode(schemeCode)
                    .stream().sorted(Comparator.comparing(mutualFundEntity -> parseDate(mutualFundEntity.getDate()))).toList();
            calculateAnnualGrowth(mutualFundEntities);
        }
    }

    @Override
    public void updateCAGR() {
        List<SchemeNameAndCodeMapEntity> schemeNameAndCodeMapEntities = schemeNameAndCodeMapRepository.findAll();
        for (SchemeNameAndCodeMapEntity schemeNameAndCodeMapEntity : schemeNameAndCodeMapEntities) {
            Integer schemeCode = schemeNameAndCodeMapEntity.getSchemeCode();
            List<MutualFundEntity> mutualFundEntities = mutualFundRepository.findBySchemeCode(schemeCode)
                    .stream().sorted(Comparator.comparing(mutualFundEntity -> parseDate(mutualFundEntity.getDate()))).toList();
            calculateCAGR(mutualFundEntities, 3);
            calculateCAGR(mutualFundEntities, 5);
        }
    }

    @Override
    public SimpleSIPOutput getSimpleSip(SimpleSIPInput simpleSIPInput) {
        double monthlyReturnPercentage = simpleSIPInput.getExpectedAnnualReturn() / 12 / 100;
        int tenureInMonths = simpleSIPInput.getTenureInYears() * 12;
        long investedAmount = (long) simpleSIPInput.getAmount() * tenureInMonths;
        long finalAmount = Math.round(simpleSIPInput.getAmount() * ((Math.pow(1 + monthlyReturnPercentage, tenureInMonths) - 1) / monthlyReturnPercentage) * (1 + monthlyReturnPercentage));
        double investmentsMultipliedBy = (double) finalAmount / investedAmount;
        return new SimpleSIPOutput(
                finalAmount,
                investedAmount,
                finalAmount - investedAmount,
                simpleSIPInput.getAmount(),
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                LocalDate.now().plusYears(simpleSIPInput.getTenureInYears()).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                investmentsMultipliedBy
        );
    }

    @Data
    private static final class MonthYear {
        private final Month month;
        private final int year;
    }
}
