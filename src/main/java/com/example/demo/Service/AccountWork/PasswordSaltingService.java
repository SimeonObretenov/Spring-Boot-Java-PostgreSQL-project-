package com.example.demo.Service.AccountWork;

import com.example.demo.Entity.Person;
import com.example.demo.Interfaces.AccountWork.PasswordSaltingInterface;
import com.example.demo.Repository.PersonRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PasswordSaltingService implements PasswordSaltingInterface {

    private final PersonRepository repo;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void hashAllPasswords() {
        List<Person> people = repo.findAll();

        for (Person person : people) {
            if (!person.getPassword().startsWith("$2a$")) {
                String hashed = passwordEncoder.encode(person.getPassword());
                person.setPassword(hashed);
            }
        }

        repo.saveAll(people);
        System.out.println("All passwords have been securely hashed.");
    }
}

