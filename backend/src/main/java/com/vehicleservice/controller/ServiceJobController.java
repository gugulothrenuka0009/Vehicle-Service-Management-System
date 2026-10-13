package com.vehicleservice.controller;

import com.vehicleservice.dto.ApiResponse;
import com.vehicleservice.dto.JobUpdateDto;
import com.vehicleservice.dto.ServiceJobDto;
import com.vehicleservice.entity.Role;
import com.vehicleservice.entity.User;
import com.vehicleservice.service.ServiceJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
@Tag(name = "Service Jobs", description = "Mechanic repair jobs and work orders")
public class ServiceJobController {

    private final ServiceJobService serviceJobService;

    public ServiceJobController(ServiceJobService serviceJobService) {
        this.serviceJobService = serviceJobService;
    }

    @GetMapping
    @Operation(summary = "Get jobs (assigned to logged-in mechanic, or all for advisor/admin)")
    public ResponseEntity<ApiResponse<List<ServiceJobDto>>> getJobs(@AuthenticationPrincipal User user) {
        if (user.getRole() == Role.MECHANIC) {
            return ResponseEntity.ok(ApiResponse.ok(serviceJobService.getJobsByMechanic(user.getId())));
        }
        return ResponseEntity.ok(ApiResponse.ok(serviceJobService.getAllJobs()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get service job details by ID")
    public ResponseEntity<ApiResponse<ServiceJobDto>> getJobById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(serviceJobService.getJobById(id)));
    }

    @GetMapping("/booking/{bookingId}")
    @Operation(summary = "Get service job by booking ID")
    public ResponseEntity<ApiResponse<ServiceJobDto>> getJobByBookingId(@PathVariable Long bookingId) {
        return serviceJobService.getJobByBookingId(bookingId)
                .map(job -> ResponseEntity.ok(ApiResponse.ok(job)))
                .orElse(ResponseEntity.ok(ApiResponse.ok("No job created yet", null)));
    }

    @PostMapping("/assign")
    @PreAuthorize("hasAnyRole('SERVICE_ADVISOR', 'ADMIN')")
    @Operation(summary = "Assign a booking to a mechanic (Advisor / Admin)")
    public ResponseEntity<ApiResponse<ServiceJobDto>> assignJob(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, Long> payload
    ) {
        Long bookingId = payload.get("bookingId");
        Long mechanicId = payload.get("mechanicId");
        ServiceJobDto job = serviceJobService.assignJob(bookingId, mechanicId, user.getId());
        return ResponseEntity.ok(ApiResponse.ok("Job assigned successfully", job));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update job status, diagnosis notes, work details, and used parts (Mechanic)")
    public ResponseEntity<ApiResponse<ServiceJobDto>> updateJob(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            @RequestBody JobUpdateDto dto
    ) {
        ServiceJobDto updated = serviceJobService.updateJob(id, user.getId(), dto);
        return ResponseEntity.ok(ApiResponse.ok("Job updated successfully", updated));
    }
}
