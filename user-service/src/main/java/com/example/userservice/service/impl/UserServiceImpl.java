package com.example.userservice.service.impl;


import com.example.userservice.config.JwtService;
import com.example.userservice.customException.UserEmailConflictException;
import com.example.userservice.customException.UserNotFoundException;
import com.example.userservice.dto.request.AuthenticationRequest;
import com.example.userservice.dto.request.RegisterRequest;
import com.example.userservice.dto.response.AuthenticationResponse;
import com.example.userservice.dto.response.UserResponse;
import com.example.userservice.model.Role;
import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper mapper;

    public List<UserResponse> list() {
        return repository.findAll()
                .stream()
                .map(user -> mapper.map(user, UserResponse.class))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public User findByEmail(String email){
        return repository.findByEmail(email).orElseThrow(() ->
                new UserNotFoundException("user with email" + email + "does not exist"));
    }

    @Override
    public void register(RegisterRequest request) {
        Optional<User> optionalUser = repository.findByEmail(request.getEmail());
        if (optionalUser.isPresent()){
            throw new UserEmailConflictException("user with email " +
                    request.getEmail() + " already exists");
        }
        var user = User.builder().
                firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .userRole(Role.USER)
                .build();
        repository.save(user);
    }
    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        ));
        var user = findByEmail(request.getEmail());
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }


    public User parseToken(String token) {
        String email = jwtService.extractUserName(token);
        return findByEmail(email);
    }

}
