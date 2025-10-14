package com.kamruddin.modulith.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderPlacedEvent {

    private Long productId;
    private Integer quantity;
    private Long orderId; // Add order ID for better tracking

    // Constructor for backward compatibility
    public OrderPlacedEvent(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}