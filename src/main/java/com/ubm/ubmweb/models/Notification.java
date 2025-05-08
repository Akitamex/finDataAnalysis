package com.ubm.ubmweb.models;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "notification", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<UserNotificationRelationship> userNotifications = new HashSet<>();

    @Column(nullable = false)
    private String Message;

    @Column(nullable = false)
    private String deliveryMethod;

    @Column
    private String link;

    public void addUserNotification(UserNotificationRelationship userNotification) {
        userNotifications.add(userNotification);
        userNotification.setNotification(this);
    }

    public void removeUserNotification(UserNotificationRelationship userNotification) {
        userNotifications.remove(userNotification);
        userNotification.setNotification(null);
    }
}
