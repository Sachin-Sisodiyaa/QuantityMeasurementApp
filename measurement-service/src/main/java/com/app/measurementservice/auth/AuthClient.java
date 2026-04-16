package com.app.measurementservice.auth;

import com.app.measurementservice.auth.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "auth-service", path = "/api/v1/auth")
public interface AuthClient {

    @GetMapping("/me")
    UserProfileResponse me();
}
