package com.example.productservice.service.impl;

import com.example.productservice.customException.BucketNotFoundException;
import com.example.productservice.feign.User;
import com.example.productservice.feign.UserFeignClient;
import com.example.productservice.model.Bucket;
import com.example.productservice.model.Product;
import com.example.productservice.repository.BucketRepository;
import com.example.productservice.repository.ProductRepository;
import com.example.productservice.service.BucketService;
import com.example.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BucketServiceImpl implements BucketService {
    private final BucketRepository repository;
    private final UserFeignClient userFeignClient;
    private final ProductRepository productRepository;

    public void addProductToBucket(UUID productId, String authHeader) {
        User user = userFeignClient.getCurrentUser(authHeader);
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            Optional<Bucket> optionalBucket = repository.findByUserId(user.getId());
            Bucket bucket = optionalBucket.orElseGet(Bucket::new);

            if (isNewProductInBucket(bucket, product)) {
                bucket.getProduct().add(product);
                bucket.setUserId(user.getId());
                repository.save(bucket);
            }
        }
    }

    public void deleteProductFromBucket(UUID productId, String authHeader) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            Bucket bucket = findByUser(authHeader);
            List<Product> products = bucket.getProduct();
            products.remove(product);
            repository.save(bucket);
        }
    }


    private boolean isNewProductInBucket(Bucket bucket, Product product) {
        return bucket.getProduct().stream().noneMatch(p -> p.equals(product));
    }

    public Bucket findByUser(String authHeader) {
        User user = userFeignClient.getCurrentUser(authHeader);
        return repository.findByUserId(user.getId()).orElseThrow(() ->
                new BucketNotFoundException("There is no bucket found for user with id: " + user.getId()));
    }

    @Override
    public void removeProductFromBuckets(UUID productId, String authHeader) {
        List<Bucket> bucketsByProductId = repository.findBucketsByProductId(productId);
        for (Bucket bucket : bucketsByProductId) {
            deleteProductFromBucket(productId,authHeader);
            repository.save(bucket);
        }
    }

}
