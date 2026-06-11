package com.sahilkumar.api.user;

import com.sahilkumar.api.auth.User;
import com.sahilkumar.api.user.dto.ResetPasswordRequest;
import com.sahilkumar.api.user.dto.StaffCreateRequest;
import com.sahilkumar.api.user.dto.StaffUpdateRequest;
import com.sahilkumar.api.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('OWNER')")
@Tag(name = "Users")
public class UserController {

    private final UserService userService;

    @GetMapping("/staff")
    public List<UserResponse> listStaff() {
        return userService.listStaff();
    }

    @PostMapping("/staff")
    public ResponseEntity<UserResponse> createStaff(@Valid @RequestBody StaffCreateRequest req) {
        return ResponseEntity.status(201).body(userService.createStaff(req));
    }

    @PutMapping("/{id}")
    public UserResponse updateStaff(
            @PathVariable UUID id,
            @Valid @RequestBody StaffUpdateRequest req) {
        return userService.updateStaff(id, req);
    }

    @PostMapping("/{id}/enable")
    public ResponseEntity<Void> enable(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        userService.setEnabled(id, true, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/disable")
    public ResponseEntity<Void> disable(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        userService.setEnabled(id, false, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        userService.deleteStaff(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/password")
    public ResponseEntity<Void> resetPassword(
            @PathVariable UUID id,
            @Valid @RequestBody ResetPasswordRequest req) {
        userService.resetPassword(id, req.password());
        return ResponseEntity.noContent().build();
    }
}
