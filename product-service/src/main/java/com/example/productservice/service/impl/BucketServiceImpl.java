package com.example.productservice.service.impl;

import com.example.productservice.customException.BucketNotFoundException;
import com.example.productservice.feign.User;
import com.example.productservice.feign.UserFeignClient;
import com.example.productservice.model.Bucket;
import com.example.productservice.model.Product;
import com.example.productservice.repository.BucketRepository;
import com.example.productservice.service.BucketService;
import com.example.productservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BucketServiceImpl implements BucketService {
    private final BucketRepository repository;
    private final UserFeignClient userFeignClient;
    private final ProductService productService;

    @Autowired
    public BucketServiceImpl(
            BucketRepository repository,
            UserFeignClient userFeignClient,
            @Lazy ProductService productService
    ) {
        this.repository = repository;
        this.userFeignClient = userFeignClient;
        this.productService = productService;
    }

    public void addProductToBucket(UUID productId, String authHeader) {
        User user = userFeignClient.getCurrentUser(authHeader);
        Product product = productService.findById(productId);

        Optional<Bucket> optionalBucket = repository.findByUserId(user.getId());

        if (optionalBucket.isPresent()) {
            addProductToExistingBucket(optionalBucket.get(), product);
        } else {
            createAndSaveNewBucket(user.getId(), product);
        }
    }


    public void addProductToExistingBucket(Bucket bucket, Product product) {
        if (isProductNewInBucket(bucket, product)) {
            bucket.getProduct().add(product);
            repository.save(bucket);
        }
    }

    public void createAndSaveNewBucket(UUID userId, Product product) {
        ArrayList<Product> productList = new ArrayList<>();
        productList.add(product);
        Bucket newBucket = new Bucket();
        newBucket.setUserId(userId);
        newBucket.setProduct(productList);
        repository.save(newBucket);
    }

    public boolean isProductNewInBucket(Bucket bucket, Product product) {
        return bucket.getProduct().stream().noneMatch(p -> p.equals(product));
    }

    public void deleteProductFromOneBucket(UUID productId, String authHeader) {
        Product product = productService.findById(productId);
        Bucket bucket = findByUser(authHeader);
        List<Product> products = bucket.getProduct();
        products.remove(product);
        repository.save(bucket);
    }




    public Bucket findByUser(String authHeader) {
        User user = userFeignClient.getCurrentUser(authHeader);
        return repository.findByUserId(user.getId()).orElseThrow(() ->
                new BucketNotFoundException("There is no bucket found for user with id: " + user.getId()));
    }

    @Override
    public void deleteProductFromMultipleBuckets(UUID productId, String authHeader) {
        List<Bucket> bucketsByProductId = repository.findBucketsByProductId(productId);
        for (Bucket bucket : bucketsByProductId) {
            deleteProductFromOneBucket(productId, authHeader);
        }
    }

}
