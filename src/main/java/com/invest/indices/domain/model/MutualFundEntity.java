package com.invest.indices.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Column;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "mutual_funds", uniqueConstraints = @UniqueConstraint(columnNames = {"scheme_code", "date"}))
public class MutualFundEntity {
    @Id
    private UUID uuid;
    private String date;
    private double nav;
    @Column(name = "scheme_name")
    private String schemeName;
    @Column(name = "scheme_code")
    private int schemeCode;
    @Column(name = "scheme_category")
    private String schemeCategory;
    @Column(name = "scheme_type")
    private String schemeType;
    @Column(name = "fund_house")
    private String funHouse;
}
