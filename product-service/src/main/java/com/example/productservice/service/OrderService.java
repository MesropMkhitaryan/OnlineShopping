package com.example.productservice.service;

import com.example.productservice.dto.request.OrderRequest;
import com.example.productservice.model.Order;

public interface OrderService {
    Order orderProduct(OrderRequest orderRequest, String authHeader);
//    void createOrder(Order order);
//
//    Order getOrderById(UUID orderId);
//
//    List<Order> getAllOrders();
//
//    void updateOrder(Order order);
//
//    void deleteOrder(UUID orderId);
}
