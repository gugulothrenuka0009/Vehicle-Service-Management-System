package com.vehicleservice.controller;

import com.vehicleservice.dto.ApiResponse;
import com.vehicleservice.dto.VehicleDto;
import com.vehicleservice.entity.Role;
import com.vehicleservice.entity.User;
import com.vehicleservice.service.VehicleManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@Tag(name = "Vehicles", description = "Customer and workshop vehicle management")
public class VehicleController {

    private final VehicleManagementService vehicleService;

    public VehicleController(VehicleManagementService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    @Operation(summary = "Get vehicles (filtered to current customer, or all for advisor/admin)")
    public ResponseEntity<ApiResponse<List<VehicleDto>>> getVehicles(@AuthenticationPrincipal User user) {
        if (user.getRole() == Role.CUSTOMER) {
            return ResponseEntity.ok(ApiResponse.ok(vehicleService.getVehiclesByUserId(user.getId())));
        }
        return ResponseEntity.ok(ApiResponse.ok(vehicleService.getAllVehicles()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get vehicle details by ID")
    public ResponseEntity<ApiResponse<VehicleDto>> getVehicleById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(vehicleService.getVehicleById(id)));
    }

    @PostMapping
    @Operation(summary = "Add a new vehicle")
    public ResponseEntity<ApiResponse<VehicleDto>> addVehicle(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody VehicleDto dto
    ) {
        VehicleDto created = vehicleService.addVehicle(user.getId(), dto);
        return ResponseEntity.ok(ApiResponse.ok("Vehicle registered successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update vehicle details")
    public ResponseEntity<ApiResponse<VehicleDto>> updateVehicle(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            @Valid @RequestBody VehicleDto dto
    ) {
        VehicleDto updated = vehicleService.updateVehicle(id, user.getId(), dto);
        return ResponseEntity.ok(ApiResponse.ok("Vehicle updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete vehicle")
    public ResponseEntity<ApiResponse<Void>> deleteVehicle(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        vehicleService.deleteVehicle(id, user.getId());
        return ResponseEntity.ok(ApiResponse.ok("Vehicle deleted successfully", null));
    }
}
