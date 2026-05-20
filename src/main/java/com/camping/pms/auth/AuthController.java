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

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        if (customerRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email déjà utilisé");
        }
        Customer customer = new Customer();
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customerRepository.save(customer);
        return jwtService.generateToken(customer.getEmail());
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );
            return jwtService.generateToken(request.getUsername());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants invalides");
        }
    }
}