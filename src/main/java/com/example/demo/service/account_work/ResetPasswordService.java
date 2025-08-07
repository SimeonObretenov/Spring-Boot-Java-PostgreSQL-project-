package com.example.demo.service.account_work;

import com.example.demo.entity.Person;
import com.example.demo.repository.PersonRepository;
import com.example.demo.interfaces.account_work.ResetPasswordInterface;
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