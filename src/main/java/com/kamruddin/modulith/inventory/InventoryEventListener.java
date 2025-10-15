package com.kamruddin.modulith.inventory;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kamruddin.modulith.config.RabbitMQConfig;
import com.kamruddin.modulith.order.OrderPlacedEvent;
import com.rabbitmq.client.Channel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryEventListener {

    private final ProductService productService;
    private final LockRegistry lockRegistry;

    // Keep the internal event listener for local module communication
    @ApplicationModuleListener
    public void handleInternalOrderPlaced(OrderPlacedEvent event) {
        // This will handle events within the same application instance (non-externalized)
        processOrderPlacedEvent(event);
    }
    
    // Add RabbitMQ listener for external events from the queue
    @RabbitListener(queues = RabbitMQConfig.ORDER_EVENTS_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    @Transactional
    public void handleExternalOrderPlaced(OrderPlacedEvent event, 
                                        Channel channel, 
                                        @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("=== RECEIVED EXTERNALIZED OrderPlacedEvent via RabbitMQ ===");
        
        // Create a lock key based on the order ID to prevent concurrent processing
        String lockKey = "order-" + event.getOrderId();
        Lock lock = lockRegistry.obtain(lockKey);
        
        try {
            // Try to acquire the lock with a timeout
            if (lock.tryLock(10, TimeUnit.SECONDS)) {
                try {
                    processOrderPlacedEvent(event);
                    // Acknowledge successful processing
                    channel.basicAck(deliveryTag, false);
                } finally {
                    // Always release the lock when done
                    lock.unlock();
                }
            } else {
                // If we couldn't get the lock, reject the message and requeue
                log.warn("Could not acquire lock for order {}. Message will be requeued.", event.getOrderId());
                channel.basicNack(deliveryTag, false, true);
            }
        } catch (Exception e) {
            try {
                log.error("Error processing OrderPlacedEvent for order {}: {}", event.getOrderId(), e.getMessage(), e);
                // Negative acknowledgment, requeue the message
                channel.basicNack(deliveryTag, false, true);
            } catch (Exception ex) {
                log.error("Error during message rejection: {}", ex.getMessage(), ex);
            }
        }
    }
    
    // Common processing logic for both internal and external events
    private void processOrderPlacedEvent(OrderPlacedEvent event) {
        log.info("Event details - Order ID: {}, Product ID: {}, Quantity: {}", 
                event.getOrderId(), event.getProductId(), event.getQuantity());
        log.info("Processing inventory update...");

        boolean success = productService.updateStock(event.getProductId(), event.getQuantity());
        if (!success) {
            log.error("Failed to update stock for product {} in order {}", 
                    event.getProductId(), event.getOrderId());
            throw new RuntimeException("Insufficient stock for product " + event.getProductId());
        }
        
        log.info("Successfully updated stock for order {}", event.getOrderId());
    }
}