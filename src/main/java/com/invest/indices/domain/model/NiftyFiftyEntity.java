package com.invest.indices.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "nifty_50")
public class NiftyFiftyEntity {
    @Id
    private LocalDate date;
    private int open;
    private int close;
    private double returns;

}
