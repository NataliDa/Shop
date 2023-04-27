package com.example.security.service;

import com.example.security.models.Order;
import com.example.security.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public Order findById(int id) {
        return orderRepository.findById(id).orElseThrow();
    }

    @Transactional(readOnly = true)
    public Order searchOrder(String search) {
        List<Order> orders = orderRepository.findAll();
        for (Order order: orders) {
            String substring = order.getNumber().substring(order.getNumber().length() - search.length());
            if (substring.equals(search)) {
                return order;
            }
        }
        return null;
    }
}
