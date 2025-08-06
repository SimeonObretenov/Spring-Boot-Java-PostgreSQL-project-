package com.example.demo.Controller;

import com.example.demo.Dto.LoginRequest;
import com.example.demo.Dto.RegisterRequest;
import com.example.demo.Dto.ResetPasswordRequest;
import com.example.demo.Entity.Person;
import com.example.demo.Interfaces.AccountWork.AccountStatusInterface;
import com.example.demo.Interfaces.AccountWork.LoginInterface;
import com.example.demo.Interfaces.AccountWork.RegisterInterface;
import com.example.demo.Interfaces.AccountWork.ResetPasswordInterface;
import com.example.demo.Service.AccountWork.AccountStatusService;
import com.example.demo.Service.AccountWork.LoginService;
import com.example.demo.Service.AccountWork.RegisterService;
import com.example.demo.Service.AccountWork.ResetPasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final LoginInterface loginService;
    private final AccountStatusInterface accountStatusService;
    private final ResetPasswordInterface resetPasswordService;
    private final RegisterInterface registerService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        String token = loginService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(token);
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
