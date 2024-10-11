package com.invest.indices.infra.repository;

import com.invest.indices.domain.model.FiveYearCAGR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FiveYearCAGRRepository extends JpaRepository<FiveYearCAGR, UUID> {

    @Transactional
    List<FiveYearCAGR> findBySchemeCode(Integer id);

    @Query(value = "SELECT * FROM five_year_CAGR f "
            + "WHERE f.scheme_code = :schemeCode "
            + "AND to_date(f.date, 'DD-MM-YYYY') <= to_date(:date, 'DD-MM-YYYY') "
            + "ORDER BY to_date(f.date, 'DD-MM-YYYY') DESC "
            + "LIMIT 1",
            nativeQuery = true)
    Optional<FiveYearCAGR> findLatestBySchemeCodeAndDate(@Param("schemeCode") int schemeCode,
                                                         @Param("date") String date);
}
