package com.app.quantitymeasurement.controller;

import com.app.quantitymeasurement.auth.AuthResponse;
import com.app.quantitymeasurement.auth.UserProfileResponse;
import com.app.quantitymeasurement.exception.AuthException;
import com.app.quantitymeasurement.security.JwtService;
import com.app.quantitymeasurement.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class OAuthSuccessController {

    private final AuthService authService;
    private final JwtService jwtService;

    public OAuthSuccessController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @GetMapping(value = "/oauth-success", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> oauthSuccess(@RequestParam(required = false) String token,
                                          @RequestParam(required = false) String email) {
        if (!StringUtils.hasText(token) || !StringUtils.hasText(email)) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Missing token or email in OAuth success redirect",
                    "path", "/oauth-success"
            ));
        }

        String normalizedEmail = email.trim().toLowerCase();
        String tokenEmail;
        try {
            tokenEmail = jwtService.extractUsername(token);
        } catch (Exception ex) {
            throw new AuthException(HttpStatus.UNAUTHORIZED, "Invalid OAuth token");
        }

        if (!normalizedEmail.equalsIgnoreCase(tokenEmail) || !jwtService.isTokenValid(token, normalizedEmail)) {
            throw new AuthException(HttpStatus.UNAUTHORIZED, "Invalid OAuth token for provided user");
        }

        UserProfileResponse user = authService.getCurrentUser(normalizedEmail);
        AuthResponse authResponse = new AuthResponse(
                token,
                "Bearer",
                jwtService.getExpirationSeconds(),
                user
        );

        return ResponseEntity.ok(authResponse);
    }
}
