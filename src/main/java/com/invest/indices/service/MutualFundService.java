package com.invest.indices.service;

import com.invest.indices.domain.model.MutualFund;
import com.invest.indices.domain.model.MutualFundEntity;
import com.invest.indices.domain.model.ReturnInputs;
import com.invest.indices.domain.model.SchemeNameAndCodeMapEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface MutualFundService {

    List<MutualFundEntity> getAll();

    Double calculateReturn(ReturnInputs returnInputs);

    List<SchemeNameAndCodeMapEntity> fuzzySearchMutualFund(String schemeName);

    ResponseEntity<MutualFund> getLatestNav(Integer id);

    ResponseEntity<List<MutualFundEntity>> getHistoricalNav(Integer id);

    void saveAllMutualFunds();
}


