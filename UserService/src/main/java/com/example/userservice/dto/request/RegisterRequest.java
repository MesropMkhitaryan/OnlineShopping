package com.example.userservice.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "user's name can't be null or empty")
    private String firstName;
    @NotBlank(message = "user's name can't be null or empty")
    private String lastName;
    @Email
    private String email;
    @Size(min = 4, max = 18, message = "password length should be between 4-18")
    private String password;

}




