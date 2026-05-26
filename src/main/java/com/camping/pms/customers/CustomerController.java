package com.camping.pms.customers;

import com.camping.pms.customers.dto.CustomerDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerRepository customerRepository;

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping
    public List<CustomerDto> findAll() {
        return customerRepository.findAll().stream()
                .map(CustomerDto::from)
                .toList();
    }

    @GetMapping("/{id}")
    public CustomerDto findById(@PathVariable UUID id) {
        return CustomerDto.from(customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client non trouvé")));
    }

    @PutMapping("/{id}")
    public CustomerDto update(@PathVariable UUID id, @RequestBody Customer updated) {
        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client non trouvé"));
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setPhone(updated.getPhone());
        return CustomerDto.from(customerRepository.save(existing));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        if (!customerRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client non trouvé");
        }
        customerRepository.deleteById(id);
    }
}