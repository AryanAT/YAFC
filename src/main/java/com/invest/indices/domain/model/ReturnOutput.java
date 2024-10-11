package com.invest.indices.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReturnOutput {
    private Double finalAmount;
    private Double invAmount;
    private String fundName;
    private Double absoluteReturns;
    private Double fundProfitOrLoss;
    private Double xirr;
    private Double threeYearRollingReturn;
    private Double fiveYearRollingReturn;
    private String sipStartDate;
    private String sipEndDate;
    private Double investmentMultipliedBy;
    private Double oneYearCAGR;
    private Double threeYearCAGR;
    private Double fiveYearCAGR;
    // This field is null when calculating individual returns and will be set when generating portfolio report
    private Double amountPercentageShareInPortfolio;
    private Double returnsPercentageShareInPortfolio;

    public ReturnOutput(Double finalAmount,
                        Double invAmount,
                        String fundName,
                        Double absoluteReturns,
                        Double fundProfitOrLoss,
                        Double xirr,
                        Double threeYearRollingReturn,
                        Double fiveYearRollingReturn,
                        String sipStartDate,
                        String sipEndDate,
                        Double investmentMultipliedBy,
                        Double oneYearCAGR,
                        Double threeYearCAGR,
                        Double fiveYearCAGR
    ) {
        this.finalAmount = finalAmount;
        this.invAmount = invAmount;
        this.fundName = fundName;
        this.absoluteReturns = absoluteReturns;
        this.fundProfitOrLoss = fundProfitOrLoss;
        this.xirr = xirr;
        this.threeYearRollingReturn = threeYearRollingReturn;
        this.fiveYearRollingReturn = fiveYearRollingReturn;
        this.sipStartDate = sipStartDate;
        this.sipEndDate = sipEndDate;
        this.investmentMultipliedBy = investmentMultipliedBy;
        this.oneYearCAGR = oneYearCAGR;
        this.threeYearCAGR = threeYearCAGR;
        this.fiveYearCAGR = fiveYearCAGR;
    }
}
