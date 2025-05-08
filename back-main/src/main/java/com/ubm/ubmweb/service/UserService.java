package com.ubm.ubmweb.service;

import com.ubm.ubmweb.compositeKey.UserCompanyId;
import com.ubm.ubmweb.exceptions.UnauthorizedAccessException;
import com.ubm.ubmweb.model.Role;
import com.ubm.ubmweb.model.Status;
import com.ubm.ubmweb.model.User;
import com.ubm.ubmweb.repository.RoleRepository;
import com.ubm.ubmweb.repository.UserCompanyRelationshipRepository;
import com.ubm.ubmweb.repository.UserRepository;
import com.ubm.ubmweb.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Date;
import java.util.Set;  // For Set
import java.util.UUID;

import jakarta.validation.ConstraintViolation;  // For ConstraintViolation
import jakarta.validation.Validator;  // For Validator
import jakarta.validation.ConstraintViolationException; 

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Validator validator;
    private final UserCompanyRelationshipRepository userCompanyRelationshipRepository;

    @Transactional
    public User create(User user) {
	Set<ConstraintViolation<User>> violations = validator.validate(user);
	if (!violations.isEmpty()) {
	    throw new ConstraintViolationException(violations);
	}
        Role roleUser = roleRepository.findByName("ROLE_USER");
        if (roleUser == null) {
            roleRepository.save(new Role("ROLE_USER"));
            roleRepository.save(new Role("ROLE_ADMIN"));
            roleUser = roleRepository.findByName("ROLE_USER");
        }
        List<Role> userRoles = new ArrayList<>();
        userRoles.add(roleUser);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRoles(userRoles);
        user.setStatus(Status.ACTIVE);
		Date currentTime = new Date(System.currentTimeMillis());
		user.setCreated(currentTime);
		user.setUpdated(currentTime);

        if (userRepository.findByEmail(user.getEmail()) != null) throw new IllegalArgumentException("User with this email already exists");
        if (userRepository.findByPhone(user.getPhone()) != null) throw new IllegalArgumentException("User with this phone number already exists");
        
        try {
            User registeredUser = userRepository.save(user);

            log.info("IN register - user: {} successfully registered", registeredUser);

            return registeredUser;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create user", e);
        }
    }

    @Transactional
    public void updatePassword(String phone, String currentPassword, String newPassword) {
        User user = userRepository.findByPhone(phone);
        if (user != null && bCryptPasswordEncoder.matches(currentPassword, user.getPassword())) {
            user.setPassword(bCryptPasswordEncoder.encode(newPassword));
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("Invalid current password");
        }
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {

        List<User> result = userRepository.findAll();
        log.info("IN getAll - {} users found", result.size());
        return result;
    }

    @Transactional(readOnly = true)
    public User findByPhone(String phone) {
        User result = userRepository.findByPhone(phone);
        if (result == null) {
            log.warn("IN findByPhone - no user found by phone: {}", phone);
            return null;
        }
        log.info("IN findByPhone - user: {} found by phone: {}", result, phone);
        return result;
    }


    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        User result = userRepository.findByEmail(email);
        if (result == null) {
            log.warn("IN findByEmail - no user found by email: {}", email);
            return null;
        }
        log.info("IN findByEmail - user: {} found by email: {}", result, email);
        return result;
    }

    @Transactional(readOnly = true)
    public User findById(UUID id) {
        User result = userRepository.findById(id).orElse(null);
        if (result == null) {
            log.warn("IN findById - no user found by id: {}", id);
            return null;
        }
        log.info("IN findById - user: {} found by id: {}", result, id);
        return result;
    }

    @Transactional
    public void delete(UUID id) {
    userRepository.deleteById(id);
        log.info("IN delete - user with id: {} successfully deleted");
    }

    @Transactional
    public void addRole(UUID userId, UUID roleId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        Role role = roleRepository.findById(roleId).orElseThrow(() -> new IllegalArgumentException("Role not found"));

        if (!user.getRoles().contains(role)) user.getRoles().add(role);

        userRepository.save(user);
    }
    
    @Transactional
    public User update(UUID userId, User newUserData) {
        User originalUser = findById(userId);
        String phone = newUserData.getPhone();
        String email = newUserData.getEmail();
        String firstName = newUserData.getFirstName();
        String lastName = newUserData.getLastName();
        String photo = newUserData.getPhoto();
        String country = newUserData.getCountry();
        String timezone = newUserData.getTimezone();
        String biography = newUserData.getBiography();

        if (phone != null) {
            if (findByPhone(phone) != null) throw new IllegalArgumentException("User with such phone number already exists");
            else originalUser.setPhone(phone);
        } 
        if (email != null) {
            if (findByEmail(email) != null) throw new IllegalArgumentException("User with such email already exists");
            else originalUser.setEmail(email);
        }
        if (firstName != null) {
            originalUser.setFirstName(firstName);
        }
        if (lastName != null) {
            originalUser.setLastName(lastName);
        }
        if (photo != null) {
            originalUser.setPhoto(photo);
        }
        if (country != null) {
            originalUser.setCountry(country);
        }
        if (timezone != null) {
            originalUser.setTimezone(timezone);
        }
        if (biography != null) {
            originalUser.setBiography(biography);
        }
        
        try {
            userRepository.save(originalUser);

            log.info("IN updateUser - user: {} successfully udpated", originalUser);

            return originalUser;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update user", e);
        }
    }

    @Transactional
    public Boolean setCompanyId(UUID userId, UUID companyId){
        boolean isAssociated = userCompanyRelationshipRepository.existsById(new UserCompanyId(companyId, userId));
        if (!isAssociated) {
            throw new UnauthorizedAccessException("User does not have access to update this company.");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User with id:" + userId + " does not exist"));
        user.setCompanyId(companyId);
        return true;
    }

    @Transactional(readOnly = true)
    public Optional<UUID> getCompanyId(UUID userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User with id:" + userId + " does not exist"));
        return user.getCompanyId();
    }
}
