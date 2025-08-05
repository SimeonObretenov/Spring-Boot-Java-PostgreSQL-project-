package com.example.demo.Service;

import com.example.demo.Entity.Person;
import com.example.demo.Interfaces.RegisterInterface;
import com.example.demo.Repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterService implements RegisterInterface {

    private final PersonRepository repo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void register(Person person) {
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        repo.save(person);
    }
}
