package com.vehicleservice.dto;

import com.vehicleservice.entity.ServiceCategory;
import com.vehicleservice.entity.ServiceItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class ServiceItemDto {
    private Long id;

    @NotBlank(message = "Service name is required")
    private String name;

    @NotBlank(message = "Service code is required")
    private String code;

    @NotNull(message = "Category is required")
    private ServiceCategory category;

    private String description;

    @NotNull(message = "Base price is required")
    private BigDecimal basePrice;

    private Integer estimatedDurationMinutes = 60;
    private boolean active = true;

    public ServiceItemDto() {}

    public ServiceItemDto(ServiceItem item) {
        if (item != null) {
            this.id = item.getId();
            this.name = item.getName();
            this.code = item.getCode();
            this.category = item.getCategory();
            this.description = item.getDescription();
            this.basePrice = item.getBasePrice();
            this.estimatedDurationMinutes = item.getEstimatedDurationMinutes();
            this.active = item.isActive();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public ServiceCategory getCategory() { return category; }
    public void setCategory(ServiceCategory category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }

    public Integer getEstimatedDurationMinutes() { return estimatedDurationMinutes; }
    public void setEstimatedDurationMinutes(Integer estimatedDurationMinutes) { this.estimatedDurationMinutes = estimatedDurationMinutes; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
