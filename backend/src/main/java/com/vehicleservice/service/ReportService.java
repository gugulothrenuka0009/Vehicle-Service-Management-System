package com.vehicleservice.service;

import com.vehicleservice.dto.DashboardStatsDto;
import com.vehicleservice.entity.BookingStatus;
import com.vehicleservice.entity.JobStatus;
import com.vehicleservice.entity.Role;
import com.vehicleservice.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class ReportService {

    private final BookingRepository bookingRepository;
    private final ServiceJobRepository serviceJobRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final InventoryRepository inventoryRepository;
    private final InvoiceRepository invoiceRepository;

    public ReportService(BookingRepository bookingRepository,
                         ServiceJobRepository serviceJobRepository,
                         UserRepository userRepository,
                         VehicleRepository vehicleRepository,
                         InventoryRepository inventoryRepository,
                         InvoiceRepository invoiceRepository) {
        this.bookingRepository = bookingRepository;
        this.serviceJobRepository = serviceJobRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.inventoryRepository = inventoryRepository;
        this.invoiceRepository = invoiceRepository;
    }

    public DashboardStatsDto getDashboardStats() {
        DashboardStatsDto stats = new DashboardStatsDto();

        stats.setTotalBookings(bookingRepository.count());
        stats.setPendingBookings(bookingRepository.countByStatus(BookingStatus.PENDING));
        stats.setInProgressJobs(serviceJobRepository.countByStatus(JobStatus.IN_PROGRESS));
        stats.setCompletedJobs(serviceJobRepository.countByStatus(JobStatus.COMPLETED));

        stats.setTotalVehicles(vehicleRepository.count());
        stats.setTotalCustomers(userRepository.findAllByRole(Role.CUSTOMER).size());
        stats.setTotalMechanics(userRepository.findAllByRole(Role.MECHANIC).size());
        stats.setLowStockCount(inventoryRepository.findLowStockItems().size());

        BigDecimal revenue = invoiceRepository.calculateTotalRevenue();
        stats.setTotalRevenue(revenue != null ? revenue : BigDecimal.ZERO);

        Map<String, Long> statusMap = new HashMap<>();
        for (BookingStatus bs : BookingStatus.values()) {
            statusMap.put(bs.name(), bookingRepository.countByStatus(bs));
        }
        stats.setBookingsByStatus(statusMap);

        return stats;
    }
}
