package com.example.apigateway.feign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private Role userRole;
    private double budget;
}
