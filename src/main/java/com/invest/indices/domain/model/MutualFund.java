package com.invest.indices.domain.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class MutualFund {
    @JsonProperty("meta")
    private MetaData meta;
    @JsonProperty("data")
    private List<PriceData> data;
}
