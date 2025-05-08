package com.ubm.ubmweb.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ubm.ubmweb.model.FixedAsset;

@Repository
public interface FixedAssetRepository extends JpaRepository<FixedAsset,UUID>, JpaSpecificationExecutor<FixedAsset> {
    @Query("SELECT fa FROM FixedAsset fa WHERE fa.company.id =:companyId AND " +
           "(:amortise IS NULL OR fa.amortise =:amortise) AND " +
           "(:name IS NULL OR fa.name LIKE %:name%) AND " +
           "(:purchaseDate IS NULL OR fa.purchaseDate =:purchaseDate) AND " +
           "(:counterpartyIds IS NULL OR fa.counterparty.id IN :counterpartyIds) AND " +
           "(:legalEntityIds IS NULL OR fa.legalEntity.id IN :legalEntityIds) AND " +
           "(:includeVat IS NULL OR fa.includeVat =:includeVat)")
    List<FixedAsset> findFixedAssets(@Param("companyId") UUID companyId,
                                     @Param("amortise") Boolean amortise,
                                     @Param("name") String name,
                                     @Param("purchaseDate") LocalDate purchaseDate,
                                     @Param("counterpartyIds") List<UUID> counterpartyIds,
                                     @Param("legalEntityIds") List<UUID> legalEntityIds,
                                     @Param("includeVat") Boolean includeVat);


    @Modifying
    @Query("DELETE FROM FixedAsset fa WHERE fa.asset.id =:assetId")
    void deleteByAssetId(@Param("assetId") UUID assetId);

    @Query("SELECT fa FROM FixedAsset fa WHERE fa.asset.id =:assetId")
    List<FixedAsset> findByAssetId(@Param("assetId") UUID assetId);

    @Query("SELECT fa FROM FixedAsset fa WHERE fa.company.id =:companyId")
    List<FixedAsset> findFixedAssetsByCompanyId(@Param("companyId") UUID companyId);
}
