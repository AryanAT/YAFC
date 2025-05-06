package com.invest.indices.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class ReturnInputs {
    @JsonProperty("amount")
    private Double invAmount;
    @JsonProperty("schemeName")
    private int schemeCode;
    private String fromDate;
    private String toDate;
}
