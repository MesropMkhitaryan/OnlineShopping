package com.example.productservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductUpdateRequest {
    @NotBlank(message = "product's title can't be null or empty")
    private String title;
    @Min(value = 1, message = "price should be at least 1")
    private double price;
    @Min(value = 1, message = "quantity should be at least 1")
    private int quantity;
    @Size(min = 3, max = 600, message = "description length should be between 3-600")
    private String description;
}
