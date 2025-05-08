package com.ubm.ubmweb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ubm.ubmweb.model.LoginHistory;
import com.ubm.ubmweb.model.User;

import java.util.List;
import java.util.UUID;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory, UUID> {
    List<LoginHistory> findByUser(User user);
}
