package com.example.security.repository;

import com.example.security.models.Order;
import com.example.security.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByPerson(Person person);

    @Query("select max(o.id) from Order o")
    int findMaxId();
}
