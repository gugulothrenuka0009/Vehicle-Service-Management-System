package com.vehicleservice.dto;

import com.vehicleservice.entity.Inventory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InventoryDto {
    private Long id;

    @NotBlank(message = "Item code is required")
    private String itemCode;

    @NotBlank(message = "Item name is required")
    private String itemName;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Unit price is required")
    private BigDecimal unitPrice;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantityInStock;

    @NotNull(message = "Reorder level is required")
    private Integer minReorderLevel;

    private String location;
    private boolean active = true;
    private boolean lowStock;
    private LocalDateTime createdAt;

    public InventoryDto() {}

    public InventoryDto(Inventory i) {
        if (i != null) {
            this.id = i.getId();
            this.itemCode = i.getItemCode();
            this.itemName = i.getItemName();
            this.category = i.getCategory();
            this.unitPrice = i.getUnitPrice();
            this.quantityInStock = i.getQuantityInStock();
            this.minReorderLevel = i.getMinReorderLevel();
            this.location = i.getLocation();
            this.active = i.isActive();
            this.lowStock = i.isLowStock();
            this.createdAt = i.getCreatedAt();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getItemCode() { return itemCode; }
    public void setItemCode(String itemCode) { this.itemCode = itemCode; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public Integer getQuantityInStock() { return quantityInStock; }
    public void setQuantityInStock(Integer quantityInStock) { this.quantityInStock = quantityInStock; }

    public Integer getMinReorderLevel() { return minReorderLevel; }
    public void setMinReorderLevel(Integer minReorderLevel) { this.minReorderLevel = minReorderLevel; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public boolean isLowStock() { return lowStock; }
    public void setLowStock(boolean lowStock) { this.lowStock = lowStock; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
