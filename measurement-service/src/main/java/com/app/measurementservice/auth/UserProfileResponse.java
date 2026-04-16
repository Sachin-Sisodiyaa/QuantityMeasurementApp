package com.app.measurementservice.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfileResponse {
    private String fullName;
    private String email;
    private String mobileNumber;
    private String role;
    private String authProvider;
}
