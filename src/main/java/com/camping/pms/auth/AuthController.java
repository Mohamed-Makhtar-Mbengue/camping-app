package com.camping.pms.auth;

import com.camping.pms.customers.CustomerRepository;
import com.camping.pms.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    private CustomerRepository customerRepository;

    public AuthController(JwtService jwtService, AuthenticationManager authenticationManager) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/fix-password")
    public String fixPassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String newHash = encoder.encode("password123");
        customerRepository.findByEmail("test@example.com").ifPresent(customer -> {
            customer.setPassword(newHash);
            customerRepository.save(customer);
        });
        return "Mot de passe mis à jour : " + newHash;
    }

    @GetMapping("/hash")
    public String hash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode("password123");
    }

    @GetMapping("/test-password")
    public String testPassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = "$2a$10$KLFKIPuBUilLE09DjNt11.TdoFcr6h2y4gSvE4OojeW5yHMVabZGy";
        boolean match = encoder.matches("password123", hash);
        return "Match : " + match;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        try {
            System.out.println("Tentative de login pour : " + request.getUsername());
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );
            System.out.println("Authentification réussie !");
            return jwtService.generateToken(request.getUsername());
        } catch (AuthenticationException e) {
            System.out.println("Échec authentification : " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants invalides");
        }
    }
}