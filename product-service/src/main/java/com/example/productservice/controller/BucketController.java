package com.example.productservice.controller;

import com.example.productservice.service.BucketService;
import com.example.productservice.service.impl.BucketServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bucket")
@Slf4j
public class BucketController {

    private final BucketService service;

    @PostMapping("/add/{productId}")
    public void addProductToBucket(@PathVariable UUID productId, @RequestHeader("Authorization") String authHeader){
        service.addProductToBucket(productId, authHeader);
    }

    @GetMapping("/get")
    public ResponseEntity<?> getBucket(@RequestHeader("Authorization") String authHeader){
        return ResponseEntity.ok(service.findByUser(authHeader));
    }

    @DeleteMapping("/delete/{productId}")
    public void deleteProductFromBucket(@PathVariable UUID productId, @RequestHeader("Authorization") String authHeader){
        service.deleteProductFromBucket(productId, authHeader);
    }

}
