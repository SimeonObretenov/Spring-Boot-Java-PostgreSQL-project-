package com.example.demo.Interfaces;

import com.example.demo.Entity.Role;

public interface JwtInterface {
    String generateToken(String username);
    String extractUsername(String token);
}

