package com.vehicleservice.repository;

import com.vehicleservice.entity.ServiceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRecordRepository extends JpaRepository<ServiceRecord, Long> {
    List<ServiceRecord> findByCustomerIdOrderByCompletedAtDesc(Long customerId);
    List<ServiceRecord> findByVehicleIdOrderByCompletedAtDesc(Long vehicleId);
}
