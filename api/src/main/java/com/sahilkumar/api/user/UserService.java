package com.sahilkumar.api.user;

import com.sahilkumar.api.auth.Role;
import com.sahilkumar.api.auth.User;
import com.sahilkumar.api.auth.UserRepository;
import com.sahilkumar.api.common.exception.BusinessException;
import com.sahilkumar.api.common.exception.ResourceNotFoundException;
import com.sahilkumar.api.user.dto.StaffCreateRequest;
import com.sahilkumar.api.user.dto.StaffUpdateRequest;
import com.sahilkumar.api.user.dto.UserResponse;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void resetPassword(UUID userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        user.setPasswordHash(passwordEncoder.encode(newPassword));
    }

    public List<UserResponse> listStaff() {
        return userRepository.findByRole(Role.STAFF, Sort.by("username")).stream()
                .map(UserResponse::from)
                .toList();
    }

    @Transactional
    public UserResponse createStaff(StaffCreateRequest req) {
        if (userRepository.existsByUsername(req.username())) {
            throw new BusinessException("Username already taken", HttpStatus.CONFLICT);
        }
        User user = User.builder()
                .username(req.username())
                .passwordHash(passwordEncoder.encode(req.password()))
                .fullName(req.fullName())
                .phone(req.phone())
                .role(Role.STAFF)
                .enabled(true)
                .build();
        userRepository.save(user);
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateStaff(UUID id, StaffUpdateRequest req) {
        User user = loadStaff(id);
        user.setFullName(req.fullName());
        user.setPhone(req.phone());
        return UserResponse.from(user);
    }

    @Transactional
    public void setEnabled(UUID id, boolean enabled, UUID actingUserId) {
        if (id.equals(actingUserId)) {
            throw new BusinessException(
                    enabled ? "Cannot enable yourself" : "Cannot disable yourself",
                    HttpStatus.CONFLICT);
        }
        User user = loadStaff(id);
        user.setEnabled(enabled);
    }

    @Transactional
    public void deleteStaff(UUID id, UUID actingUserId) {
        if (id.equals(actingUserId)) {
            throw new BusinessException("Cannot delete yourself", HttpStatus.CONFLICT);
        }
        User user = loadStaff(id);
        userRepository.delete(user);
    }

    private User loadStaff(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        if (user.getRole() != Role.STAFF) {
            throw new BusinessException(
                    "This endpoint only manages STAFF users.", HttpStatus.CONFLICT);
        }
        return user;
    }
}
