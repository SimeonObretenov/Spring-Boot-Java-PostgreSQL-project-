package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.ResetPasswordRequest;
import com.example.demo.entity.Person;
import com.example.demo.interfaces.account_work.AccountStatusInterface;
import com.example.demo.interfaces.account_work.LoginInterface;
import com.example.demo.interfaces.account_work.RegisterInterface;
import com.example.demo.interfaces.account_work.ResetPasswordInterface;
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

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        String token = loginService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(Collections.singletonMap("token", token));
    }


    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        Person person = new Person();
        person.setName(request.getName());
        person.setUsername(request.getUsername());
        person.setPassword(request.getPassword());

        registerService.register(person);
        return ResponseEntity.ok("User registered successfully");
    }

    @PutMapping("/deactivate")
    public ResponseEntity<String> deactivate(@RequestParam String username, @RequestParam String password) {
        accountStatusService.deactivate(username, password);
        return ResponseEntity.ok("User deactivated successfully");
    }

    @PutMapping("/reactivate")
    public ResponseEntity<String> reactivate(@RequestParam String username,  @RequestParam String password) {
        accountStatusService.reactivate(username, password);
        return ResponseEntity.ok("User reactivated successfully");
    }

    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        resetPasswordService.resetPassword(request.getUsername(), request.getNewPassword());
        return ResponseEntity.ok("Password has been reset successfully.");
    }

}
