package com.ubm.ubmweb.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class RequestInfoProvider {

    public String getDeviceInfo(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    public String getLocationInfo(HttpServletRequest request) {
        return request.getRemoteAddr();
    }
}