package com.ubm.ubmweb.service;

import com.ubm.ubmweb.dto.LoginHistoryDto;
import com.ubm.ubmweb.model.LoginHistory;
import com.ubm.ubmweb.model.User;
import com.ubm.ubmweb.repository.LoginHistoryRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoginHistoryService {

    private final LoginHistoryRepository loginHistoryRepository;

    @Transactional
    public void addLoginHistory(User user, String deviceInfo, String locationInfo) {
        LoginHistory loginHistory = new LoginHistory();
        loginHistory.setUser(user);
        loginHistory.setDevice(deviceInfo);
        loginHistory.setLocation(locationInfo);
        loginHistory.setLoginTime(LocalDateTime.now());

        loginHistoryRepository.save(loginHistory);
    }

    @Transactional(readOnly = true)
    public List<LoginHistoryDto> getLoginHistoryByUser(User user) {
        return loginHistoryRepository.findByUser(user).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private LoginHistoryDto toDto(LoginHistory loginHistory) {
        LoginHistoryDto dto = new LoginHistoryDto();
        dto.setDevice(loginHistory.getDevice());
        dto.setLocation(loginHistory.getLocation());
        dto.setLoginTime(loginHistory.getLoginTime());
        return dto;
    }
}