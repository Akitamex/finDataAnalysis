package com.ubm.ubmweb.helper;

import jakarta.servlet.ServletRequest;
import org.springframework.stereotype.Component;

import com.ubm.ubmweb.security.jwt.JwtTokenProvider;

import java.util.UUID;

@Component
public class JwtUtil {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtUtil(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public UUID userIdFromRequest(ServletRequest servletRequest) {
        String token = jwtTokenProvider.resolveToken((jakarta.servlet.http.HttpServletRequest) servletRequest);
        if (token==null) throw new IllegalArgumentException("No Token");
        token = jwtTokenProvider.decryptToken(token);
        if(token==null|| !jwtTokenProvider.validateToken(token)){
            throw new IllegalArgumentException("Invalid or expired token");
        }
        UUID userId = jwtTokenProvider.getUserId(token);
        return userId;
    }
}
