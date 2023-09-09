package com.invest.indices.controller;

import com.invest.indices.domain.model.NiftyFiftyEntity;
import com.invest.indices.service.NiftyFiftyService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/Nifty50")
public class NiftyFiftyController {

    private final NiftyFiftyService niftyFiftyService;

    public NiftyFiftyController(NiftyFiftyService niftyFiftyService) {
        this.niftyFiftyService = niftyFiftyService;
    }

    @GetMapping("/all")
    List<NiftyFiftyEntity> findAllEntries(){
        return niftyFiftyService.getAll();
    }

    @GetMapping("/historicalReturns")
    Double historicalReturns(@RequestParam int invAmount){
        List<NiftyFiftyEntity> niftyFiftyEntities = findAllEntries();
        return niftyFiftyService.calculateReturn(niftyFiftyEntities,invAmount);
    }

    @GetMapping("/onDates")
    Double timeBasedReturns(
            @RequestParam int invAmount,
            @RequestParam("inceptionDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate inceptionDate,
            @RequestParam("redemptionDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate redemptionDate
            ){
        List<NiftyFiftyEntity> niftyFiftyEntities = findAllEntries();
        return niftyFiftyService.calculateReturnFor(niftyFiftyEntities,invAmount,inceptionDate,redemptionDate);
    }

    // DataBase utility end points
    @DeleteMapping("/cleanData")
    void filterData(){
        List<NiftyFiftyEntity> niftyFiftyEntities = findAllEntries();
        niftyFiftyService.cleanData(niftyFiftyEntities);
    }
}
