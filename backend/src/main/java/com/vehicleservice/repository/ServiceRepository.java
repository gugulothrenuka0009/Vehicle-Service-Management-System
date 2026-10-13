package com.vehicleservice.repository;

import com.vehicleservice.entity.ServiceCategory;
import com.vehicleservice.entity.ServiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceItem, Long> {
    List<ServiceItem> findByActiveTrue();
    List<ServiceItem> findByCategoryAndActiveTrue(ServiceCategory category);
    Optional<ServiceItem> findByCode(String code);
    boolean existsByCode(String code);
}
