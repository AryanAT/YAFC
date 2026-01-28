package com.invest.indices.controller;

import com.invest.indices.service.CodeNameAndSchemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/schemeNameAndCode")
public class SchemeCodeAndNameController {

    @Autowired
    private CodeNameAndSchemeService codeNameAndSchemeService;

    // Step 1
    @GetMapping("/save")
    public ResponseEntity<String> saveSchemeCodeAndName() {
        codeNameAndSchemeService.saveSchemeCodeAndName();
        return ResponseEntity.ok("SchemeCodeAndNameStored");
    }

    // Step 3
    @GetMapping("/updateAllSchemeNames")
    public ResponseEntity<String> updateAllSchemeNames() {
        codeNameAndSchemeService.updateSchemeNameAndSchemeCodeTable();
        return ResponseEntity.ok("UPDATED ALL SCHEME NAMES USING MUTUAL FUNDS TABLE");
    }

    // Step 4
    @PostMapping("/updateInceptionAndEndDate")
    public ResponseEntity<String> updateInceptionAndEndDate() {
        codeNameAndSchemeService.updateInceptionAndEndDate();
        return ResponseEntity.ok("UPDATED ALL Inception Date and End Date USING MUTUAL FUNDS TABLE");
    }
}
