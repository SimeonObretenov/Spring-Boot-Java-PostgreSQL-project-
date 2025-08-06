package com.example.demo.Interfaces.AccountWork;

public interface JwtInterface {
    String generateToken(String username);
    String extractUsername(String token);
}

