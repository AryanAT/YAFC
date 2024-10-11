package com.invest.indices.infra.repository;

import com.invest.indices.domain.model.FiveYearCAGR;
import com.invest.indices.domain.model.ThreeYearCAGR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ThreeYearCAGRRepository extends JpaRepository<ThreeYearCAGR, UUID> {

    @Transactional
    List<ThreeYearCAGR> findBySchemeCode(Integer id);

    @Query(value = "SELECT * FROM three_year_CAGR f "
            + "WHERE f.scheme_code = :schemeCode "
            + "AND to_date(f.date, 'DD-MM-YYYY') <= to_date(:date, 'DD-MM-YYYY') "
            + "ORDER BY to_date(f.date, 'DD-MM-YYYY') DESC "
            + "LIMIT 1",
            nativeQuery = true)
    Optional<ThreeYearCAGR> findLatestBySchemeCodeAndDate(@Param("schemeCode") int schemeCode,
                                                         @Param("date") String date);
}
