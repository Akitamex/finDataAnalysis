package com.ubm.ubmweb.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ubm.ubmweb.model.Asset;

@Repository
public interface AssetRepository extends JpaRepository<Asset,UUID>, JpaSpecificationExecutor<Asset> {
    @Query("SELECT a FROM Asset a WHERE a.company.id =:companyId AND " +
           "(:name IS NULL OR a.name LIKE %:name%) AND " +
           "(:quantity IS NULL OR a.quantity =:quantity) AND " +
           "(:remainingCost IS NULL OR a.remainingCost =:remainingCost) AND " +
           "(:wholeCost IS NULL OR a.wholeCost =:wholeCost)")
    List<Asset> findAssets(@Param("companyId") UUID companyId,
                           @Param("name") String name,
                           @Param("quantity") Long quantity,
                           @Param("remainingCost") BigDecimal remainingCost,
                           @Param("wholeCost") BigDecimal wholeCost);
}
