package com.invest.indices.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "scheme_name_code_map")
public class SchemeNameAndCodeMapEntity {
    @Id
    private Integer schemeCode;
    private String schemeName;
    private String inceptionDate;
    private String lastDate;

    public SchemeNameAndCodeMapEntity(int schemeCode, String schemeName) {
        this.schemeCode = schemeCode;
        this.schemeName = schemeName;
    }
}

