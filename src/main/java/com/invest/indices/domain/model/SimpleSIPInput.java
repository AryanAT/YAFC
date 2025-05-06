package com.invest.indices.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SimpleSIPInput {
    private int amount;
    @JsonProperty("returns")
    private double expectedAnnualReturn;
    @JsonProperty("tenure")
    private int tenureInYears;
}
