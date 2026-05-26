package com.devops.releasetracker.service;

import com.devops.releasetracker.dto.AuthResponse;
import com.devops.releasetracker.dto.LoginRequest;
import com.devops.releasetracker.dto.RegisterRequest;
import com.devops.releasetracker.entity.Role;
import com.devops.releasetracker.entity.User;
import com.devops.releasetracker.exception.BadRequestException;
import com.devops.releasetracker.repository.UserRepository;
import com.devops.releasetracker.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerCreatesUserAndReturnsJwt() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Jane Developer");
        request.setEmail("jane@example.com");
        request.setPassword("password123");
        request.setRole(Role.DEVELOPER);

        User saved = User.builder()
                .id(1L)
                .name("Jane Developer")
                .email("jane@example.com")
                .password("encoded")
                .role(Role.DEVELOPER)
                .build();

        when(userRepository.existsByEmail("jane@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(saved);
        when(jwtService.generateToken(saved)).thenReturn("jwt-token");

        AuthResponse response = authService.register(request);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getUser().getEmail()).isEqualTo("jane@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerRejectsDuplicateEmail() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("jane@example.com");

        when(userRepository.existsByEmail("jane@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Email is already registered");
    }

    @Test
    void loginAuthenticatesAndReturnsJwt() {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@example.com");
        request.setPassword("password123");

        User user = User.builder()
                .id(2L)
                .name("Admin")
                .email("admin@example.com")
                .password("encoded")
                .role(Role.ADMIN)
                .build();

        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("admin-token");

        AuthResponse response = authService.login(request);

        assertThat(response.getToken()).isEqualTo("admin-token");
        assertThat(response.getUser().getRole()).isEqualTo(Role.ADMIN);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
