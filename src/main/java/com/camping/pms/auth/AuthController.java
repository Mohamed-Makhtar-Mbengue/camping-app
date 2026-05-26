package com.camping.pms.auth;

import com.camping.pms.customers.Customer;
import com.camping.pms.customers.CustomerRepository;
import com.camping.pms.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(JwtService jwtService, AuthenticationManager authenticationManager,
                          CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );
            Customer customer = (Customer) customerRepository.findByEmail(request.getUsername())
                    .orElseThrow();
            String accessToken = jwtService.generateToken(customer.getEmail());
            String refreshToken = jwtService.generateRefreshToken(customer.getEmail());
            customer.setRefreshToken(refreshToken);
            customerRepository.save(customer);
            return new AuthResponse(accessToken, refreshToken);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants invalides");
        }
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        if (customerRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email déjà utilisé");
        }
        Customer customer = new Customer();
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        String refreshToken = jwtService.generateRefreshToken(request.getEmail());
        customer.setRefreshToken(refreshToken);
        customerRepository.save(customer);
        return new AuthResponse(jwtService.generateToken(request.getEmail()), refreshToken);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody RefreshRequest request) {
        String email = jwtService.extractUsername(request.getRefreshToken());
        Customer customer = (Customer) customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non trouvé"));
        if (!request.getRefreshToken().equals(customer.getRefreshToken())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token invalide");
        }
        String newAccessToken = jwtService.generateToken(email);
        String newRefreshToken = jwtService.generateRefreshToken(email);
        customer.setRefreshToken(newRefreshToken);
        customerRepository.save(customer);
        return new AuthResponse(newAccessToken, newRefreshToken);
    }
}