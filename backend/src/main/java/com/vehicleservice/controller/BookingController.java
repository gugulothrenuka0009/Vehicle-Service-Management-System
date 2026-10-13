package com.vehicleservice.controller;

import com.vehicleservice.dto.ApiResponse;
import com.vehicleservice.dto.BookingDto;
import com.vehicleservice.dto.BookingRequest;
import com.vehicleservice.entity.BookingStatus;
import com.vehicleservice.entity.Role;
import com.vehicleservice.entity.User;
import com.vehicleservice.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Bookings", description = "Appointment booking and service request scheduling")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    @Operation(summary = "Get bookings (current user bookings if Customer, all bookings if Advisor/Admin)")
    public ResponseEntity<ApiResponse<List<BookingDto>>> getBookings(@AuthenticationPrincipal User user) {
        if (user.getRole() == Role.CUSTOMER) {
            return ResponseEntity.ok(ApiResponse.ok(bookingService.getCustomerBookings(user.getId())));
        }
        return ResponseEntity.ok(ApiResponse.ok(bookingService.getAllBookings()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get booking details by ID")
    public ResponseEntity<ApiResponse<BookingDto>> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(bookingService.getBookingById(id)));
    }

    @PostMapping
    @Operation(summary = "Book a new service appointment")
    public ResponseEntity<ApiResponse<BookingDto>> createBooking(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody BookingRequest request
    ) {
        BookingDto created = bookingService.createBooking(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Booking submitted successfully", created));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel an appointment (Customer only)")
    public ResponseEntity<ApiResponse<BookingDto>> cancelBooking(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        BookingDto cancelled = bookingService.cancelBooking(id, user.getId());
        return ResponseEntity.ok(ApiResponse.ok("Booking cancelled successfully", cancelled));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SERVICE_ADVISOR', 'ADMIN')")
    @Operation(summary = "Update booking status and estimated cost (Advisor / Admin)")
    public ResponseEntity<ApiResponse<BookingDto>> updateBookingStatus(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, Object> body
    ) {
        BookingStatus status = BookingStatus.valueOf(body.get("status").toString());
        BigDecimal cost = body.get("estimatedCost") != null ?
                new BigDecimal(body.get("estimatedCost").toString()) : null;

        BookingDto updated = bookingService.updateBookingStatus(id, user.getId(), status, cost);
        return ResponseEntity.ok(ApiResponse.ok("Booking status updated", updated));
    }
}
