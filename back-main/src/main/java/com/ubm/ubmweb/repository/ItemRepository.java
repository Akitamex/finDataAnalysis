package com.ubm.ubmweb.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ubm.ubmweb.model.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, UUID> , JpaSpecificationExecutor<Item> {
    @Query("SELECT i FROM Item i WHERE i.company.id =:companyId AND " +
       "(:name IS NULL OR i.name LIKE %:name%) AND " +
       "(:types IS NULL OR i.type IN :types) AND " +
       "(:description IS NULL OR i.description LIKE %:description%)")
List<Item> findItems(@Param("companyId") UUID companyId,
                     @Param("name") String name,
                     @Param("types") List<String> types,
                     @Param("description") String description);
}
