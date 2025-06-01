package com.invest.indices.controller;

import com.invest.indices.domain.model.MutualFundEntity;
import com.invest.indices.domain.model.PortfolioReport;
import com.invest.indices.domain.model.ReturnInputs;
import com.invest.indices.domain.model.SchemeNameAndCodeMapEntity;
import com.invest.indices.domain.model.MutualFund;
import com.invest.indices.domain.model.SimpleSIPOutput;
import com.invest.indices.domain.model.SimpleSIPInput;
import com.invest.indices.service.MutualFundService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/mutual-funds")
public class MutualFundsController {

    private final MutualFundService mutualFundService;

    public MutualFundsController(MutualFundService mutualFundService) {
        this.mutualFundService = mutualFundService;
    }

    @GetMapping("/all")
    List<MutualFundEntity> findAllEntries() {
        return mutualFundService.getAll();
    }

    @PostMapping("/historicalReturns")
    ResponseEntity<PortfolioReport> historicalReturns(@RequestBody List<ReturnInputs> returnInputs) {
        return ResponseEntity.ok(mutualFundService.calculateReturnForListOfMutualFunds(returnInputs));
    }

    @GetMapping("/search/{mutualFundName}")
    public ResponseEntity<List<SchemeNameAndCodeMapEntity>> searchMutualFund(@PathVariable String mutualFundName) {
        return ResponseEntity.ok(mutualFundService.fuzzySearchMutualFund(mutualFundName));
    }

    @GetMapping("/{id}/latest")
    public ResponseEntity<MutualFund> getLatestNav(@PathVariable Integer id) {
        return mutualFundService.getLatestNav(id);
    }


    @GetMapping("/{id}")
    public ResponseEntity<List<MutualFundEntity>> getHistoricalNav(@PathVariable Integer id) {
        return mutualFundService.getHistoricalNav(id);
    }


    @GetMapping("/saveAll")
    public ResponseEntity<String> saveAllMutualFunds() {
        mutualFundService.saveAllMutualFunds();
        return ResponseEntity.ok("All Mutual funds processed and saved in Database");
    }

    @PostMapping("/simpleSip")
    public ResponseEntity<SimpleSIPOutput> calculateSimpleSip(@RequestBody SimpleSIPInput simpleSIPInput) {
        return ResponseEntity.ok(mutualFundService.getSimpleSip(simpleSIPInput));
    }

    @PostMapping("/updateAnnualReturns")
    public ResponseEntity<String> annualGrowth() {
        mutualFundService.updateAnnualReturn();
        return ResponseEntity.ok("All Mutual funds processed and saved in Database");
    }

    @PostMapping("/updateCAGR")
    public ResponseEntity<String> cagrGrowth() {
        mutualFundService.updateCAGR();
        return ResponseEntity.ok("All Mutual funds processed and saved in Database");
    }
}
