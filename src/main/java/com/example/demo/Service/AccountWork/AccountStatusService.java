package com.example.demo.Service.AccountWork;

import com.example.demo.Entity.Person;
import com.example.demo.Interfaces.AccountWork.AccountStatusInterface;
import com.example.demo.Repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AccountStatusService implements AccountStatusInterface {

    private final PersonRepository repo;

    @Override
    public void deactivate(String username) {
        Person person = repo.findByUsername(username);
        if (person == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        person.setActive(false);
        repo.save(person);
    }

    @Override
    public void reactivate(String username) {
        Person person = repo.findByUsername(username);
        if (person == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        person.setActive(true);
        repo.save(person);
    }
}

