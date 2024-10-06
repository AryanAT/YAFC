package com.invest.indices.action;

import com.invest.indices.domain.model.MutualFundEntity;
import com.invest.indices.domain.model.ReturnInputs;
import com.invest.indices.domain.model.ReturnOutput;
import com.invest.indices.infra.repository.MutualFundRepository;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Service
public class CalculateReturns {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    private final MutualFundRepository mutualFundRepository;

    public CalculateReturns(MutualFundRepository mutualFundRepository) {
        this.mutualFundRepository = mutualFundRepository;
    }

    public ReturnOutput with(ReturnInputs returnInputs) {
        double totalUnitsPurchased = 0;
        double totalInvestmentAmount = 0;
        double absoluteReturns;
        double finalAmount;
        List<MutualFundEntity> mutualFundEntityList = mutualFundRepository.findBySchemeCode(returnInputs.getSchemeCode());
        Date fromDate;
        Date toDate;
        try {
            fromDate = DATE_FORMAT.parse(returnInputs.getFromDate());
            toDate = DATE_FORMAT.parse(returnInputs.getToDate());
        } catch (ParseException exception) {
            System.out.println("Invalid Date");
            return new ReturnOutput(0.0, 0.0, mutualFundEntityList.get(0).getSchemeName(), 0.0);
        }

        Date startOfFromMonth = adjustToStartOfMonth(fromDate);
        Date endOfToMonth = adjustToEndOfMonth(toDate);

        List<MutualFundEntity> filteredMutualFundEntityList = mutualFundEntityList.stream()
                .filter(mutualFundEntity -> {
                    try {
                        Date entityDate = DATE_FORMAT.parse(mutualFundEntity.getDate());
                        return !entityDate.before(startOfFromMonth) && !entityDate.after(endOfToMonth);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }).sorted(Comparator.comparing(mutualFundEntity -> {
                    try {
                        return DATE_FORMAT.parse(mutualFundEntity.getDate());
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }))
                .toList();

        for (MutualFundEntity mutualFundEntity : filteredMutualFundEntityList) {
            totalUnitsPurchased += returnInputs.getInvAmount() / mutualFundEntity.getNav();
            totalInvestmentAmount += returnInputs.getInvAmount();
        }
        finalAmount = totalUnitsPurchased * filteredMutualFundEntityList.get(filteredMutualFundEntityList.size() - 1).getNav();
        absoluteReturns = ((finalAmount * 100) / totalInvestmentAmount) - 100;
        return filteredMutualFundEntityList.isEmpty()
                ? new ReturnOutput(0.0, 0.0, mutualFundEntityList.get(0).getSchemeName(), absoluteReturns)
                : new ReturnOutput(finalAmount, totalInvestmentAmount, mutualFundEntityList.get(0).getSchemeName(), absoluteReturns);
    }

    private Date adjustToStartOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    private Date adjustToEndOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }
}
