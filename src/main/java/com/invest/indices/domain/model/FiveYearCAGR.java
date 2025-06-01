package com.invest.indices.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "five_year_CAGR", uniqueConstraints = @UniqueConstraint(columnNames = {"scheme_code", "date"}))
public class FiveYearCAGR {
    @Id
    private UUID uuid;
    private String date;
    @Column(name = "scheme_name")
    private String schemeName;
    @Column(name = "scheme_code")
    private int schemeCode;
    @Column(name = "five_year_CAGR")
    private Double fiveYearCAGR;
}
