package com.invest.indices.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "annual_return_entity")
/*
 uniqueConstraints = @UniqueConstraint(columnNames = {"scheme_code", "year"})
 Unique Constraint not required as these are calculated on calendar year dates
 */
public class AnnualReturnEntity {
    @Id
    private UUID uuid;
    private int year;
    @Column(name = "scheme_name")
    private String schemeName;
    @Column(name = "scheme_code")
    private int schemeCode;
    @Column(name = "annual_return")
    private Double annualReturn;
}
