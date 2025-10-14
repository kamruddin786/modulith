package com.kamruddin.modulith.inventory;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.kamruddin.modulith.order.OrderPlacedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryEventListener {

    private final ProductService productService;

    @EventListener
    public void handleOrderPlaced(OrderPlacedEvent event) {
        log.info("Received OrderPlacedEvent for product {} with quantity {}", event.getProductId(), event.getQuantity());
        boolean success = productService.updateStock(event.getProductId(), event.getQuantity());
        if (!success) {
            log.warn("Failed to update stock for product {}", event.getProductId());
            // In a real app, might publish another event or handle error
        }
    }

}