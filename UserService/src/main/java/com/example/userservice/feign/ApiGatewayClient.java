package com.example.userservice.feign;

import com.example.userservice.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "api-gateway", url = "/generate/token")
public interface ApiGatewayClient {
    @PostMapping
    String generateToken(@RequestBody User userDetails);
}