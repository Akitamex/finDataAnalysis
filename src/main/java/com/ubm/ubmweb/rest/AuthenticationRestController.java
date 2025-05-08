package com.ubm.ubmweb.rest;


import com.ubm.ubmweb.dto.AuthenticationRequestDto;
import com.ubm.ubmweb.model.User;
import com.ubm.ubmweb.security.jwt.JwtTokenProvider;
import com.ubm.ubmweb.service.LoginHistoryService;
import com.ubm.ubmweb.service.UserService;
import com.ubm.ubmweb.util.RequestInfoProvider;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.core.AuthenticationException;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
@RequestMapping(value = "/api/auth/")
@RequiredArgsConstructor
public class AuthenticationRestController {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    private final LoginHistoryService loginHistoryService;

    private final UserService userService;
    
    private final RequestInfoProvider requestInfoProvider;


    @PostMapping("login")
    public ResponseEntity<Map<Object, Object>> login(@RequestBody AuthenticationRequestDto requestDto, HttpServletRequest request) {
    
        try {
            String phone = requestDto.getPhone();
            log.warn("Received phone: {}", phone);
    
            // Authenticate first
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(phone, requestDto.getPassword())
            );
    
            // Check authentication
            if (authentication == null) {
                log.warn("Authentication failed for phone: {}", phone);
                throw new BadCredentialsException("Invalid username or password");
            }
    
            // Authentication successful
            log.warn("Authentication successful for phone: {}", phone);
    
            // Now perform user lookup after successful authentication
            User user = userService.findByPhone(phone);
            if (user == null) {
                log.warn("No user found for phone: {}", phone);
                throw new UsernameNotFoundException("User with phone number: " + phone + " not found");
            }
    
            log.warn("User found: {}", user);
    
            // Generate token after user is found
            String token = jwtTokenProvider.createToken(phone, user.getId(), user.getRoles());
    
            Map<Object, Object> response = new HashMap<>();
            response.put("user", phone);
            response.put("token", token);
    
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            log.error("Authentication failed for phone: {}", requestDto.getPhone(), e);
            Map<Object, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } catch (UsernameNotFoundException e) {
            log.error("User not found for phone: {}", requestDto.getPhone(), e);
            Map<Object, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "User not found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } catch (Exception e) {
            log.error("Unexpected error during login", e);
            Map<Object, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Unexpected error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
}
