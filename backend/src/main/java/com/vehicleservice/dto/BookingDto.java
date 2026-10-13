package com.vehicleservice.dto;

import com.vehicleservice.entity.Booking;
import com.vehicleservice.entity.BookingStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BookingDto {
    private Long id;
    private String bookingNumber;
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private Long vehicleId;
    private String vehicleDetails;
    private String regNumber;
    private Long serviceId;
    private String serviceName;
    private Long advisorId;
    private String advisorName;
    private LocalDate appointmentDate;
    private String appointmentTime;
    private String notes;
    private BookingStatus status;
    private BigDecimal estimatedCost;
    private LocalDateTime createdAt;

    public BookingDto() {}

    public BookingDto(Booking b) {
        if (b != null) {
            this.id = b.getId();
            this.bookingNumber = b.getBookingNumber();
            if (b.getCustomer() != null) {
                this.customerId = b.getCustomer().getId();
                this.customerName = b.getCustomer().getName();
                this.customerEmail = b.getCustomer().getEmail();
                this.customerPhone = b.getCustomer().getPhone();
            }
            if (b.getVehicle() != null) {
                this.vehicleId = b.getVehicle().getId();
                this.regNumber = b.getVehicle().getRegNumber();
                this.vehicleDetails = b.getVehicle().getBrand() + " " + b.getVehicle().getModel() + " (" + b.getVehicle().getYear() + ")";
            }
            if (b.getService() != null) {
                this.serviceId = b.getService().getId();
                this.serviceName = b.getService().getName();
            }
            if (b.getAdvisor() != null) {
                this.advisorId = b.getAdvisor().getId();
                this.advisorName = b.getAdvisor().getName();
            }
            this.appointmentDate = b.getAppointmentDate();
            this.appointmentTime = b.getAppointmentTime();
            this.notes = b.getNotes();
            this.status = b.getStatus();
            this.estimatedCost = b.getEstimatedCost();
            this.createdAt = b.getCreatedAt();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBookingNumber() { return bookingNumber; }
    public void setBookingNumber(String bookingNumber) { this.bookingNumber = bookingNumber; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }

    public String getVehicleDetails() { return vehicleDetails; }
    public void setVehicleDetails(String vehicleDetails) { this.vehicleDetails = vehicleDetails; }

    public String getRegNumber() { return regNumber; }
    public void setRegNumber(String regNumber) { this.regNumber = regNumber; }

    public Long getServiceId() { return serviceId; }
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public Long getAdvisorId() { return advisorId; }
    public void setAdvisorId(Long advisorId) { this.advisorId = advisorId; }

    public String getAdvisorName() { return advisorName; }
    public void setAdvisorName(String advisorName) { this.advisorName = advisorName; }

    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }

    public String getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(String appointmentTime) { this.appointmentTime = appointmentTime; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public BigDecimal getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(BigDecimal estimatedCost) { this.estimatedCost = estimatedCost; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
