package com.invest.indices.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReturnOutput {
    private Double finalAmount;
    private Double invAmount;
    private String fundName;
    private Double absoluteReturns;
    // This field is null when calculating individual returns and will be set when generating portfolio report
    private Double returnContributions;

    public ReturnOutput(Double finalAmount, Double invAmount, String fundName, Double absoluteReturns) {
        this.finalAmount = finalAmount;
        this.invAmount = invAmount;
        this.fundName = fundName;
        this.absoluteReturns = absoluteReturns;
    }
}
