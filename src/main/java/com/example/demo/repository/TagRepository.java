package com.example.demo.repository;

import com.example.demo.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Set<Tag> findByIdIn(Collection<Long> ids);

    Optional<Tag> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);

    @Query("select t from Tag t where lower(t.name) in :names")
    Set<Tag> findAllByNameInIgnoreCase(@Param("names") Collection<String> names);
}
