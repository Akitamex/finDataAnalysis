package com.ubm.ubmweb.rest;

import com.ubm.ubmweb.dto.UserDto;
import com.ubm.ubmweb.model.User;
import com.ubm.ubmweb.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.ConstraintViolationException;

@RestController
@RequestMapping(value = "/api/auth/")
@RequiredArgsConstructor
public class RegistrationRestController {

    private final UserService userService;


    @PostMapping("register")
    public ResponseEntity<?> register(@RequestBody UserDto userDto) {
        try {
            if (userDto.getPassword() == null || userDto.getPassword().isEmpty()) {
                throw new IllegalArgumentException("Password cannot be null or empty");
            }
            User user = userDto.toUser();
            userService.create(user);
            UserDto result = UserDto.fromUser(user);
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch (IllegalArgumentException | ConstraintViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
        
    }
}
