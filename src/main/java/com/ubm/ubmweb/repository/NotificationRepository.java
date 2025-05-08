package com.ubm.ubmweb.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ubm.ubmweb.model.Notification;
public interface NotificationRepository extends JpaRepository<Notification,UUID> {

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.id =:id")
    void deleteNotificationById(@Param("id") UUID id);

}
