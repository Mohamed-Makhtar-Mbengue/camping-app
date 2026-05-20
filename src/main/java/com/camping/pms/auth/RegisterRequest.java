package com.camping.pms.auth;

import lombok.Data;

@Data
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String password;
}