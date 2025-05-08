package com.ubm.ubmweb.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LoginHistoryDto {
    private String device;
    private String location;
    private LocalDateTime loginTime;
}
