package com.ubm.ubmweb.models;

import jakarta.persistence.*;

import com.ubm.ubmweb.compositeKey.UserNotificationId;

@Entity
@Table(name = "user_notification")
public class UserNotificationRelationship {

    @EmbeddedId
    private UserNotificationId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("notificationId")
    @JoinColumn(name = "notification_id")
    private Notification notification;
    
    public UserNotificationRelationship() {}

    public UserNotificationRelationship(User user, Notification notification) {
        this.notification = notification;
        this.user = user;
        this.id = new UserNotificationId(user.getId(), notification.getId());
    }

    public UserNotificationId getId() {
        return this.id;
    }

    public void setId(UserNotificationId id) {
        this.id = id;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Notification getNotification() {
        return this.notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }
}