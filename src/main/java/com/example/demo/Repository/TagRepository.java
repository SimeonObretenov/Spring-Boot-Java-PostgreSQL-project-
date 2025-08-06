package com.example.demo.Repository;

import com.example.demo.Entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Set<Tag> findByIdIn(Set<Long> ids);
}
