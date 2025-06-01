package com.invest.indices.service.impl;

import com.invest.indices.common.Utils;
import com.invest.indices.domain.model.MutualFundEntity;
import com.invest.indices.domain.model.SchemeNameAndCodeMapEntity;
import com.invest.indices.infra.repository.MutualFundRepository;
import com.invest.indices.infra.repository.SchemeNameAndCodeMapRepository;
import com.invest.indices.service.CodeNameAndSchemeService;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CodeNameAndSchemeServiceImpl implements CodeNameAndSchemeService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final SchemeNameAndCodeMapRepository schemeNameAndCodeMapRepository;
    private final MutualFundRepository mutualFundRepository;

    public CodeNameAndSchemeServiceImpl(SchemeNameAndCodeMapRepository schemeNameAndCodeMapRepository, MutualFundRepository mutualFundRepository) {
        this.schemeNameAndCodeMapRepository = schemeNameAndCodeMapRepository;
        this.mutualFundRepository = mutualFundRepository;
    }

    @Override
    public void saveSchemeCodeAndName() {
        schemeNameAndCodeMapRepository.deleteAll();
        String url = "https://api.mfapi.in/mf";
        SchemeNameAndCodeMapEntity[] schemeNameAndCodeMapEntities = restTemplate.getForObject(url, SchemeNameAndCodeMapEntity[].class);
        if (schemeNameAndCodeMapEntities != null && schemeNameAndCodeMapEntities.length != 0) {
            List<SchemeNameAndCodeMapEntity> filteredEntities = Arrays.stream(schemeNameAndCodeMapEntities).filter(
                    schemeNameAndCodeMapEntity -> (schemeNameAndCodeMapEntity.getSchemeName().toLowerCase().contains("direct"))
                            && schemeNameAndCodeMapEntity.getSchemeName().toLowerCase().contains("growth")).collect(Collectors.toList());
            schemeNameAndCodeMapRepository.saveAll(filteredEntities);
        }
    }

    @Override
    @Retryable(value = {DataAccessException.class}, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public void updateSchemeNameAndSchemeCodeTable() {
        List<SchemeNameAndCodeMapEntity> schemeNameAndCodeMapEntityList = mutualFundRepository.findAll()
                .stream().map(mutualFundEntity -> new SchemeNameAndCodeMapEntity(mutualFundEntity.getSchemeCode(), mutualFundEntity.getSchemeName())).toList();
        List<SchemeNameAndCodeMapEntity> tempSchemeNameAndCodeMapEntityList = new ArrayList<>();
        schemeNameAndCodeMapRepository.deleteAll();
        int batch = 100;
        for (int i = 0; i < schemeNameAndCodeMapEntityList.size(); i++) {
            tempSchemeNameAndCodeMapEntityList.add(schemeNameAndCodeMapEntityList.get(i));

            if (tempSchemeNameAndCodeMapEntityList.size() == batch || i == schemeNameAndCodeMapEntityList.size() - 1) {
                schemeNameAndCodeMapRepository.saveAll(tempSchemeNameAndCodeMapEntityList);
                tempSchemeNameAndCodeMapEntityList.clear();
                System.out.println("Saved a batch of " + batch + " records");
            }
        }
    }

    @Override
    @Retryable(value = {DataAccessException.class}, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public void updateInceptionAndEndDate() {
        List<SchemeNameAndCodeMapEntity> batch = new ArrayList<>();
        int batchSize = 100;

        List<Integer> listOfSchemeCodes = schemeNameAndCodeMapRepository.findAll().stream().map(SchemeNameAndCodeMapEntity::getSchemeCode).toList();
        for (Integer schemeCode: listOfSchemeCodes) {
            List<MutualFundEntity> mutualFundEntityList =
                mutualFundRepository.findBySchemeCode(schemeCode).stream()
                        .sorted(Comparator.comparing(mutualFundEntity -> Utils.parseDate(mutualFundEntity.getDate()))).toList();

            if (mutualFundEntityList.isEmpty()) {
                continue;
            }

            String inceptionDate = mutualFundEntityList.get(0).getDate();
            String lastDate = mutualFundEntityList.get(mutualFundEntityList.size() - 1).getDate();

            SchemeNameAndCodeMapEntity schemeNameAndCodeMapEntity = schemeNameAndCodeMapRepository.findById(schemeCode)
                    .orElseThrow(() -> new IllegalStateException("Scheme not found: " + schemeCode));
            schemeNameAndCodeMapEntity.setInceptionDate(inceptionDate);
            schemeNameAndCodeMapEntity.setLastDate(lastDate);
            batch.add(schemeNameAndCodeMapEntity);

            if (batch.size() == batchSize) {
                schemeNameAndCodeMapRepository.saveAll(batch);
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            schemeNameAndCodeMapRepository.saveAll(batch);
        }
    }
}
