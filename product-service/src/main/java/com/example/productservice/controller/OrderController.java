package com.example.productservice.controller;

import com.example.productservice.dto.request.OrderRequest;
import com.example.productservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/product")
    public ResponseEntity<?> orderProduct(@Valid @RequestBody OrderRequest orderRequest, @RequestHeader("Authorization") String header) {
       return ResponseEntity.ok(orderService.orderProduct(orderRequest,header));
    }
}
