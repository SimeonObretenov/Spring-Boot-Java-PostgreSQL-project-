package com.example.demo.service.account_work;

import com.example.demo.entity.Person;
import com.example.demo.interfaces.account_work.PasswordSaltingInterface;
import com.example.demo.repository.PersonRepository;
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

