package com.invest.indices.domain.model;

import lombok.Data;

@Data
public class SimpleSIPInput {
    private int amount;
    private double expectedAnnualReturn;
    private int tenureInYears;
}
