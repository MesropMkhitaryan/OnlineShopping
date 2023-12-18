package com.example.productservice.service;

import com.example.productservice.model.Bucket;
import com.example.productservice.model.Product;

import java.util.UUID;

public interface BucketService {
    boolean isProductNewInBucket(Bucket bucket, Product product);
    void addProductToBucket(UUID productId, String authHeader);
    void deleteProductFromOneBucket(UUID productId, String authHeader);
    Bucket findByUser(String authHeader);
    void deleteProductFromMultipleBuckets(UUID productId, String authHeader);
    void addProductToExistingBucket(Bucket bucket, Product product);
    void createAndSaveNewBucket(UUID userId, Product product);
}
