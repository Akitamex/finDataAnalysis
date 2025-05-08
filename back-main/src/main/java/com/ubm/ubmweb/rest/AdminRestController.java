package com.ubm.ubmweb.rest;

import com.ubm.ubmweb.dto.UserDto;
import com.ubm.ubmweb.model.User;
import com.ubm.ubmweb.service.UserService;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/admin/")
@RequiredArgsConstructor
public class AdminRestController {

    private final UserService userService;

    @GetMapping("users/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable(name = "id") UUID userId){
      User user = userService.findById(userId);
      if (user == null){
          return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }

      UserDto result = UserDto.fromUser(user);
      return new ResponseEntity<>(result, HttpStatus.OK);
    }

}