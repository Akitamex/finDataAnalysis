package com.ubm.ubmweb.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ubm.ubmweb.compositeKey.UserNotificationId;
import com.ubm.ubmweb.model.UserNotificationRelationship;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserNotificationRelationshipRepository extends JpaRepository<UserNotificationRelationship, UserNotificationId> {
    boolean existsById(UserNotificationId id);

    List<UserNotificationRelationship> findByUserId(UUID userId);

    
    @Modifying
    @Query("DELETE FROM UserNotificationRelationship u WHERE u.id.notificationId =:notificationId")
    void deleteAllByNotificationId(UUID notificationId);

    @Modifying
    @Query("DELETE FROM UserNotificationRelationship u WHERE u.id.userId =:userId")
    void deleteAllByUserId(UUID userId);

    @Modifying
    @Query("DELETE FROM UserNotificationRelationship u WHERE u.id.notificationId =:notificationId AND u.id.userId =:userId")
    void customDeleteById(@Param("notificationId") UUID notificationId, @Param("userId") UUID userId);
}