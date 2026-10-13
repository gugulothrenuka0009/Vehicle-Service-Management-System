package com.vehicleservice.controller;

import com.vehicleservice.dto.ApiResponse;
import com.vehicleservice.dto.InvoiceDto;
import com.vehicleservice.dto.PaymentDto;
import com.vehicleservice.dto.PaymentRequest;
import com.vehicleservice.entity.Role;
import com.vehicleservice.entity.User;
import com.vehicleservice.service.BillingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/billing")
@Tag(name = "Billing & Payments", description = "Invoicing, simulated payment gateway, and payment records")
public class BillingController {

    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @GetMapping("/invoices")
    @Operation(summary = "Get invoices (current user invoices if customer, all for advisor/admin)")
    public ResponseEntity<ApiResponse<List<InvoiceDto>>> getInvoices(@AuthenticationPrincipal User user) {
        if (user.getRole() == Role.CUSTOMER) {
            return ResponseEntity.ok(ApiResponse.ok(billingService.getCustomerInvoices(user.getId())));
        }
        return ResponseEntity.ok(ApiResponse.ok(billingService.getAllInvoices()));
    }

    @GetMapping("/invoices/{id}")
    @Operation(summary = "Get invoice by ID")
    public ResponseEntity<ApiResponse<InvoiceDto>> getInvoiceById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(billingService.getInvoiceById(id)));
    }

    @GetMapping("/invoices/booking/{bookingId}")
    @Operation(summary = "Get invoice by booking ID")
    public ResponseEntity<ApiResponse<InvoiceDto>> getInvoiceByBooking(@PathVariable Long bookingId) {
        return billingService.getInvoiceByBookingId(bookingId)
                .map(inv -> ResponseEntity.ok(ApiResponse.ok(inv)))
                .orElse(ResponseEntity.ok(ApiResponse.ok("No invoice generated yet", null)));
    }

    @PostMapping("/pay")
    @Operation(summary = "Simulated payment gateway processing (Cash, Card, UPI)")
    public ResponseEntity<ApiResponse<PaymentDto>> processPayment(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody PaymentRequest request
    ) {
        PaymentDto payment = billingService.processPayment(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Payment confirmed successfully", payment));
    }

    @GetMapping("/payments")
    @Operation(summary = "Get payment transaction history")
    public ResponseEntity<ApiResponse<List<PaymentDto>>> getPayments(@AuthenticationPrincipal User user) {
        if (user.getRole() == Role.CUSTOMER) {
            return ResponseEntity.ok(ApiResponse.ok(billingService.getCustomerPayments(user.getId())));
        }
        return ResponseEntity.ok(ApiResponse.ok(billingService.getAllPayments()));
    }
}
