package com.example.demo.Service;

import com.example.demo.Entity.Person;
import com.example.demo.Interfaces.LoginInterface;
import com.example.demo.Repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class LoginService implements LoginInterface {

    private final PersonRepository repo;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String login(String username, String password) {
        Person person = repo.findByUsername(username);

        if (person == null || !passwordEncoder.matches(password, person.getPassword()) || !person.isActive()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return jwtService.generateToken(username);

    }
}

