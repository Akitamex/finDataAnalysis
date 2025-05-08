package com.ubm.ubmweb.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ubm.ubmweb.model.User;

public interface UserRepository  extends JpaRepository<User, UUID> {
    User findByPhone(String phone);
    User findByEmail(String email);
}
 