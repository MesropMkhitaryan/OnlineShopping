package com.example.productservice.service;

import com.example.productservice.config.JwtParser;
import com.example.productservice.dto.User;
import com.example.productservice.model.Bucket;
import com.example.productservice.model.Product;
import com.example.productservice.repository.BucketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BucketService {
    private final BucketRepository repository;
    private final JwtParser jwtParser;
    private final ProductService productService;


    public void addProduct(UUID productId, String authHeader) {
        String token = authHeader.substring("Bearer ".length());
        User user = jwtParser.parseToken(token);
        Product product = productService.findById(productId);
        Optional<Bucket> bucketByUserId = repository.findByUserId(user.getId());

        if (bucketByUserId.isPresent()) {
            Bucket bucket = bucketByUserId.get();
            boolean containsProduct = bucket.getProduct().stream()
                    .anyMatch(p -> p.equals(product));

            if (!containsProduct) {
                bucket.getProduct().add(product);
                bucket.setUserId(user.getId());
                repository.save(bucket);
            }
        } else {
            Bucket bucket = new Bucket();
            List<Product> products = new ArrayList<>();
            products.add(product);
            bucket.setProduct(products);
            bucket.setUserId(user.getId());
            repository.save(bucket);
        }
    }

    public void deleteProductFromBucket(UUID productId, String authHeader) {
        String token = authHeader.substring("Bearer ".length());
        User user = jwtParser.parseToken(token);
        Product product = productService.findById(productId);
        Optional<Bucket> bucketByUserId = repository.findByUserId(user.getId());
        if (bucketByUserId.isPresent()) {
            Bucket bucket = bucketByUserId.get();
            List<Product> products = bucket.getProduct();
            products.remove(product);
            repository.save(bucket);
        }
    }

    public Bucket findByUser(String authHeader) {
        String token = authHeader.substring("Bearer ".length());
        User user = jwtParser.parseToken(token);
        return repository.findByUserId(user.getId()).orElseThrow(() -> new RuntimeException("There is no bucket for user with: " + user.getId() + " :id"));
    }

    public void deleteBucket(String authHeader) {
        Bucket bucket = findByUser(authHeader);
        repository.delete(bucket);
    }

    public boolean isBucketEmpty(UUID userId) {
        Optional<Bucket> bucketByUserId = repository.findByUserId(userId);
        return bucketByUserId.map(bucket -> bucket.getProduct().isEmpty()).orElse(true);
    }

}
