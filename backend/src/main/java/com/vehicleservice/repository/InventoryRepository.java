package com.vehicleservice.repository;

import com.vehicleservice.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByItemCode(String itemCode);
    boolean existsByItemCode(String itemCode);
    List<Inventory> findByActiveTrue();

    @Query("SELECT i FROM Inventory i WHERE i.active = true AND i.quantityInStock <= i.minReorderLevel")
    List<Inventory> findLowStockItems();
}
