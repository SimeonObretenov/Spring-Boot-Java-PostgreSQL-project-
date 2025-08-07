package com.example.demo.service.account_work;

import com.example.demo.entity.Person;
import com.example.demo.entity.Role;
import com.example.demo.interfaces.account_work.RegisterInterface;
import com.example.demo.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class RegisterService implements RegisterInterface {

    private final PersonRepository repo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void register(Person person) {
        if (repo.findByUsername(person.getUsername()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        person.setPassword(passwordEncoder.encode(person.getPassword()));
        person.setRole(Role.USER);
        repo.save(person);
    }
}
