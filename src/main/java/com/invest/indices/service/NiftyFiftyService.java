package com.invest.indices.service;

import com.invest.indices.domain.model.NiftyFiftyEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

public interface NiftyFiftyService {

    List<NiftyFiftyEntity> getAll();

    Double calculateReturn(List<NiftyFiftyEntity> niftyFiftyEntities, int invAmount);

    Double calculateReturnFor(
            List<NiftyFiftyEntity> niftyFiftyEntities,
            int invAmount,
            LocalDate inceptionDate,
            LocalDate redemptionDate
    );

    void cleanData(List<NiftyFiftyEntity> niftyFiftyEntities);
    }
