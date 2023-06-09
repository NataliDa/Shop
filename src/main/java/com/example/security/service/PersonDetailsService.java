package com.example.security.service;

import com.example.security.models.Person;
import com.example.security.repository.PersonRepository;
import com.example.security.security.PersonDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PersonDetailsService implements UserDetailsService {
    private final PersonRepository personRepository;

    @Autowired
    public PersonDetailsService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Person> person = personRepository.findByLogin(username);
        if (person.isEmpty()) {
            throw new UsernameNotFoundException("Пользователь не найден");
        }
        return new PersonDetails(person.get());
    }
}
