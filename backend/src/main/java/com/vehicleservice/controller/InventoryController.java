package com.vehicleservice.controller;

import com.vehicleservice.dto.ApiResponse;
import com.vehicleservice.dto.InventoryDto;
import com.vehicleservice.entity.TransactionType;
import com.vehicleservice.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
@Tag(name = "Inventory & Spare Parts", description = "Workshop stock management and inventory alerts")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    @Operation(summary = "Get all inventory items")
    public ResponseEntity<ApiResponse<List<InventoryDto>>> getInventory() {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getAllItems()));
    }

    @GetMapping("/active")
    @Operation(summary = "Get active spare parts (for mechanics selecting parts)")
    public ResponseEntity<ApiResponse<List<InventoryDto>>> getActiveInventory() {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getActiveItems()));
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'SERVICE_ADVISOR')")
    @Operation(summary = "Get inventory items with stock at or below reorder level")
    public ResponseEntity<ApiResponse<List<InventoryDto>>> getLowStockItems() {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getLowStockItems()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get inventory item details by ID")
    public ResponseEntity<ApiResponse<InventoryDto>> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getItemById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add a new spare part/item (Admin only)")
    public ResponseEntity<ApiResponse<InventoryDto>> createItem(@Valid @RequestBody InventoryDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Item added to inventory", inventoryService.createItem(dto)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update spare part details (Admin only)")
    public ResponseEntity<ApiResponse<InventoryDto>> updateItem(
            @PathVariable Long id,
            @Valid @RequestBody InventoryDto dto
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Item updated successfully", inventoryService.updateItem(id, dto)));
    }

    @PostMapping("/{id}/adjust")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Adjust or restock spare parts inventory (Admin only)")
    public ResponseEntity<ApiResponse<InventoryDto>> adjustStock(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body
    ) {
        TransactionType type = TransactionType.valueOf(body.get("type").toString());
        int quantity = Integer.parseInt(body.get("quantity").toString());
        String notes = body.get("notes") != null ? body.get("notes").toString() : "Admin stock adjustment";

        InventoryDto updated = inventoryService.recordTransaction(id, type, quantity, notes, null);
        return ResponseEntity.ok(ApiResponse.ok("Stock updated successfully", updated));
    }
}
