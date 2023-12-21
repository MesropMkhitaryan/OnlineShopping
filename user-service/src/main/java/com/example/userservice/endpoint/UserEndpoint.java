package com.example.userservice.endpoint;

import com.example.userservice.dto.response.UserResponse;
import com.example.userservice.model.User;
import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Slf4j
public class UserEndpoint {
    private final UserService service;

    @GetMapping("/list")
    public ResponseEntity<List<UserResponse>> list(){
        return ResponseEntity.ok( service.list());
    }

    @GetMapping("/current")
    public User getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring("Bearer ".length());
        return service.parseToken(token);
    }

}

