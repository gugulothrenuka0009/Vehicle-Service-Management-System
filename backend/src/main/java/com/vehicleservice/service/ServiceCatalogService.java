package com.vehicleservice.service;

import com.vehicleservice.dto.ServiceItemDto;
import com.vehicleservice.entity.ServiceCategory;
import com.vehicleservice.entity.ServiceItem;
import com.vehicleservice.exception.BadRequestException;
import com.vehicleservice.exception.ResourceNotFoundException;
import com.vehicleservice.repository.ServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceCatalogService {

    private final ServiceRepository serviceRepository;

    public ServiceCatalogService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public List<ServiceItemDto> getAllActiveServices() {
        return serviceRepository.findByActiveTrue().stream().map(ServiceItemDto::new).collect(Collectors.toList());
    }

    public List<ServiceItemDto> getAllServices() {
        return serviceRepository.findAll().stream().map(ServiceItemDto::new).collect(Collectors.toList());
    }

    public List<ServiceItemDto> getServicesByCategory(ServiceCategory category) {
        return serviceRepository.findByCategoryAndActiveTrue(category).stream().map(ServiceItemDto::new).collect(Collectors.toList());
    }

    public ServiceItemDto getServiceById(Long id) {
        ServiceItem item = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));
        return new ServiceItemDto(item);
    }

    @Transactional
    public ServiceItemDto createService(ServiceItemDto dto) {
        if (serviceRepository.existsByCode(dto.getCode().trim())) {
            throw new BadRequestException("Service with code " + dto.getCode() + " already exists");
        }

        ServiceItem item = new ServiceItem(
                null,
                dto.getName().trim(),
                dto.getCode().trim().toUpperCase(),
                dto.getCategory(),
                dto.getDescription(),
                dto.getBasePrice(),
                dto.getEstimatedDurationMinutes() != null ? dto.getEstimatedDurationMinutes() : 60,
                true
        );

        return new ServiceItemDto(serviceRepository.save(item));
    }

    @Transactional
    public ServiceItemDto updateService(Long id, ServiceItemDto dto) {
        ServiceItem item = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));

        if (dto.getName() != null) item.setName(dto.getName());
        if (dto.getCategory() != null) item.setCategory(dto.getCategory());
        if (dto.getDescription() != null) item.setDescription(dto.getDescription());
        if (dto.getBasePrice() != null) item.setBasePrice(dto.getBasePrice());
        if (dto.getEstimatedDurationMinutes() != null) item.setEstimatedDurationMinutes(dto.getEstimatedDurationMinutes());
        item.setActive(dto.isActive());

        return new ServiceItemDto(serviceRepository.save(item));
    }

    @Transactional
    public ServiceItemDto toggleServiceStatus(Long id) {
        ServiceItem item = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));
        item.setActive(!item.isActive());
        return new ServiceItemDto(serviceRepository.save(item));
    }
}
