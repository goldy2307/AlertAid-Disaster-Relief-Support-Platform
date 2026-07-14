package com.alertaid.controller;

import com.alertaid.dto.LoginRequest;
import com.alertaid.model.Role;
import com.alertaid.model.User;
import com.alertaid.security.JwtTokenProvider;
import com.alertaid.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(AuthController.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    void loginReturnsOk() throws Exception {
        when(authenticationManager.authenticate(any())).thenReturn(new TestingAuthenticationToken("x","y"));
        User u = new User();
        u.setEmail("test@example.com");
        u.setRole(Role.CITIZEN);
        when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(u));
        when(jwtTokenProvider.generateToken(any())).thenReturn("token");

        String body = "{\"email\":\"test@example.com\",\"password\":\"pass\"}";
mockMvc.perform(post("/api/auth/login").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());
    }
}
