package com.vehicleservice.repository;

import com.vehicleservice.entity.JobStatus;
import com.vehicleservice.entity.ServiceJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceJobRepository extends JpaRepository<ServiceJob, Long> {
    Optional<ServiceJob> findByBookingId(Long bookingId);
    Optional<ServiceJob> findByJobNumber(String jobNumber);
    List<ServiceJob> findByMechanicIdOrderByCreatedAtDesc(Long mechanicId);
    List<ServiceJob> findByStatus(JobStatus status);
    long countByStatus(JobStatus status);
}
