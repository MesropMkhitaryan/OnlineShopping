package com.example.apigateway.configs;

import com.example.apigateway.config.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouterValidator {

    public static final String ADMIN = "ADMIN";
    private final JwtService jwtService;

    public static final List<String> openEndpoints = List.of(
            "/api/v1/auth/register",
            "/api/v1/auth/authenticate",
            "/api/v1/user/{email}",
            "/api/v1/product/list",
            "/api/v1/product/getProductPic",
            "/api/v1/category/list",
            "/api/v1/product/find"
    );


    public Predicate<ServerHttpRequest> isSecured =
            request -> openEndpoints.stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

}
