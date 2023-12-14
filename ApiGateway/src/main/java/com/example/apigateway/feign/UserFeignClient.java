package com.example.apigateway.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "users-service")
public interface UserFeignClient {
    @GetMapping("/api/v1/user/{email}")
    public User findByEmail(@PathVariable String email);
}