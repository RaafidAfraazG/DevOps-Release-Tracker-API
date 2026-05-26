package com.devops.releasetracker.controller;

import com.devops.releasetracker.dto.AuthResponse;
import com.devops.releasetracker.dto.RegisterRequest;
import com.devops.releasetracker.dto.UserResponse;
import com.devops.releasetracker.entity.Role;
import com.devops.releasetracker.security.CustomUserDetailsService;
import com.devops.releasetracker.security.JwtService;
import com.devops.releasetracker.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void registerReturnsCreatedAndToken() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("Intern Dev");
        request.setEmail("intern@example.com");
        request.setPassword("password123");
        request.setRole(Role.DEVELOPER);

        when(authService.register(any(RegisterRequest.class))).thenReturn(AuthResponse.builder()
                .token("jwt-token")
                .tokenType("Bearer")
                .user(UserResponse.builder()
                        .id(1L)
                        .name("Intern Dev")
                        .email("intern@example.com")
                        .role(Role.DEVELOPER)
                        .build())
                .build());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("jwt-token"))
                .andExpect(jsonPath("$.data.user.role").value("DEVELOPER"));
    }
}
