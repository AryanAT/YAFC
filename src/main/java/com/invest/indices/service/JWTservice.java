package com.invest.indices.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JWTservice {
    String generateToken(String username);

    String getUsername(String token);

    boolean validateToken(String token, UserDetails userDetails);
}
