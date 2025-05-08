package com.ubm.ubmweb.rest;

import com.ubm.ubmweb.dto.LoginHistoryDto;
import com.ubm.ubmweb.dto.UserDto;
import com.ubm.ubmweb.model.Role;
import com.ubm.ubmweb.model.User;
import com.ubm.ubmweb.dto.RoleDTO;
import com.ubm.ubmweb.security.jwt.JwtTokenProvider;
import com.ubm.ubmweb.service.LoginHistoryService;
import com.ubm.ubmweb.service.UserService;
import com.ubm.ubmweb.repository.RoleRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;

import jakarta.servlet.ServletRequest;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/users/")

public class UserRestController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final LoginHistoryService loginHistoryService;
    private final RoleRepository roleRepository;

    @GetMapping("/userid")
    public ResponseEntity<?> getUserId(ServletRequest servletRequest){

        String token = jwtTokenProvider.resolveToken((jakarta.servlet.http.HttpServletRequest) servletRequest);
        if (token==null) return ResponseEntity.badRequest().body("No token");
        token = jwtTokenProvider.decryptToken(token);
        if(token==null|| !jwtTokenProvider.validateToken(token)){
            return ResponseEntity.badRequest().body("Invalid or expired token");
        }
        UUID userId = jwtTokenProvider.getUserId(token);

        return new ResponseEntity<>(userId, HttpStatus.OK);
    }

    // @GetMapping("/loginHistory")
    // public ResponseEntity<?> getLoginHistory(Authentication authentication) {
    //     User user = userService.findByUsername(authentication.getName());
    //     List<LoginHistoryDto> loginHistory = loginHistoryService.getLoginHistoryByUser(user);
    //     System.out.println("Returning login history for user: " + user.getUsername());
    //     return ResponseEntity.ok(loginHistory);
    // }


    @PutMapping("/updatePassword")
    public ResponseEntity<?> updatePassword( @RequestBody Map<String, Object> request, Authentication authentication) {
        String currentPassword = (String) request.get("currentPassword");
        String newPassword = (String) request.get("newPassword");

        try {
            userService.updatePassword(authentication.getName(), currentPassword, newPassword);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable(name = "id") UUID userId){
      User user = userService.findById(userId);
      if (user == null){
          return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }

      UserDto result = UserDto.fromUser(user);
      return new ResponseEntity<>(result, HttpStatus.OK);
    }
    

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUserById(@PathVariable(name = "id") UUID userId, @RequestBody UserDto userDto){
      User user = userService.update(userId, userDto.toUser());
      if (user == null){
          return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }

      UserDto result = UserDto.fromUser(user);
      return new ResponseEntity<>(result, HttpStatus.OK);
    }


    // Testing methods to add admin role to user and check roles
    @GetMapping("/roles")
    public ResponseEntity<?> getRoles() {
        List<RoleDTO> result = new ArrayList<RoleDTO>();
        for (Role role: roleRepository.findAll()) {
            result.add(new RoleDTO().fromRole(role));
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/setAdmin")
    public ResponseEntity<?> setAdmin(ServletRequest servletRequest) {
        String token = jwtTokenProvider.resolveToken((jakarta.servlet.http.HttpServletRequest) servletRequest);
        if (token==null) return ResponseEntity.badRequest().body("No token");
        token = jwtTokenProvider.decryptToken(token);
        if(token==null|| !jwtTokenProvider.validateToken(token)){
            return ResponseEntity.badRequest().body("Invalid or expired token");
        }
        UUID userId = jwtTokenProvider.getUserId(token);
        UUID roleId = roleRepository.findByName("ROLE_ADMIN").getId();
        userService.addRole(userId, roleId);
        User user = userService.findById(userId);
        
        UserDto result = UserDto.fromUser(user);
        
        return ResponseEntity.ok(result);
    }

}
