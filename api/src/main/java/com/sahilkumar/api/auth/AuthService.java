package com.sahilkumar.api.auth;

import com.sahilkumar.api.auth.dto.AuthResponse;
import com.sahilkumar.api.auth.dto.LoginRequest;
import com.sahilkumar.api.auth.dto.RegisterRequest;
import com.sahilkumar.api.common.exception.BusinessException;
import com.sahilkumar.api.security.JwtProperties;
import com.sahilkumar.api.security.JwtService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.username())) {
            throw new BusinessException("Username already taken", HttpStatus.CONFLICT);
        }
        User user = User.builder()
                .username(req.username())
                .passwordHash(passwordEncoder.encode(req.password()))
                .fullName(req.fullName())
                .phone(req.phone())
                .role(req.role())
                .enabled(true)
                .build();
        userRepository.save(user);
        return buildResponse(user);
    }

    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password()));
        User user = userRepository.findByUsername(req.username()).orElseThrow();
        return buildResponse(user);
    }

    private AuthResponse buildResponse(User user) {
        String token = jwtService.generateAccessToken(user, Map.of(
                "role", user.getRole().name(),
                "uid", user.getId().toString()));
        return new AuthResponse(
                token,
                "Bearer",
                jwtProperties.accessTokenTtl().toSeconds(),
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getRole());
    }
}
