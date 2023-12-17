package com.example.productservice.repository;

import com.example.productservice.model.OrdersProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersProductRepository extends JpaRepository<OrdersProduct, Integer> {
}
