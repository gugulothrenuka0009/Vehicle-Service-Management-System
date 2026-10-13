package com.vehicleservice.dto;

import com.vehicleservice.entity.Vehicle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class VehicleDto {
    private Long id;
    private Long userId;
    private String ownerName;

    @NotBlank(message = "Registration number is required")
    private String regNumber;

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Model is required")
    private String model;

    @NotNull(message = "Year is required")
    private Integer year;

    private String fuelType = "PETROL";
    private Integer mileage = 0;
    private LocalDateTime createdAt;

    public VehicleDto() {}

    public VehicleDto(Vehicle vehicle) {
        if (vehicle != null) {
            this.id = vehicle.getId();
            if (vehicle.getUser() != null) {
                this.userId = vehicle.getUser().getId();
                this.ownerName = vehicle.getUser().getName();
            }
            this.regNumber = vehicle.getRegNumber();
            this.brand = vehicle.getBrand();
            this.model = vehicle.getModel();
            this.year = vehicle.getYear();
            this.fuelType = vehicle.getFuelType();
            this.mileage = vehicle.getMileage();
            this.createdAt = vehicle.getCreatedAt();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getRegNumber() { return regNumber; }
    public void setRegNumber(String regNumber) { this.regNumber = regNumber; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }

    public Integer getMileage() { return mileage; }
    public void setMileage(Integer mileage) { this.mileage = mileage; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
