package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.ResetPasswordRequest;
import com.example.demo.entity.Person;
import com.example.demo.interfaces.account_work.AccountStatusInterface;
import com.example.demo.interfaces.account_work.LoginInterface;
import com.example.demo.interfaces.account_work.RegisterInterface;
import com.example.demo.interfaces.account_work.ResetPasswordInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final LoginInterface loginService;
    private final AccountStatusInterface accountStatusService;
    private final ResetPasswordInterface resetPasswordService;
    private final RegisterInterface registerService;

    @Operation(summary = "User login", description = "Authenticates a user and returns a JWT token for further requests.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful, JWT token returned"),
            @ApiResponse(responseCode = "401", description = "Invalid username or password")
    })
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        String token = loginService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(Collections.singletonMap("token", token));
    }

    @Operation(summary = "Register a new user", description = "Creates a new user account with a username, password, and name.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or user already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        Person person = new Person();
        person.setName(request.getName());
        person.setUsername(request.getUsername());
        person.setPassword(request.getPassword());

        registerService.register(person);
        return ResponseEntity.ok("User registered successfully");
    }

    @Operation(summary = "Deactivate account", description = "Deactivates an existing account. Requires username and password for verification.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User deactivated successfully"),
            @ApiResponse(responseCode = "403", description = "Invalid credentials or not allowed")
    })
    @PutMapping("/deactivate")
    public ResponseEntity<String> deactivate(@RequestParam String username, @RequestParam String password) {
        accountStatusService.deactivate(username, password);
        return ResponseEntity.ok("User deactivated successfully");
    }

    @Operation(summary = "Reactivate account", description = "Reactivates a previously deactivated account. Requires username and password for verification.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User reactivated successfully"),
            @ApiResponse(responseCode = "403", description = "Invalid credentials or not allowed")
    })
    @PutMapping("/reactivate")
    public ResponseEntity<String> reactivate(@RequestParam String username, @RequestParam String password) {
        accountStatusService.reactivate(username, password);
        return ResponseEntity.ok("User reactivated successfully");
    }

    @Operation(summary = "Reset password", description = "Resets the password for a given username.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password reset successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        resetPasswordService.resetPassword(request.getUsername(), request.getNewPassword());
        return ResponseEntity.ok("Password has been reset successfully.");
    }
}
