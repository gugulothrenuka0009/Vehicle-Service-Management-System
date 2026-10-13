package com.vehicleservice.controller;

import com.vehicleservice.dto.ApiResponse;
import com.vehicleservice.dto.RegisterRequest;
import com.vehicleservice.dto.UserDto;
import com.vehicleservice.entity.Role;
import com.vehicleservice.entity.User;
import com.vehicleservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User management and profile APIs")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/mechanics")
    @Operation(summary = "Get list of active mechanics (for job assignment)")
    public ResponseEntity<ApiResponse<List<UserDto>>> getMechanics() {
        return ResponseEntity.ok(ApiResponse.ok(userService.getActiveMechanics()));
    }

    @GetMapping("/advisors")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get list of service advisors")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAdvisors() {
        return ResponseEntity.ok(ApiResponse.ok(userService.getUsersByRole(Role.SERVICE_ADVISOR)));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update current user profile")
    public ResponseEntity<ApiResponse<UserDto>> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, String> body
    ) {
        UserDto updated = userService.updateProfile(user.getId(), body.get("name"), body.get("phone"));
        return ResponseEntity.ok(ApiResponse.ok("Profile updated successfully", updated));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users (Admin only)")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.ok(userService.getAllUsers()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create user account with specific role (Admin only)")
    public ResponseEntity<ApiResponse<UserDto>> createUser(@Valid @RequestBody RegisterRequest request) {
        UserDto created = userService.createUser(request);
        return ResponseEntity.ok(ApiResponse.ok("User created successfully", created));
    }

    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activate or deactivate user account (Admin only)")
    public ResponseEntity<ApiResponse<UserDto>> toggleUserStatus(@PathVariable Long id) {
        UserDto updated = userService.toggleUserStatus(id);
        return ResponseEntity.ok(ApiResponse.ok("User status updated", updated));
    }
}
