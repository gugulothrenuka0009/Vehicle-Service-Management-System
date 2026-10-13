package com.vehicleservice.dto;

import java.math.BigDecimal;
import java.util.Map;

public class DashboardStatsDto {
    private long totalBookings;
    private long pendingBookings;
    private long inProgressJobs;
    private long completedJobs;
    private long totalVehicles;
    private long totalCustomers;
    private long totalMechanics;
    private long lowStockCount;
    private BigDecimal totalRevenue = BigDecimal.ZERO;
    private Map<String, Long> bookingsByStatus;

    public DashboardStatsDto() {}

    public long getTotalBookings() { return totalBookings; }
    public void setTotalBookings(long totalBookings) { this.totalBookings = totalBookings; }

    public long getPendingBookings() { return pendingBookings; }
    public void setPendingBookings(long pendingBookings) { this.pendingBookings = pendingBookings; }

    public long getInProgressJobs() { return inProgressJobs; }
    public void setInProgressJobs(long inProgressJobs) { this.inProgressJobs = inProgressJobs; }

    public long getCompletedJobs() { return completedJobs; }
    public void setCompletedJobs(long completedJobs) { this.completedJobs = completedJobs; }

    public long getTotalVehicles() { return totalVehicles; }
    public void setTotalVehicles(long totalVehicles) { this.totalVehicles = totalVehicles; }

    public long getTotalCustomers() { return totalCustomers; }
    public void setTotalCustomers(long totalCustomers) { this.totalCustomers = totalCustomers; }

    public long getTotalMechanics() { return totalMechanics; }
    public void setTotalMechanics(long totalMechanics) { this.totalMechanics = totalMechanics; }

    public long getLowStockCount() { return lowStockCount; }
    public void setLowStockCount(long lowStockCount) { this.lowStockCount = lowStockCount; }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

    public Map<String, Long> getBookingsByStatus() { return bookingsByStatus; }
    public void setBookingsByStatus(Map<String, Long> bookingsByStatus) { this.bookingsByStatus = bookingsByStatus; }
}
