package com.example.demo.interfaces.account_work;

public interface JwtInterface {
    String generateToken(String username);
    String extractUsername(String token);
}

