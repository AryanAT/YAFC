package com.invest.indices.infra.repository;

import com.invest.indices.domain.model.SchemeNameAndCodeMapEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SchemeNameAndCodeMapRepository extends JpaRepository<SchemeNameAndCodeMapEntity, Integer> {
    List<SchemeNameAndCodeMapEntity> findBySchemeNameContainingIgnoreCase(String schemeName);
}
