package com.camping.pms.auth;

import com.camping.pms.customers.Customer;
import com.camping.pms.customers.CustomerRepository;
import com.camping.pms.CurrentUserService;
import com.camping.pms.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private AuthController authController;

    @Test
    void login_shouldReturnTokens_withValidCredentials() {
        Customer customer = new Customer();
        customer.setEmail("test@example.com");

        when(authenticationManager.authenticate(any())).thenReturn(
            new UsernamePasswordAuthenticationToken("test@example.com", "password123")
        );
        when(customerRepository.findByEmail("test@example.com")).thenReturn(Optional.of(customer));
        when(jwtService.generateToken(any())).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any())).thenReturn("refresh-token");

        LoginRequest request = new LoginRequest();
        request.setUsername("test@example.com");
        request.setPassword("password123");

        AuthResponse response = authController.login(request);

        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
    }

    @Test
    void login_shouldThrow401_withInvalidCredentials() {
        when(authenticationManager.authenticate(any()))
            .thenThrow(new BadCredentialsException("Bad credentials"));

        LoginRequest request = new LoginRequest();
        request.setUsername("test@example.com");
        request.setPassword("wrong");

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> authController.login(request)
        );
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void register_shouldThrow409_whenEmailAlreadyExists() {
        when(customerRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(new Customer()));

        RegisterRequest request = new RegisterRequest();
        request.setFirstName("Test");
        request.setLastName("User");
        request.setEmail("test@example.com");
        request.setPassword("password123");

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> authController.register(request)
        );
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void register_shouldReturnTokens_withNewEmail() {
        when(customerRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("hashed-password");
        when(jwtService.generateToken(any())).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any())).thenReturn("refresh-token");

        RegisterRequest request = new RegisterRequest();
        request.setFirstName("New");
        request.setLastName("User");
        request.setEmail("new@example.com");
        request.setPassword("password123");

        AuthResponse response = authController.register(request);

        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
    }
}