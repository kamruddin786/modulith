package com.kamruddin.modulith.inventory;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kamruddin.modulith.order.OrderPlacedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryEventListener {

    private final ProductService productService;

    @EventListener
    @Transactional
    public void handleOrderPlaced(OrderPlacedEvent event) {
        log.info("Received OrderPlacedEvent for order {}: product {} with quantity {}",
                event.getOrderId(), event.getProductId(), event.getQuantity());

        try {
            boolean success = productService.updateStock(event.getProductId(), event.getQuantity());
            if (!success) {
                log.error("Failed to update stock for product {} in order {}", event.getProductId(), event.getOrderId());
                throw new RuntimeException("Insufficient stock for product " + event.getProductId());
            }
            log.info("Successfully updated stock for order {}", event.getOrderId());
        } catch (Exception e) {
            log.error("Error processing OrderPlacedEvent for order {}: {}", event.getOrderId(), e.getMessage(), e);
            throw e; // Re-throw to trigger event publication retry
        }
    }
}