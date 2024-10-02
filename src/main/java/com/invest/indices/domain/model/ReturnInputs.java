package com.invest.indices.domain.model;

import lombok.Data;


@Data
public class ReturnInputs {
    private int invAmount;
    private int schemeCode;
    private String fromDate;
    private String toDate;
}
