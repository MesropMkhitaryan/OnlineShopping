package com.example.apigateway.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableHystrix
public class GatewayConfig {
    @Autowired
    private AuthenticationFilter filter;

    @Autowired
    private AdminFilter adminFilter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("admin-service", r -> r.path("/api/v1/user/list")
                        .filters(f-> f.filter(adminFilter))
                        .uri("lb://users-service"))
                .route("users-service", r -> r.path("/api/v1/auth/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://users-service"))
                .route("users-services", r -> r.path("/api/v1/user/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://users-service"))
                .route("products-service", r -> r.path("/api/v1/product/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://products-service"))
                .route("products-service-bucket", r -> r.path("/api/v1/bucket/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://products-service"))
                .route("products-service-order", r -> r.path("/api/v1/order/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://products-service"))
                .route("category-service", r -> r.path("/api/v1/category/**")
                        .filters(f-> f.filter(adminFilter))
                        .uri("lb://products-service"))
                .build();
    }
}
