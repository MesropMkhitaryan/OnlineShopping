package com.example.productservice.service;

import com.example.productservice.model.Bucket;

import java.util.UUID;

public interface BucketService {

    public void addProductToBucket(UUID productId, String authHeader);
    public void deleteProductFromBucket(UUID productId, String authHeader);
    public Bucket findByUser(String authHeader);
    void removeProductFromBuckets(UUID productId, String authHeader);
}
