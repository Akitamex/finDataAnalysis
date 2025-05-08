package com.ubm.ubmweb.compositeKey;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class UserNotificationId implements Serializable {
    
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "notification_id")
    private UUID notificationId;
    
    public UserNotificationId() {}

    public UserNotificationId(UUID userId, UUID notificationId) {
        this.notificationId = notificationId;
        this.userId = userId;
    }

    

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserNotificationId)) return false;
        UserNotificationId that = (UserNotificationId) o;
        return Objects.equals(notificationId, that.notificationId) &&
               Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(notificationId, userId);
    }

    public UUID getCompanyId() {
        return notificationId;
    }

    public void setCompanyId(UUID notificationId) {
        this.notificationId = notificationId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}