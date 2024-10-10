package com.invest.indices.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SimpleSIPOutput {
    private double finalAmount;
    private long investmentAmount;
    private long profitOrLoss;
    private int monthlySip;
    private String sipStartDate;
    private String sipEndDate;
    private double investmentMultipliedBy;
}
