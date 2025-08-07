package com.example.demo.service.account_work;

import com.example.demo.entity.Person;
import com.example.demo.interfaces.account_work.FindNameInterface;
import com.example.demo.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class FindNameService implements FindNameInterface {

    private final PersonRepository repo;

    public String getString(String name) {
        Person person = repo.findByName(name);
        if (person == null) { throw new ResponseStatusException(HttpStatus.FORBIDDEN); }
        return "Hello, " + person.getName() + "! User:  " + person.getUsername() + " Role: " + person.getRole();
    }
}
