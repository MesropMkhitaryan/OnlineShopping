package com.example.productservice.feign;

import com.example.productservice.dto.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "users-service")
@Async
public interface UserFeignClient {
    @GetMapping("/api/v1/user/{email}")
    public User findByEmail(@PathVariable String email);
}
