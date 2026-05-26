package com.camping.pms.customers.dto;

import com.camping.pms.customers.Customer;
import com.camping.pms.customers.Role;
import lombok.Data;

import java.util.UUID;

@Data
public class CustomerDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Role role;

    public static CustomerDto from(Customer customer) {
        CustomerDto dto = new CustomerDto();
        dto.setId(customer.getId());
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        dto.setRole(customer.getRole());
        return dto;
    }
}