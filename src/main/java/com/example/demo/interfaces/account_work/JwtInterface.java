package com.example.demo.interfaces.account_work;

import io.jsonwebtoken.Claims;

import java.util.Map;

public interface JwtInterface {
    String generateToken(String username);
    String extractUsername(String token);
    boolean isTokenValid(String token);
    Claims getAllClaims(String token);
    String generateRefreshToken(String username);
    String generateAccessToken(String username, Map<String, Object> extraClaims);
}


