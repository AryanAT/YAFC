package com.invest.indices.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/initialize")
public class InitController {

    private final MutualFundsController mutualFundsController;
    private final SchemeCodeAndNameController schemeCodeAndNameController;

    public InitController(
            MutualFundsController mutualFundsController,
            SchemeCodeAndNameController schemeCodeAndNameController
    ) {
        this.mutualFundsController = mutualFundsController;
        this.schemeCodeAndNameController = schemeCodeAndNameController;
    }

    @PostMapping(value = "/data")
    public ResponseEntity<?> populateData() {
//        schemeCodeAndNameController.saveSchemeCodeAndName();
//        mutualFundsController.saveAllMutualFunds();
//        schemeCodeAndNameController.updateAllSchemeNames();
//        schemeCodeAndNameController.updateInceptionAndEndDate();
        mutualFundsController.annualGrowth();
        mutualFundsController.cagrGrowth();
        return ResponseEntity.ok("All Tables are Populated");
    }
}
