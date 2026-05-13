package com.sahilkumar.api.auth;

import com.sahilkumar.api.auth.dto.AuthResponse;
import com.sahilkumar.api.auth.dto.LoginRequest;
import com.sahilkumar.api.auth.dto.RegisterRequest;
import com.sahilkumar.api.security.CurrentUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {

    private final AuthService authService;
    private final CurrentUser currentUser;

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.status(201).body(authService.register(req));
    }

    @GetMapping("/me")
    public AuthResponse me() {
        User u = currentUser.require();
        return new AuthResponse(null, null, 0, u.getId(), u.getUsername(), u.getFullName(), u.getRole());
    }
}
