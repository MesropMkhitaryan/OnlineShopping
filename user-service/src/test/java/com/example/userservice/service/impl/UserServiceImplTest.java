package com.example.userservice.service.impl;

import com.example.userservice.customException.UserEmailConflictException;
import com.example.userservice.customException.UserNotFoundException;
import com.example.userservice.dto.request.RegisterRequest;
import com.example.userservice.dto.response.UserResponse;
import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void list_ShouldReturnListOfUserResponses() {
        List<User> users = Arrays.asList(
                new User(),
                new User()
        );
        when(userRepository.findAll()).thenReturn(users);

        List<UserResponse> userResponses = userService.list();

        assertEquals(userResponses.size(),2);
    }

    @Test
    public void findByEmail_WithValidEmail_ShouldReturnUser() {
        String email = "test@example.com";
        User user = new User();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User result = userService.findByEmail(email);

        assert(result).equals(user);
    }

    @Test
    public void findByEmail_WithInvalidEmail_ShouldThrowUserNotFoundException() {
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByEmail(email))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("does not exist");
    }

    @Test
    public void registerWithExistingEmail() {
        RegisterRequest request = new RegisterRequest("User", "Useryan","user@gmail.com","1234");
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(UserEmailConflictException.class)
                .hasMessageContaining("already exists");
    }

}