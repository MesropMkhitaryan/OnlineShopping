package com.example.productservice.repository;

import com.example.productservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findAllByUserIdAndIsDeletedFalse(UUID userId);
    List<Product> findAllByCategoryId(UUID categoryId);
    List<Product> findAllByIsDeletedFalse();
}
