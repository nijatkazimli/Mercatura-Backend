package com.mercatura.backend.controller;

import com.mercatura.backend.dto.RequestBody.LoginBody;
import com.mercatura.backend.dto.RequestBody.PasswordChangeBody;
import com.mercatura.backend.dto.RequestBody.RegistrationBody;
import com.mercatura.backend.dto.Responses.AuthResponse;
import com.mercatura.backend.service.AuthenticationService;
import com.mercatura.backend.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final TokenService tokenService;

    public AuthenticationController(AuthenticationService authenticationService, TokenService tokenService) {
        this.authenticationService = authenticationService;
        this.tokenService = tokenService;
    }

    @Tag(name = "Authentication")
    @Operation(summary = "Registers a new user.",
        description = "Registers a new user and provides JWT token.")
    @PostMapping("/register")
    public AuthResponse registerUser(@Valid @RequestBody RegistrationBody body) {
        return authenticationService.registerUser(
                body.getFirstName(), body.getLastName(), body.getUsername(), body.getPassword()
        );
    }

    @Tag(name = "Authentication")
    @Operation(summary = "Logins the existing user.",
        description = "Logins the existing user and provides JWT token.")
    @PostMapping("/login")
    public AuthResponse loginResponse(@RequestBody LoginBody body) {
        return authenticationService.loginUser(body.getUsername(), body.getPassword());
    }

    @Tag(name = "Authentication")
    @Operation(summary = "Checks if the provided token is expired.")
    @PostMapping("/check-token")
    public ResponseEntity<String> checkToken(@RequestHeader("Authorization") String token) {
        String jwtToken = token.replace("Bearer ", "");
        System.out.println(jwtToken);

        if (jwtToken.isEmpty() || tokenService.isJWTExpired(jwtToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is expired.");
        }
        return ResponseEntity.ok("Token is valid.");
    }

    @Tag(name = "Authentication")
    @Operation(summary = "Changes the password.")
    @PostMapping("/change")
    public ResponseEntity<String> changePassword(
            @RequestBody
            PasswordChangeBody passwordChangeBody
    ) {
        return ResponseEntity.ok(authenticationService.changePassword(passwordChangeBody));
    }
}
