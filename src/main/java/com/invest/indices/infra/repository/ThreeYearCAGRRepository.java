package com.invest.indices.infra.repository;

import com.invest.indices.domain.model.AnnualReturnEntity;
import com.invest.indices.domain.model.ThreeYearCAGR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface ThreeYearCAGRRepository extends JpaRepository<ThreeYearCAGR, UUID> {

    @Transactional
    List<AnnualReturnEntity> findBySchemeCode(Integer id);
}
