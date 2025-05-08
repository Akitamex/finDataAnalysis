package com.ubm.ubmweb.repository;

import com.ubm.ubmweb.graphql.dto.DateRangeInput;
import com.ubm.ubmweb.model.Item;
import com.ubm.ubmweb.model.ItemHistory;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemHistoryRepository extends JpaRepository<ItemHistory, UUID> , JpaSpecificationExecutor<ItemHistory> {

    List<ItemHistory> findByItem(Item item);

    @Query("SELECT ih FROM ItemHistory ih WHERE ih.item = :item AND ih.date <= :date ORDER BY ih.date ASC")
    List<ItemHistory> findByItemAndDateLessThanEqualOrderByDateAsc(Item item, LocalDate date);

    @Query("SELECT ih FROM ItemHistory ih WHERE ih.company.id =:companyId AND " +
        "ih.item.id = :itemId AND " +
        "(:counterpartyIds IS NULL OR ih.counterparty.id IN :counterpartyIds) AND " +
        "(:legalEntityIds IS NULL OR ih.legalEntity.id IN :legalEntityIds) AND " +
        "(:isIncoming IS NULL OR ih.isIncoming =:isIncoming) AND " +
        "(:#{#dateRange.startDate} IS NULL OR ih.date >= :#{#dateRange.startDate}) AND " +
        "(:#{#dateRange.endDate} IS NULL OR ih.date <= :#{#dateRange.endDate})")
    List<ItemHistory> findItemHistories(@Param("companyId") UUID companyId,
                                    @Param("itemId") UUID itemId,
                                    @Param("dateRange") DateRangeInput dateRange,
                                    @Param("counterpartyIds") List<UUID> counterpartyIds,
                                    @Param("legalEntityIds") List<UUID> legalEntityIds,
                                    @Param("isIncoming") Boolean isIncoming);


    @Query("SELECT ih FROM ItemHistory ih WHERE ih.obligation.id = :obligationId")
    List<ItemHistory> findByObligationId(UUID obligationId);
}
