package com.app.userservice.auth;

import com.app.userservice.model.UserEntity;
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

    public static UserProfileResponse fromEntity(UserEntity user) {
        return new UserProfileResponse(
                user.getFullName(),
                user.getEmail(),
                user.getMobileNumber(),
                user.getRole().name(),
                user.getAuthProvider().name()
        );
    }
}
