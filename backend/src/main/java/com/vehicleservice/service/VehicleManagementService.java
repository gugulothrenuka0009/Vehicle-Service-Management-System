package com.vehicleservice.service;

import com.vehicleservice.dto.VehicleDto;
import com.vehicleservice.entity.User;
import com.vehicleservice.entity.Vehicle;
import com.vehicleservice.exception.BadRequestException;
import com.vehicleservice.exception.ResourceNotFoundException;
import com.vehicleservice.repository.UserRepository;
import com.vehicleservice.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleManagementService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    public VehicleManagementService(VehicleRepository vehicleRepository, UserRepository userRepository) {
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
    }

    public List<VehicleDto> getAllVehicles() {
        return vehicleRepository.findAll().stream().map(VehicleDto::new).collect(Collectors.toList());
    }

    public List<VehicleDto> getVehiclesByUserId(Long userId) {
        return vehicleRepository.findByUserId(userId).stream().map(VehicleDto::new).collect(Collectors.toList());
    }

    public VehicleDto getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));
        return new VehicleDto(vehicle);
    }

    @Transactional
    public VehicleDto addVehicle(Long userId, VehicleDto dto) {
        if (vehicleRepository.existsByRegNumber(dto.getRegNumber().trim())) {
            throw new BadRequestException("Vehicle with registration number " + dto.getRegNumber() + " already exists");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Vehicle vehicle = new Vehicle(
                null,
                user,
                dto.getRegNumber().trim().toUpperCase(),
                dto.getBrand().trim(),
                dto.getModel().trim(),
                dto.getYear(),
                dto.getFuelType() != null ? dto.getFuelType() : "PETROL",
                dto.getMileage() != null ? dto.getMileage() : 0
        );

        return new VehicleDto(vehicleRepository.save(vehicle));
    }

    @Transactional
    public VehicleDto updateVehicle(Long id, Long userId, VehicleDto dto) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));

        // Ensure user owns vehicle or is advisor/admin
        if (userId != null && !vehicle.getUser().getId().equals(userId)) {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null && user.getRole() == com.vehicleservice.entity.Role.CUSTOMER) {
                throw new BadRequestException("You can only edit your own vehicles");
            }
        }

        if (dto.getBrand() != null) vehicle.setBrand(dto.getBrand().trim());
        if (dto.getModel() != null) vehicle.setModel(dto.getModel().trim());
        if (dto.getYear() != null) vehicle.setYear(dto.getYear());
        if (dto.getFuelType() != null) vehicle.setFuelType(dto.getFuelType());
        if (dto.getMileage() != null) vehicle.setMileage(dto.getMileage());

        return new VehicleDto(vehicleRepository.save(vehicle));
    }

    @Transactional
    public void deleteVehicle(Long id, Long userId) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));

        if (userId != null && !vehicle.getUser().getId().equals(userId)) {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null && user.getRole() == com.vehicleservice.entity.Role.CUSTOMER) {
                throw new BadRequestException("You can only delete your own vehicles");
            }
        }

        vehicleRepository.delete(vehicle);
    }
}
