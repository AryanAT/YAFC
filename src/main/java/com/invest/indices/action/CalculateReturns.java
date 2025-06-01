package com.invest.indices.action;

import com.invest.indices.domain.model.FiveYearCAGR;
import com.invest.indices.domain.model.MutualFundEntity;
import com.invest.indices.domain.model.ReturnInputs;
import com.invest.indices.domain.model.ReturnOutput;
import com.invest.indices.domain.model.ThreeYearCAGR;
import com.invest.indices.infra.repository.AnnualReturnRepository;
import com.invest.indices.infra.repository.FiveYearCAGRRepository;
import com.invest.indices.infra.repository.MutualFundRepository;
import com.invest.indices.infra.repository.ThreeYearCAGRRepository;
import org.decampo.xirr.Transaction;
import org.decampo.xirr.Xirr;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import static com.invest.indices.common.Utils.roundToDouble;


@Service
public class CalculateReturns {
    private final MutualFundRepository mutualFundRepository;
    private final AnnualReturnRepository annualReturnRepository;
    private final ThreeYearCAGRRepository threeYearCAGRRepository;
    private final FiveYearCAGRRepository fiveYearCAGRRepository;


    public CalculateReturns(
            MutualFundRepository mutualFundRepository,
            AnnualReturnRepository annualReturnRepository,
            ThreeYearCAGRRepository threeYearCAGRRepository,
            FiveYearCAGRRepository fiveYearCAGRRepository
    ) {
        this.mutualFundRepository = mutualFundRepository;
        this.annualReturnRepository = annualReturnRepository;
        this.threeYearCAGRRepository = threeYearCAGRRepository;
        this.fiveYearCAGRRepository = fiveYearCAGRRepository;
    }

    public ReturnOutput with(ReturnInputs returnInputs) {
        double totalUnitsPurchased = 0;
        double totalInvestmentAmount = 0;
        double absoluteReturns;
        double finalAmount;
        ArrayList<Transaction> transactions = new ArrayList<>();


        List<MutualFundEntity> mutualFundEntityList = mutualFundRepository.findBySchemeCode(returnInputs.getSchemeCode());
        LocalDate fromDate = parseDateString(returnInputs.getFromDate());
        LocalDate toDate = parseDateString(returnInputs.getToDate());

        LocalDate startOfFromMonth = adjustToStartOfMonth(fromDate);
        LocalDate endOfToMonth = adjustToEndOfMonth(toDate);

        List<MutualFundEntity> filteredMutualFundEntityList = mutualFundEntityList.stream()
                .filter(mutualFundEntity -> {
                    LocalDate entityDate = parseDateString(mutualFundEntity.getDate());
                    return !entityDate.isBefore(startOfFromMonth) && !entityDate.isAfter(endOfToMonth);
                }).sorted(Comparator.comparing(mutualFundEntity -> parseDateString(mutualFundEntity.getDate())))
                .toList();
        if (filteredMutualFundEntityList.isEmpty()) {
            //TODO Throw Exception
            System.out.println("Could not find any entities");
        }

        for (MutualFundEntity mutualFundEntity : filteredMutualFundEntityList) {
            transactions.add(new Transaction(-returnInputs.getInvAmount(), dateFormatter(mutualFundEntity.getDate())));
            totalUnitsPurchased += returnInputs.getInvAmount() / mutualFundEntity.getNav();
            totalInvestmentAmount += returnInputs.getInvAmount();
        }

        finalAmount = totalUnitsPurchased * filteredMutualFundEntityList.get(filteredMutualFundEntityList.size() - 1).getNav();
        absoluteReturns = ((finalAmount * 100) / totalInvestmentAmount) - 100;
        double totalProfitOrLoss = finalAmount - totalInvestmentAmount;
        transactions.add(new Transaction(finalAmount, dateFormatter(returnInputs.getToDate())));
        double xirr = new Xirr(transactions).xirr() * 100;
        double threeYearRollingReturn = getThreeYearRollingReturns(returnInputs.getSchemeCode(), LocalDate.now());
        double fiveYearRollingReturn = getFiveYearRollingReturns(returnInputs.getSchemeCode(), LocalDate.now());
        String sipStartDate = returnInputs.getFromDate();
        String sipEndDate = returnInputs.getToDate();
        Double investmentMultipliedBy = finalAmount / totalInvestmentAmount;
        Double oneYearCAGR = annualReturnRepository.findBySchemeCodeAndYear(returnInputs.getSchemeCode(), toDate.getYear() - 1).getAnnualReturn();
        Double fiveYearCAGR = fiveYearCAGRRepository.findLatestBySchemeCodeAndDate(returnInputs.getSchemeCode(), returnInputs.getToDate()).get().getFiveYearCAGR();
        Double threeYearCAGR = threeYearCAGRRepository.findLatestBySchemeCodeAndDate(returnInputs.getSchemeCode(), returnInputs.getToDate()).get().getThreeYearCAGR();

        return new ReturnOutput(
                roundToDouble(finalAmount),
                roundToDouble(totalInvestmentAmount),
                mutualFundEntityList.get(0).getSchemeName(),
                roundToDouble(absoluteReturns),
                roundToDouble(totalProfitOrLoss),
                roundToDouble(xirr),
                roundToDouble(threeYearRollingReturn),
                roundToDouble(fiveYearRollingReturn),
                sipStartDate,
                sipEndDate,
                roundToDouble(investmentMultipliedBy),
                roundToDouble(oneYearCAGR),
                roundToDouble(threeYearCAGR),
                roundToDouble(fiveYearCAGR)
        );
    }

    private LocalDate adjustToStartOfMonth(LocalDate date) {
        return date.withDayOfMonth(1);
    }

    private LocalDate adjustToEndOfMonth(LocalDate date) {
        return date.withDayOfMonth(date.lengthOfMonth());
    }

    private String dateFormatter(String date) {
        DateTimeFormatter originalFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        // Step 2: Create a formatter for the desired format
        DateTimeFormatter desiredFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


        // Step 3: Parse the original date and format it to the desired format
        LocalDate formattedDate = LocalDate.parse(date, originalFormatter);
        return formattedDate.format(desiredFormatter);
    }

    private LocalDate parseDateString(String dateString) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return LocalDate.parse(dateString, dateTimeFormatter);
    }

    private Double getThreeYearRollingReturns(int schemeCode, LocalDate today) {
        List<ThreeYearCAGR> threeYearCAGRS = threeYearCAGRRepository.findBySchemeCode(schemeCode);
        return threeYearCAGRS.stream().sorted(Comparator.comparing(threeYearCAGR -> parseDateString(threeYearCAGR.getDate())))
                .filter(threeYearCAGR -> parseDateString(threeYearCAGR.getDate()).isAfter(today.minusYears(3)))
                .mapToDouble(ThreeYearCAGR::getThreeYearCAGR).average().orElse(0.0);
    }

    private Double getFiveYearRollingReturns(int schemeCode, LocalDate today) {
        List<FiveYearCAGR> fiveYearCAGRS = fiveYearCAGRRepository.findBySchemeCode(schemeCode);
        return fiveYearCAGRS.stream().sorted(Comparator.comparing(fiveYearCAGR -> parseDateString(fiveYearCAGR.getDate())))
                .filter(fiveYearCAGR -> parseDateString(fiveYearCAGR.getDate()).isAfter(today.minusYears(5)))
                .mapToDouble(FiveYearCAGR::getFiveYearCAGR).average().orElse(0.0);
    }
}
