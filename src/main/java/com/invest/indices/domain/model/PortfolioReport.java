package com.invest.indices.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PortfolioReport {
    private List<ReturnOutput> returnOutputs;
    private Double portfolioAbsoluteReturns;
    private Double portfolioTotalInvestment;
    private Double portfolioFinalAmount;
    private Double portfolioProfitOrLoss;

    public PortfolioReport(List<ReturnOutput> returnOutputs) {
        this.returnOutputs = returnOutputs;
    }
}
