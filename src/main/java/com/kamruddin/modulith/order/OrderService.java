package com.kamruddin.modulith.order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    public List<Order> findAll() {
        return (List<Order>) orderRepository.findAll();
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    @Transactional
    public Order placeOrder(Order order) {
        order.setStatus("PLACED");
        order.setOrderDate(LocalDateTime.now());
        Order saved = orderRepository.save(order);
        // Publish event with order ID for better tracking
        eventPublisher.publishEvent(new OrderPlacedEvent(saved.getProductId(), saved.getQuantity(), saved.getId()));
        return saved;
    }

    @Transactional
    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }

}