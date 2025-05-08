package com.ubm.ubmweb.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ubm.ubmweb.model.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {
    Company findByName(String name);
}
