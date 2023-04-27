package com.example.security.service;

import com.example.security.enumm.Role;
import com.example.security.enumm.Status;
import com.example.security.models.Order;
import com.example.security.models.Person;
import com.example.security.repository.OrderRepository;
import com.example.security.repository.PersonRepository;
import com.example.security.security.PersonDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PersonService {

    private final PersonRepository personRepository;

    private final PasswordEncoder passwordEncoder;

    private final OrderRepository orderRepository;

    @Autowired
    public PersonService(PersonRepository personRepository, PasswordEncoder passwordEncoder, OrderRepository orderRepository) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
        this.orderRepository = orderRepository;

        saveFirstAdmin();
    }

    @Transactional(readOnly = true)
    public boolean existsPerson(Person person) {
        return personRepository.existsByLogin(person.getLogin());
    }

    public void registerUser(Person person) {
        person.setRole(Role.USER);
        register(person);
    }

    public void registerAdmin(Person person) {
        person.setRole(Role.ADMIN);
        register(person);
    }

    @Transactional
    public void register(Person person) {
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        personRepository.save(person);
    }

    @Transactional
    void saveFirstAdmin() {
        if (!personRepository.existsByLogin("admin")) {
            Person admin = new Person();
            admin.setLogin("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole(Role.ADMIN);

            personRepository.save(admin);
        }
    }

    @Transactional
    public void editPersonRole(Long id) {
        Person person = personRepository.findById(id).orElseThrow();
        if (Role.ADMIN.equals(person.getRole())) {
            person.setRole(Role.USER);
        } else {
            person.setRole(Role.ADMIN);
        }
    }

    @Transactional(readOnly = true)
    public List<Person> getAllPersons() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        Person person = personDetails.getPerson();

        return personRepository.findAllByIdIsNot(person.getId());
    }

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional
    public void editOrderStatus(int id) {
        Order order = orderRepository.findById(id).orElseThrow();
        if (Status.Ожидает.equals(order.getStatus())) {
            order.setStatus(Status.Получен);
        }
    }
}
