package com.ubm.ubmweb.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ubm.ubmweb.model.Role;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Role findByName(String name);
}
