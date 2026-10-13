package com.vehicleservice.controller;

import com.vehicleservice.dto.ApiResponse;
import com.vehicleservice.dto.ServiceItemDto;
import com.vehicleservice.entity.ServiceCategory;
import com.vehicleservice.service.ServiceCatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@Tag(name = "Services Catalog", description = "Service packages, pricing, and workshop catalog")
public class ServiceCatalogController {

    private final ServiceCatalogService catalogService;

    public ServiceCatalogController(ServiceCatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping
    @Operation(summary = "Get all active service packages (Public)")
    public ResponseEntity<ApiResponse<List<ServiceItemDto>>> getActiveServices(
            @RequestParam(required = false) ServiceCategory category
    ) {
        if (category != null) {
            return ResponseEntity.ok(ApiResponse.ok(catalogService.getServicesByCategory(category)));
        }
        return ResponseEntity.ok(ApiResponse.ok(catalogService.getAllActiveServices()));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVICE_ADVISOR')")
    @Operation(summary = "Get all service packages including inactive (Admin & Advisor)")
    public ResponseEntity<ApiResponse<List<ServiceItemDto>>> getAllServices() {
        return ResponseEntity.ok(ApiResponse.ok(catalogService.getAllServices()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get service package details by ID")
    public ResponseEntity<ApiResponse<ServiceItemDto>> getServiceById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(catalogService.getServiceById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new service package (Admin only)")
    public ResponseEntity<ApiResponse<ServiceItemDto>> createService(@Valid @RequestBody ServiceItemDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Service created successfully", catalogService.createService(dto)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update service package (Admin only)")
    public ResponseEntity<ApiResponse<ServiceItemDto>> updateService(
            @PathVariable Long id,
            @Valid @RequestBody ServiceItemDto dto
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Service updated successfully", catalogService.updateService(id, dto)));
    }

    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Toggle active status of service package (Admin only)")
    public ResponseEntity<ApiResponse<ServiceItemDto>> toggleServiceStatus(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Service status updated", catalogService.toggleServiceStatus(id)));
    }
}
