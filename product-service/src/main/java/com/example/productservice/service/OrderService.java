package com.example.productservice.service;

import com.example.productservice.dto.request.OrderRequest;
import com.example.productservice.model.Order;

public interface OrderService {
    Order orderProduct(OrderRequest orderRequest, String authHeader);
}
