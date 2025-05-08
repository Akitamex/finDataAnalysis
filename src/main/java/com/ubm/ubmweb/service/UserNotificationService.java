package com.ubm.ubmweb.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ubm.ubmweb.repository.NotificationRepository;
import com.ubm.ubmweb.repository.UserNotificationRelationshipRepository;
import com.ubm.ubmweb.repository.UserRepository;

import com.ubm.ubmweb.compositeKey.UserNotificationId;
import com.ubm.ubmweb.model.Notification;
import com.ubm.ubmweb.model.User;
import com.ubm.ubmweb.model.UserNotificationRelationship;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserNotificationService {
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final UserNotificationRelationshipRepository userNotificationRelationshipRepository;

    @Transactional(readOnly = true)
    public List<Notification> findAll(UUID userId) {
        List<Notification> notifications = new ArrayList<>();
        List<UserNotificationRelationship> list = userNotificationRelationshipRepository.findByUserId(userId);
        for (UserNotificationRelationship unr: list) {
            notifications.add(unr.getNotification());
        }
        return notifications;
    }

    @Transactional
    public String delete(UUID userId, UUID notificationId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new IllegalArgumentException("Notification not found with id: " + notificationId));
        if (!userNotificationRelationshipRepository.existsById(new UserNotificationId(userId, notificationId))) throw new IllegalArgumentException("User: " + userId + " does not have notification: " + notificationId);
        
        userNotificationRelationshipRepository.customDeleteById(notificationId, userId);
        return user.getFirstName() + " " + user.getLastName() + " deleted notification with id " + notification.getId();
    }
}
