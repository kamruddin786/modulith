package com.kamruddin.modulith.order;

import java.io.Serializable;

import org.springframework.modulith.events.Externalized;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Externalized
public class OrderPlacedEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long productId;
    private Integer quantity;
    private Long orderId; // Add order ID for better tracking

    // Constructor for backward compatibility
    public OrderPlacedEvent(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}