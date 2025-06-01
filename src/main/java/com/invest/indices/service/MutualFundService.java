package com.invest.indices.service;

import com.invest.indices.domain.model.MutualFundEntity;
import com.invest.indices.domain.model.PortfolioReport;
import com.invest.indices.domain.model.ReturnInputs;
import com.invest.indices.domain.model.ReturnOutput;
import com.invest.indices.domain.model.SchemeNameAndCodeMapEntity;
import com.invest.indices.domain.model.MutualFund;
import com.invest.indices.domain.model.SimpleSIPInput;
import com.invest.indices.domain.model.SimpleSIPOutput;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface MutualFundService {

    List<MutualFundEntity> getAll();

    ReturnOutput calculateReturn(ReturnInputs returnInputs);

    PortfolioReport calculateReturnForListOfMutualFunds(List<ReturnInputs> returnInputs);

    List<SchemeNameAndCodeMapEntity> fuzzySearchMutualFund(String schemeName);

    ResponseEntity<MutualFund> getLatestNav(Integer id);

    ResponseEntity<List<MutualFundEntity>> getHistoricalNav(Integer id);

    void saveAllMutualFunds();

    void updateAnnualReturn();

    void updateCAGR();

    SimpleSIPOutput getSimpleSip(SimpleSIPInput simpleSIPInput);
}


