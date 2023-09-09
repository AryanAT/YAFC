package com.invest.indices.service.impl;

import com.invest.indices.action.CleanData;
import com.invest.indices.domain.model.NiftyFiftyEntity;
import com.invest.indices.infra.repository.NiftyFiftyRepository;
import com.invest.indices.action.CalculateReturns;
import com.invest.indices.service.NiftyFiftyService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class NiftyFiftyServiceImpl implements NiftyFiftyService {

    private final NiftyFiftyRepository niftyFiftyRepository;
    private final CalculateReturns calculateReturns;

    private final CleanData cleanData;


    public NiftyFiftyServiceImpl(
            NiftyFiftyRepository niftyFiftyRepository,
            CalculateReturns calculateReturns,
            CleanData cleanData
    ) {
        this.niftyFiftyRepository = niftyFiftyRepository;
        this.calculateReturns = calculateReturns;
        this.cleanData = cleanData;

    }

    @Override
    public List<NiftyFiftyEntity> getAll() {
        return niftyFiftyRepository.findAll();
    }

    @Override
    public Double calculateReturn(List<NiftyFiftyEntity> niftyFiftyEntities, int invAmount) {
        return calculateReturns.with(niftyFiftyEntities, invAmount);
    }

    @Override
    public Double calculateReturnFor(List<NiftyFiftyEntity> niftyFiftyEntities, int invAmount, LocalDate inceptionDate, LocalDate redemptionDate) {
        return calculateReturns.forTheTime(niftyFiftyEntities, invAmount, inceptionDate, redemptionDate);
    }

    @Override
    public void cleanData(List<NiftyFiftyEntity> niftyFiftyEntities) {
        cleanData.filterFirstDatesOfMonth(niftyFiftyEntities);
    }
}
