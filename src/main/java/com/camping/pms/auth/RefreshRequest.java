package com.camping.pms.auth;

import lombok.Data;

@Data
public class RefreshRequest {
    private String refreshToken;
}