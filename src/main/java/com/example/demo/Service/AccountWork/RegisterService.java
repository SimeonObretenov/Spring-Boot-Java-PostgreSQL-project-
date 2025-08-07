package com.example.demo.Service.AccountWork;

import com.example.demo.Entity.Person;
import com.example.demo.Entity.Role;
import com.example.demo.Interfaces.AccountWork.RegisterInterface;
import com.example.demo.Repository.PersonRepository;
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
