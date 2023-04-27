package com.example.security.repository;

import com.example.security.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByLogin(String login);

    boolean existsByLogin(String login);

    List<Person> findAllByIdIsNot(Long id);
}