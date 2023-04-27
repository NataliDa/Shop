package com.example.security.service;

import com.example.security.enumm.Status;
import com.example.security.models.Order;
import com.example.security.repository.OrderRepository;
import com.example.security.repository.PersonRepository;
import com.example.security.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {
    private final PersonRepository personRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public AdminService(PersonRepository personRepository, ProductRepository productRepository, OrderRepository orderRepository) {
        this.personRepository = personRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public void editOrderStatus(int id) {
        Order order = orderRepository.findById(id).orElseThrow();
        if (Status.Принят.equals(order.getStatus()) || Status.Оформлен.equals(order.getStatus())) {
            if (Status.Принят.equals(order.getStatus())) {
                order.setStatus(Status.Оформлен);
            } else if (Status.Оформлен.equals(order.getStatus())) {
                order.setStatus(Status.Ожидает);
            }
        }
    }
}
