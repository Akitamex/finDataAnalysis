package com.ubm.ubmweb.rest;

import com.postmarkapp.postmark.client.exception.PostmarkException;
import com.ubm.ubmweb.model.Role;
import com.ubm.ubmweb.model.User;
import com.ubm.ubmweb.security.jwt.JwtTokenProvider;
import com.ubm.ubmweb.service.NotificationService;
import com.ubm.ubmweb.service.UserNotificationService;
import com.ubm.ubmweb.service.UserService;

import jakarta.servlet.ServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications/")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    private final UserService userService;

    private final UserNotificationService userNotificationService;
    
    private final JwtTokenProvider jwtTokenProvider;

    private UUID userIdFromRequest(ServletRequest servletRequest) {
        String token = jwtTokenProvider.resolveToken((jakarta.servlet.http.HttpServletRequest) servletRequest);
        if (token==null) throw new IllegalArgumentException("No Token");
        token = jwtTokenProvider.decryptToken(token);
        if(token==null|| !jwtTokenProvider.validateToken(token)){
            throw new IllegalArgumentException("Invalid or expired token");
        }
        UUID userId = jwtTokenProvider.getUserId(token);
        return userId;
    }

    private boolean isAuthorized(UUID userId) {
        User user = userService.findById(userId);
        List<Role> roles = user.getRoles();
        
        for (Role role: roles) {
            if (role.getName().equals("ROLE_ADMIN")) {
                return true;
            }
        }
        return false;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllNotifications(ServletRequest servletRequest) {
        UUID userId = userIdFromRequest(servletRequest);

        if (!isAuthorized(userId)) {
            return ResponseEntity.badRequest().body("User is not authorized");
        }        
        
        ResponseEntity<?> response = ResponseEntity.ok("");

        try {
            response = ResponseEntity.ok(notificationService.getAllNotifications());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        return response;
                
    }

    @GetMapping
    public ResponseEntity<?> getUserNotificationsById(ServletRequest servletRequest) {
        UUID userId = userIdFromRequest(servletRequest);
        return ResponseEntity.ok(userNotificationService.findAll(userId));
    }

    @PostMapping
    public ResponseEntity<?> createNotification(ServletRequest servletRequest, @RequestBody Map<String, Object> request)  throws IOException, PostmarkException {
        UUID userId = userIdFromRequest(servletRequest);
        
        if (!isAuthorized(userId)) {
            return ResponseEntity.badRequest().body("User is not authorized");
        }        
        ResponseEntity<?> response = ResponseEntity.ok("");

        try {
            response = ResponseEntity.ok(notificationService.createNotification(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        
        return response;
    }

    @DeleteMapping
    public ResponseEntity<?> deleteNotification(ServletRequest servletRequest, @RequestBody Map<String, Object> request) {
        UUID userId = userIdFromRequest(servletRequest);

        if (!isAuthorized(userId)) {
            return ResponseEntity.badRequest().body("User is not authorized");
        }    
        
        UUID notificationId = (UUID) request.get("id");

        ResponseEntity<?> response = ResponseEntity.ok("");

        try {
            response = ResponseEntity.ok(notificationService.deleteNotification(notificationId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        return response;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserRelationship(@PathVariable("id") UUID notificationId, ServletRequest servletRequest) {
        UUID userId = userIdFromRequest(servletRequest);

        
        ResponseEntity<?> response = ResponseEntity.ok("");

        try {
            response = ResponseEntity.ok(userNotificationService.delete(userId, notificationId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        return response;
    }
}
