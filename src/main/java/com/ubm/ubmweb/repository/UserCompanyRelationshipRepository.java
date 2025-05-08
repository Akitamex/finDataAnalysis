package com.ubm.ubmweb.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ubm.ubmweb.compositeKey.UserCompanyId;
import com.ubm.ubmweb.model.Company;
import com.ubm.ubmweb.model.UserCompanyRelationship;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserCompanyRelationshipRepository extends JpaRepository<UserCompanyRelationship, UserCompanyId> {
    boolean existsById(UserCompanyId id);

    List<UserCompanyRelationship> findByUserId(UUID userId);

    List<UserCompanyRelationship> findByRole(String role);

    List<UserCompanyRelationship> findByCompany(Company company);

    @Modifying
    @Query("DELETE FROM UserCompanyRelationship u WHERE u.id.companyId =:companyId")
    void deleteAllByCompanyId(@Param("companyId") UUID companyId);

    // @Modifying
    @Query("SELECT u FROM UserCompanyRelationship u WHERE u.role =:role AND u.id.companyId =:companyId")
    List<UserCompanyRelationship> findByRoleAndCompanyId(@Param("role") String role, @Param("companyId") UUID companyId);
}