package com.vehicleservice.repository;

import com.vehicleservice.entity.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {
    List<InventoryTransaction> findByItemIdOrderByCreatedAtDesc(Long itemId);
    List<InventoryTransaction> findByJobId(Long jobId);
}
