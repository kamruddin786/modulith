package com.kamruddin.modulith.inventory;

import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import com.kamruddin.modulith.order.OrderPlacedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryEventListener {

    private final ProductService productService;

    @ApplicationModuleListener
    public void handleOrderPlaced(OrderPlacedEvent event) {
        log.info("=== RECEIVED OrderPlacedEvent ===");
        log.info("Event details - Order ID: {}, Product ID: {}, Quantity: {}", 
                event.getOrderId(), event.getProductId(), event.getQuantity());
        log.info("Processing inventory update...");

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