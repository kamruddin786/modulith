package com.kamruddin.modulith.order;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Positive;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    private Long id;
    private Long productId;
    @Positive(message = "Quantity must be a positive value")
    private Integer quantity;
    private LocalDateTime orderDate;
    private String status;

}