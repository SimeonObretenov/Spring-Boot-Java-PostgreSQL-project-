package com.example.demo.Service;

import com.example.demo.Entity.Person;
import com.example.demo.Repository.PersonRepository;
import com.example.demo.Interfaces.ResetPasswordInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ResetPasswordService implements ResetPasswordInterface {

    private final PersonRepository repo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void resetPassword(String username, String newPassword) {
        Person person = repo.findByUsername(username);
        if (person == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        person.setPassword(passwordEncoder.encode(newPassword));
        repo.save(person);
    }
}