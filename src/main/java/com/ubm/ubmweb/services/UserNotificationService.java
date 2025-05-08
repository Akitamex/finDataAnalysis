package com.ubm.ubmweb.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ubm.ubmweb.models.User;
import com.ubm.ubmweb.models.UserNotificationRelationship;
import com.ubm.ubmweb.models.Notification;

import com.ubm.ubmweb.repository.NotificationRepository;
import com.ubm.ubmweb.repository.UserNotificationRelationshipRepository;
import com.ubm.ubmweb.repository.UserRepository;

import com.ubm.ubmweb.compositeKey.UserNotificationId;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserNotificationService {
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final UserNotificationRelationshipRepository userNotificationRelationshipRepository;

    @Transactional(readOnly = true)
    public List<Notification> findAll(Long userId) {
        List<Notification> notifications = new ArrayList<>();
        List<UserNotificationRelationship> list = userNotificationRelationshipRepository.findByUserId(userId);
        for (UserNotificationRelationship unr: list) {
            notifications.add(unr.getNotification());
        }
        return notifications;
    }

    @Transactional
    public String delete(Long userId, Long notificationId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new IllegalArgumentException("Notification not found with id: " + notificationId));
        if (!userNotificationRelationshipRepository.existsById(new UserNotificationId(userId, notificationId))) throw new IllegalArgumentException("User: " + userId + " does not have notification: " + notificationId);
        
        userNotificationRelationshipRepository.customDeleteById(notificationId, userId);
        return user.getFirstName() + " " + user.getLastName() + " deleted notification with id " + notification.getId();
    }
}
