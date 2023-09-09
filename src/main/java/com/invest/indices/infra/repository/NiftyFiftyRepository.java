package com.invest.indices.infra.repository;

import com.invest.indices.domain.model.NiftyFiftyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;

@Repository
public interface NiftyFiftyRepository extends JpaRepository<NiftyFiftyEntity, LocalDate> {
}
