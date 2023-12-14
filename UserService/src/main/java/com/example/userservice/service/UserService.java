package com.example.userservice.service;


import com.example.userservice.model.Role;
import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public User findById(int userId) {
        return repository.findById(userId).orElseThrow();
    }

    public User findByEmail(String email){
        return repository.findByEmail(email).orElseThrow();
    }

    public List<User> list(){
        return repository.findAll();
    }

    public void makeAdmin(int userId) {
        User user = findById(userId);
        if (!user.getUserRole().equals(Role.ADMIN)) {
            user.setUserRole(Role.ADMIN);
            repository.save(user);
        }
    }

}
