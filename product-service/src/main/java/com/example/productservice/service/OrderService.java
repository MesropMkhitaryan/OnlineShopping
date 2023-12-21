package com.example.productservice.service;

import com.example.productservice.dto.request.OrderRequest;
import com.example.productservice.feign.User;
import com.example.productservice.model.Order;
import com.example.productservice.model.OrdersProduct;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    Order orderProduct(OrderRequest orderRequest, String authHeader);
    Order createOrder(User user);
    List<OrdersProduct> createOrderProducts(OrderRequest orderRequest, Order order, String authHeader);
    OrdersProduct createOrderProduct(UUID productId, OrderRequest orderRequest, Order order, String authHeader);
}
