package com.vehicleservice.service;

import com.vehicleservice.dto.BookingDto;
import com.vehicleservice.dto.BookingRequest;
import com.vehicleservice.entity.*;
import com.vehicleservice.exception.BadRequestException;
import com.vehicleservice.exception.ResourceNotFoundException;
import com.vehicleservice.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final VehicleRepository vehicleRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public BookingService(BookingRepository bookingRepository,
                          VehicleRepository vehicleRepository,
                          ServiceRepository serviceRepository,
                          UserRepository userRepository,
                          NotificationService notificationService) {
        this.bookingRepository = bookingRepository;
        this.vehicleRepository = vehicleRepository;
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public List<BookingDto> getAllBookings() {
        return bookingRepository.findAll().stream().map(BookingDto::new).collect(Collectors.toList());
    }

    public List<BookingDto> getCustomerBookings(Long customerId) {
        return bookingRepository.findByCustomerIdOrderByCreatedAtDesc(customerId).stream()
                .map(BookingDto::new)
                .collect(Collectors.toList());
    }

    public BookingDto getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
        return new BookingDto(booking);
    }

    @Transactional
    public BookingDto createBooking(Long customerId, BookingRequest request) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + request.getVehicleId()));

        if (!vehicle.getUser().getId().equals(customerId)) {
            throw new BadRequestException("You can only book appointments for your registered vehicles");
        }

        ServiceItem serviceItem = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + request.getServiceId()));

        // Check if vehicle already has an active booking on that date
        boolean hasExisting = bookingRepository.existsByVehicleIdAndAppointmentDateAndStatusNotIn(
                vehicle.getId(),
                request.getAppointmentDate(),
                List.of(BookingStatus.CANCELLED, BookingStatus.REJECTED, BookingStatus.COMPLETED)
        );
        if (hasExisting) {
            throw new BadRequestException("Vehicle " + vehicle.getRegNumber() + " already has an active booking on " + request.getAppointmentDate());
        }

        String bookingNumber = "BK-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" +
                UUID.randomUUID().toString().substring(0, 5).toUpperCase();

        Booking booking = new Booking();
        booking.setBookingNumber(bookingNumber);
        booking.setCustomer(customer);
        booking.setVehicle(vehicle);
        booking.setService(serviceItem);
        booking.setAppointmentDate(request.getAppointmentDate());
        booking.setAppointmentTime(request.getAppointmentTime());
        booking.setNotes(request.getNotes());
        booking.setStatus(BookingStatus.PENDING);
        booking.setEstimatedCost(serviceItem.getBasePrice());

        Booking savedBooking = bookingRepository.save(booking);

        // Send confirmation notification
        notificationService.createNotification(
                customer,
                "Booking Submitted",
                "Your booking " + bookingNumber + " for " + vehicle.getBrand() + " " + vehicle.getModel() +
                        " has been placed for " + request.getAppointmentDate() + " at " + request.getAppointmentTime() + ".",
                NotificationType.BOOKING
        );

        return new BookingDto(savedBooking);
    }

    @Transactional
    public BookingDto cancelBooking(Long bookingId, Long customerId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (!booking.getCustomer().getId().equals(customerId)) {
            throw new BadRequestException("You can only cancel your own bookings");
        }

        if (booking.getStatus() != BookingStatus.PENDING && booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BadRequestException("Cannot cancel booking in status: " + booking.getStatus());
        }

        booking.setStatus(BookingStatus.CANCELLED);
        Booking updated = bookingRepository.save(booking);

        notificationService.createNotification(
                booking.getCustomer(),
                "Booking Cancelled",
                "Your booking " + booking.getBookingNumber() + " has been cancelled.",
                NotificationType.BOOKING
        );

        return new BookingDto(updated);
    }

    @Transactional
    public BookingDto updateBookingStatus(Long bookingId, Long advisorId, BookingStatus newStatus, BigDecimal estimatedCost) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (advisorId != null) {
            User advisor = userRepository.findById(advisorId).orElse(null);
            if (advisor != null) {
                booking.setAdvisor(advisor);
            }
        }

        if (estimatedCost != null && estimatedCost.compareTo(BigDecimal.ZERO) >= 0) {
            booking.setEstimatedCost(estimatedCost);
        }

        booking.setStatus(newStatus);
        Booking updated = bookingRepository.save(booking);

        notificationService.createNotification(
                booking.getCustomer(),
                "Booking Status Updated",
                "Your booking " + booking.getBookingNumber() + " status is now " + newStatus.name() + ".",
                NotificationType.STATUS_UPDATE
        );

        return new BookingDto(updated);
    }
}
