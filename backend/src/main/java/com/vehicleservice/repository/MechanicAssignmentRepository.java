package com.vehicleservice.repository;

import com.vehicleservice.entity.AssignmentStatus;
import com.vehicleservice.entity.MechanicAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MechanicAssignmentRepository extends JpaRepository<MechanicAssignment, Long> {
    List<MechanicAssignment> findByJobId(Long jobId);
    List<MechanicAssignment> findByMechanicIdAndStatus(Long mechanicId, AssignmentStatus status);
}
