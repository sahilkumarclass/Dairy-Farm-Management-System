package com.sahilkumar.api.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminBootstrap implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username:admin}")
    private String username;

    @Value("${app.admin.password:admin123}")
    private String password;

    @Value("${app.admin.fullName:Farm Owner}")
    private String fullName;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.existsByUsername(username)) {
            return;
        }
        User admin = User.builder()
                .username(username)
                .passwordHash(passwordEncoder.encode(password))
                .fullName(fullName)
                .role(Role.OWNER)
                .enabled(true)
                .build();
        userRepository.save(admin);
        log.warn("Bootstrapped admin user '{}' — change the password immediately.", username);
    }
}
