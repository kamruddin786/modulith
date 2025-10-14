package com.kamruddin.modulith.inventory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    private Long id;
    @NotBlank(message = "Product name cannot be blank")
    private String name;
    private String description;
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;
    private LocalDateTime createdAt;

}