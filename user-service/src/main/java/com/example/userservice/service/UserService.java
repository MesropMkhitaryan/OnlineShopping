package com.example.userservice.service;

import com.example.userservice.dto.request.AuthenticationRequest;
import com.example.userservice.dto.request.RegisterRequest;
import com.example.userservice.dto.response.AuthenticationResponse;
import com.example.userservice.dto.response.UserResponse;
import com.example.userservice.model.User;

import java.util.List;

public interface UserService {
    User findByEmail(String email);
    AuthenticationResponse authenticate(AuthenticationRequest request);
    void register(RegisterRequest request);
    List<UserResponse> list();
    User parseToken(String token);
}
