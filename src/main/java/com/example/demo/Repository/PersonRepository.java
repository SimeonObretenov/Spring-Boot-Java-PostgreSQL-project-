package com.example.demo.Repository;

import com.example.demo.Entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {
    Person findByName(String name);
    Person findByUsername(String username);
}