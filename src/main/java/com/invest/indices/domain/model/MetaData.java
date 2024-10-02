package com.invest.indices.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MetaData {
    @JsonProperty("fund_house")
    private String fundHouse;
    @JsonProperty("scheme_type")
    private String schemeType;
    @JsonProperty("scheme_category")
    private String schemeCategory;
    @JsonProperty("scheme_code")
    private Integer schemeCode;
    @JsonProperty("scheme_name")
    private String schemeName;
}
