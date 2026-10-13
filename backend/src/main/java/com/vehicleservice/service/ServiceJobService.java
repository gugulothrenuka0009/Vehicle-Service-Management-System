package com.vehicleservice.service;

import com.vehicleservice.dto.JobUpdateDto;
import com.vehicleservice.dto.ServiceJobDto;
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
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ServiceJobService {

    private final ServiceJobRepository serviceJobRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final MechanicAssignmentRepository assignmentRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryService inventoryService;
    private final BillingService billingService;
    private final ServiceRecordRepository serviceRecordRepository;
    private final NotificationService notificationService;

    public ServiceJobService(ServiceJobRepository serviceJobRepository,
                             BookingRepository bookingRepository,
                             UserRepository userRepository,
                             MechanicAssignmentRepository assignmentRepository,
                             InventoryRepository inventoryRepository,
                             InventoryService inventoryService,
                             BillingService billingService,
                             ServiceRecordRepository serviceRecordRepository,
                             NotificationService notificationService) {
        this.serviceJobRepository = serviceJobRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
        this.inventoryRepository = inventoryRepository;
        this.inventoryService = inventoryService;
        this.billingService = billingService;
        this.serviceRecordRepository = serviceRecordRepository;
        this.notificationService = notificationService;
    }

    public List<ServiceJobDto> getAllJobs() {
        return serviceJobRepository.findAll().stream().map(ServiceJobDto::new).collect(Collectors.toList());
    }

    public List<ServiceJobDto> getJobsByMechanic(Long mechanicId) {
        return serviceJobRepository.findByMechanicIdOrderByCreatedAtDesc(mechanicId).stream()
                .map(ServiceJobDto::new)
                .collect(Collectors.toList());
    }

    public ServiceJobDto getJobById(Long id) {
        ServiceJob job = serviceJobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service job not found with id: " + id));
        return new ServiceJobDto(job);
    }

    public Optional<ServiceJobDto> getJobByBookingId(Long bookingId) {
        return serviceJobRepository.findByBookingId(bookingId).map(ServiceJobDto::new);
    }

    @Transactional
    public ServiceJobDto assignJob(Long bookingId, Long mechanicId, Long advisorId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        User mechanic = userRepository.findById(mechanicId)
                .orElseThrow(() -> new ResourceNotFoundException("Mechanic not found with id: " + mechanicId));

        if (mechanic.getRole() != Role.MECHANIC) {
            throw new BadRequestException("User " + mechanic.getName() + " is not a mechanic");
        }

        User advisor = advisorId != null ? userRepository.findById(advisorId).orElse(null) : null;

        // Check if job exists for this booking or create new
        ServiceJob job = serviceJobRepository.findByBookingId(bookingId).orElseGet(() -> {
            ServiceJob newJob = new ServiceJob();
            String jobNumber = "JOB-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" +
                    UUID.randomUUID().toString().substring(0, 5).toUpperCase();
            newJob.setJobNumber(jobNumber);
            newJob.setBooking(booking);
            return newJob;
        });

        job.setMechanic(mechanic);
        job.setStatus(JobStatus.ASSIGNED);
        ServiceJob savedJob = serviceJobRepository.save(job);

        // Update booking status
        booking.setStatus(BookingStatus.IN_PROGRESS);
        if (advisor != null) {
            booking.setAdvisor(advisor);
        }
        bookingRepository.save(booking);

        // Create assignment record
        if (advisor != null) {
            MechanicAssignment assignment = new MechanicAssignment(savedJob, mechanic, advisor);
            assignmentRepository.save(assignment);
        }

        // Notify Mechanic
        notificationService.createNotification(
                mechanic,
                "New Job Assigned",
                "You have been assigned job " + savedJob.getJobNumber() + " for " +
                        booking.getVehicle().getBrand() + " " + booking.getVehicle().getModel() +
                        " (" + booking.getVehicle().getRegNumber() + ").",
                NotificationType.ASSIGNMENT
        );

        // Notify Customer
        notificationService.createNotification(
                booking.getCustomer(),
                "Mechanic Assigned",
                "Mechanic " + mechanic.getName() + " has been assigned to inspect and service your vehicle " +
                        booking.getVehicle().getRegNumber() + ".",
                NotificationType.STATUS_UPDATE
        );

        return new ServiceJobDto(savedJob);
    }

    @Transactional
    public ServiceJobDto updateJob(Long jobId, Long mechanicId, JobUpdateDto dto) {
        ServiceJob job = serviceJobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Service job not found with id: " + jobId));

        // Ensure mechanic is only editing their own job unless admin/advisor
        if (mechanicId != null && job.getMechanic() != null && !job.getMechanic().getId().equals(mechanicId)) {
            User user = userRepository.findById(mechanicId).orElse(null);
            if (user != null && user.getRole() == Role.MECHANIC) {
                throw new BadRequestException("You can only modify jobs assigned to you");
            }
        }

        if (dto.getStatus() != null) {
            job.setStatus(dto.getStatus());
            if (dto.getStatus() == JobStatus.IN_PROGRESS && job.getStartTime() == null) {
                job.setStartTime(LocalDateTime.now());
            }
        }

        if (dto.getDiagnosisNotes() != null) job.setDiagnosisNotes(dto.getDiagnosisNotes());
        if (dto.getWorkSummary() != null) job.setWorkSummary(dto.getWorkSummary());
        if (dto.getLaborCost() != null) job.setLaborCost(dto.getLaborCost());

        // Process parts used if provided
        if (dto.getPartsUsed() != null && !dto.getPartsUsed().isEmpty()) {
            BigDecimal partsCostAccum = job.getPartsCost() != null ? job.getPartsCost() : BigDecimal.ZERO;
            for (JobUpdateDto.PartUsageItem partItem : dto.getPartsUsed()) {
                if (partItem.getInventoryId() != null && partItem.getQuantity() != null && partItem.getQuantity() > 0) {
                    Inventory inv = inventoryRepository.findById(partItem.getInventoryId())
                            .orElseThrow(() -> new ResourceNotFoundException("Part not found with id: " + partItem.getInventoryId()));
                    inventoryService.recordTransaction(
                            inv.getId(),
                            TransactionType.OUT,
                            partItem.getQuantity(),
                            "Used for job " + job.getJobNumber(),
                            job
                    );
                    BigDecimal itemTotal = inv.getUnitPrice().multiply(new BigDecimal(partItem.getQuantity()));
                    partsCostAccum = partsCostAccum.add(itemTotal);
                }
            }
            job.setPartsCost(partsCostAccum);
        } else if (dto.getPartsCost() != null) {
            job.setPartsCost(dto.getPartsCost());
        }

        BigDecimal labor = job.getLaborCost() != null ? job.getLaborCost() : BigDecimal.ZERO;
        BigDecimal parts = job.getPartsCost() != null ? job.getPartsCost() : BigDecimal.ZERO;
        job.setTotalJobCost(labor.add(parts));

        // When marked COMPLETED
        if (dto.getStatus() == JobStatus.COMPLETED) {
            job.setCompletionTime(LocalDateTime.now());
            Booking booking = job.getBooking();
            booking.setStatus(BookingStatus.COMPLETED);
            bookingRepository.save(booking);

            // Generate invoice automatically
            billingService.generateInvoice(booking, job.getPartsCost(), job.getLaborCost());

            // Save service record for vehicle history
            ServiceRecord record = new ServiceRecord();
            record.setBooking(booking);
            record.setVehicle(booking.getVehicle());
            record.setCustomer(booking.getCustomer());
            record.setCompletedAt(LocalDateTime.now());
            record.setSummaryNotes(job.getWorkSummary() != null ? job.getWorkSummary() : "Service completed successfully.");
            record.setFinalMileage(booking.getVehicle().getMileage());
            record.setTotalAmount(job.getTotalJobCost());
            serviceRecordRepository.save(record);

            // Notify Customer
            notificationService.createNotification(
                    booking.getCustomer(),
                    "Service Work Completed",
                    "Service on vehicle " + booking.getVehicle().getRegNumber() +
                            " has been completed by mechanic. Invoice is now ready for payment.",
                    NotificationType.STATUS_UPDATE
            );
        }

        return new ServiceJobDto(serviceJobRepository.save(job));
    }
}
