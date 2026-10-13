package com.vehicleservice.service;

import com.vehicleservice.dto.InventoryDto;
import com.vehicleservice.entity.*;
import com.vehicleservice.exception.BadRequestException;
import com.vehicleservice.exception.ResourceNotFoundException;
import com.vehicleservice.repository.InventoryRepository;
import com.vehicleservice.repository.InventoryTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionRepository transactionRepository;

    public InventoryService(InventoryRepository inventoryRepository,
                            InventoryTransactionRepository transactionRepository) {
        this.inventoryRepository = inventoryRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<InventoryDto> getAllItems() {
        return inventoryRepository.findAll().stream().map(InventoryDto::new).collect(Collectors.toList());
    }

    public List<InventoryDto> getActiveItems() {
        return inventoryRepository.findByActiveTrue().stream().map(InventoryDto::new).collect(Collectors.toList());
    }

    public List<InventoryDto> getLowStockItems() {
        return inventoryRepository.findLowStockItems().stream().map(InventoryDto::new).collect(Collectors.toList());
    }

    public InventoryDto getItemById(Long id) {
        Inventory item = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found with id: " + id));
        return new InventoryDto(item);
    }

    @Transactional
    public InventoryDto createItem(InventoryDto dto) {
        if (inventoryRepository.existsByItemCode(dto.getItemCode().trim())) {
            throw new BadRequestException("Inventory item with code " + dto.getItemCode() + " already exists");
        }

        Inventory item = new Inventory(
                null,
                dto.getItemCode().trim().toUpperCase(),
                dto.getItemName().trim(),
                dto.getCategory().trim(),
                dto.getUnitPrice(),
                dto.getQuantityInStock(),
                dto.getMinReorderLevel(),
                dto.getLocation() != null ? dto.getLocation() : "Shelf A1",
                true
        );

        return new InventoryDto(inventoryRepository.save(item));
    }

    @Transactional
    public InventoryDto updateItem(Long id, InventoryDto dto) {
        Inventory item = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found with id: " + id));

        if (dto.getItemName() != null) item.setItemName(dto.getItemName().trim());
        if (dto.getCategory() != null) item.setCategory(dto.getCategory().trim());
        if (dto.getUnitPrice() != null) item.setUnitPrice(dto.getUnitPrice());
        if (dto.getQuantityInStock() != null) item.setQuantityInStock(dto.getQuantityInStock());
        if (dto.getMinReorderLevel() != null) item.setMinReorderLevel(dto.getMinReorderLevel());
        if (dto.getLocation() != null) item.setLocation(dto.getLocation());
        item.setActive(dto.isActive());

        return new InventoryDto(inventoryRepository.save(item));
    }

    @Transactional
    public InventoryDto recordTransaction(Long itemId, TransactionType type, int quantity, String notes, ServiceJob job) {
        Inventory item = inventoryRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found with id: " + itemId));

        if (type == TransactionType.OUT && item.getQuantityInStock() < quantity) {
            throw new BadRequestException("Insufficient stock for item: " + item.getItemName() +
                    " (Available: " + item.getQuantityInStock() + ", Requested: " + quantity + ")");
        }

        int newQty = item.getQuantityInStock();
        if (type == TransactionType.IN) {
            newQty += quantity;
        } else if (type == TransactionType.OUT) {
            newQty -= quantity;
        } else if (type == TransactionType.ADJUSTMENT) {
            newQty = quantity;
        }
        item.setQuantityInStock(newQty);
        Inventory saved = inventoryRepository.save(item);

        InventoryTransaction tx = new InventoryTransaction(
                saved,
                job,
                type,
                quantity,
                saved.getUnitPrice(),
                notes
        );
        transactionRepository.save(tx);

        return new InventoryDto(saved);
    }
}
