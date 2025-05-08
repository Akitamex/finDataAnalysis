package com.ubm.ubmweb.service;

import com.postmarkapp.postmark.client.exception.PostmarkException;
import com.ubm.ubmweb.model.Notification;
import com.ubm.ubmweb.model.User;
import com.ubm.ubmweb.model.UserCompanyRelationship;
import com.ubm.ubmweb.model.UserNotificationRelationship;
import com.ubm.ubmweb.repository.NotificationRepository;
import com.ubm.ubmweb.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.ubm.ubmweb.repository.UserNotificationRelationshipRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


import com.postmarkapp.postmark.client.exception.PostmarkException;
import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final UserNotificationRelationshipRepository userNotificationRelationshipRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final UserCompanyService userCompanyService;
    private final EmailService emailService;

    private Long parseLong(Object obj) {
        Long result = 0L;
        if (obj instanceof Number) {
            result = ((Number) obj).longValue();
        } else if (obj instanceof String) {
            try {
                result = Long.valueOf((String) obj);
            } catch (NumberFormatException e) {
                System.err.println("Invalid number format in string: " + obj);
            }
        }
        return result;
    }

    private UUID parseUUID(Object obj) {
        UUID result = null;
        result = (UUID) obj;
        return result;
    }

    private List<User> parseUserIds(Map<String, Object> request) {
        List<User> userList = new ArrayList<>();
        Object usersObj = request.get("users");

        if (usersObj instanceof List) {
            List<?> tempUsersList = (List<?>) usersObj;
            for (Object userIdObj : tempUsersList) {
                try {
                    UUID userId = parseUUID(userIdObj);
                    User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userIdObj));
                    userList.add(user);
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid user ID format: " + userIdObj);
                }
            }
        } else if (usersObj instanceof String) {
            if (((String) usersObj).toLowerCase().equals("all")) {
                userList = userRepository.findAll();
            } else {
                try {
                    UUID userId = parseUUID(usersObj);
                    User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found with id: " + usersObj));
                    userList.add(user);
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid user ID format: " + usersObj);
                }
            }            
        }

        return userList;
    }

    @Transactional(readOnly = true)
    public List<Notification> getAllNotifications() {
        List<Notification> notifications = notificationRepository.findAll();
        return notifications;
    }

    @Transactional
    public Notification createNotification(Map<String, Object> request) throws IOException, PostmarkException {

        String message = (String) request.get("message");
        String link = (String) request.get("link");
        String deliveryMethod = ((String) request.get("deliveryMethod")).toLowerCase();
        
        Notification notification = new Notification();
        notification.setLink(link);
        notification.setMessage(message);
        notification.setDeliveryMethod(deliveryMethod);


        List<User> userList = parseUserIds(request);
        if (userList.isEmpty()) {
            Object companiesObj = request.get("companies");
            if (companiesObj == null) throw new IllegalArgumentException("Notification must have a recipient. The request did not have appropriate userIds nor companies");
            List<UUID> companyIds = new ArrayList<>();
            if (companiesObj instanceof List) {
                List<?> tempCompaniesList = (List<?>) companiesObj;
                for (Object companyIdObj : tempCompaniesList) {
                    try {
                        UUID id = parseUUID(companyIdObj);
                        companyIds.add(id);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Invalid company ID format: " + companyIdObj);
                    }
                }
            } else {
                UUID id = parseUUID(companiesObj);
                companyIds.add(id);
            }

            Object rolesObj = request.get("roles");
            if (rolesObj == null) throw new IllegalArgumentException("Notification must have a recipient. The request did not have appropriate userIds nor roles");
            List<String> companyRoles = new ArrayList<>();
            if (rolesObj instanceof List) {
                List<String> rolesList = (List<String>) rolesObj;
                companyRoles = new ArrayList<>(rolesList);
            } else if (rolesObj instanceof String) {
                companyRoles.add((String) rolesObj);
            }
            
            
            for (String companyRole: companyRoles) {
                for (UUID id: companyIds) {
                    List<UserCompanyRelationship> userCompanyRelationships = userCompanyService.findByRoleAndCompanyId(companyRole.toLowerCase(), id);
                    for (UserCompanyRelationship userCompanyRelationship: userCompanyRelationships) {
                        userList.add(userCompanyRelationship.getUser());
                    }
                }
            }
        }
           
        if (userList.isEmpty()) throw new IllegalArgumentException("Users satisfying the requested conditions haven't been found");

        notificationRepository.save(notification);

        /*  Email notification
        if (deliveryMethod.equals("both") || deliveryMethod.equals("email")) {
            for (Long userId: usersList) {
                String email = userService.findById(userId).getEmail();
                emailService.sendEmail(email, "subject", message);
            }
        }
        */
        if (deliveryMethod.equals("both") || deliveryMethod.equals("app")) {
            
            List<UserNotificationRelationship> relationships = new ArrayList<>();
            for (User user : userList) {
                UserNotificationRelationship relationship = new UserNotificationRelationship(user, notification);
                relationships.add(relationship);
            }
            
            userNotificationRelationshipRepository.saveAll(relationships);
        }


        return notification;
    }

    @Transactional
    public Notification deleteNotification(UUID id) {
        Notification notification = notificationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Notification not found with id: " + id));
        userNotificationRelationshipRepository.deleteAllByNotificationId(id);
        notificationRepository.deleteNotificationById(id);
        return notification;
    }
}

