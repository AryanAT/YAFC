package com.invest.indices.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PriceData {
    @JsonProperty("date")
    private String date;
    @JsonProperty("nav")
    private double nav;
}
