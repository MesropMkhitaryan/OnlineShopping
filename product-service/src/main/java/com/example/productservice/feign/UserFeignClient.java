package com.example.productservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "users-service")
@Async
public interface UserFeignClient {
    @GetMapping("/api/v1/user/current")
    User getCurrentUser(@RequestHeader("Authorization") String authHeader);
}
