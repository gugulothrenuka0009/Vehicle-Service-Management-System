package com.vehicleservice.repository;

import com.vehicleservice.entity.Booking;
import com.vehicleservice.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    List<Booking> findByStatus(BookingStatus status);
    List<Booking> findByAppointmentDate(LocalDate date);
    Optional<Booking> findByBookingNumber(String bookingNumber);
    long countByStatus(BookingStatus status);
    boolean existsByVehicleIdAndAppointmentDateAndStatusNotIn(Long vehicleId, LocalDate date, List<BookingStatus> statuses);
}
