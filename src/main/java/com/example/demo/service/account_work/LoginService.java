package com.example.demo.service.account_work;

import com.example.demo.entity.Person;
import com.example.demo.interfaces.account_work.JwtInterface;
import com.example.demo.interfaces.account_work.LoginInterface;
import com.example.demo.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class LoginService implements LoginInterface {

    private final PersonRepository repo;
    private final JwtInterface jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String login(String username, String password) {
        Person person = repo.findByUsername(username);

        if (person == null || !passwordEncoder.matches(password, person.getPassword()) || !person.isActive()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        return jwtService.generateToken(person.getUsername());
    }
}

