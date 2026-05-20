package com.camping.pms.customers;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private final CustomerRepository customerRepository;

    public UserService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé : " + username));
        
        System.out.println("Utilisateur trouvé : " + customer.getUsername());
        System.out.println("Hash en base : " + customer.getPassword());
        
        return customer;
    }
}