package com.invest.indices.infra.repository;

import com.invest.indices.domain.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDetailsRepository extends JpaRepository<Users, Integer> {
    Users findByUsername(String username);
}
