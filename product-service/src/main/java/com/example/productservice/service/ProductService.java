package com.example.productservice.service;

import com.example.productservice.dto.request.ProductRequest;
import com.example.productservice.dto.request.ProductUpdateRequest;
import com.example.productservice.dto.response.ProductResponse;
import com.example.productservice.model.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    UUID findByToken(String authHeader);
    Product save(ProductRequest request, String authHeader);
    Product saveImgIntoProduct(UUID productId, MultipartFile image);
    List<ProductResponse> list();
    void delete(UUID id, String authHeader);
    Product findById(UUID id);
    Product update(UUID id, ProductUpdateRequest request);
    List<ProductResponse> findAllByUserId(String authHeader);
    void updateProductQuantity(Product product, int quantity);
//    UUID findByToken(String authHeader);
}
