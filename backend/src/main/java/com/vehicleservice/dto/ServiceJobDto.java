package com.vehicleservice.dto;

import com.vehicleservice.entity.JobStatus;
import com.vehicleservice.entity.ServiceJob;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ServiceJobDto {
    private Long id;
    private String jobNumber;
    private Long bookingId;
    private String bookingNumber;
    private String customerName;
    private String customerPhone;
    private String vehicleDetails;
    private String regNumber;
    private String serviceName;
    private Long mechanicId;
    private String mechanicName;
    private JobStatus status;
    private String diagnosisNotes;
    private String workSummary;
    private BigDecimal laborCost;
    private BigDecimal partsCost;
    private BigDecimal totalJobCost;
    private LocalDateTime startTime;
    private LocalDateTime completionTime;
    private LocalDateTime createdAt;

    public ServiceJobDto() {}

    public ServiceJobDto(ServiceJob j) {
        if (j != null) {
            this.id = j.getId();
            this.jobNumber = j.getJobNumber();
            if (j.getBooking() != null) {
                this.bookingId = j.getBooking().getId();
                this.bookingNumber = j.getBooking().getBookingNumber();
                if (j.getBooking().getCustomer() != null) {
                    this.customerName = j.getBooking().getCustomer().getName();
                    this.customerPhone = j.getBooking().getCustomer().getPhone();
                }
                if (j.getBooking().getVehicle() != null) {
                    this.regNumber = j.getBooking().getVehicle().getRegNumber();
                    this.vehicleDetails = j.getBooking().getVehicle().getBrand() + " " + j.getBooking().getVehicle().getModel();
                }
                if (j.getBooking().getService() != null) {
                    this.serviceName = j.getBooking().getService().getName();
                }
            }
            if (j.getMechanic() != null) {
                this.mechanicId = j.getMechanic().getId();
                this.mechanicName = j.getMechanic().getName();
            }
            this.status = j.getStatus();
            this.diagnosisNotes = j.getDiagnosisNotes();
            this.workSummary = j.getWorkSummary();
            this.laborCost = j.getLaborCost();
            this.partsCost = j.getPartsCost();
            this.totalJobCost = j.getTotalJobCost();
            this.startTime = j.getStartTime();
            this.completionTime = j.getCompletionTime();
            this.createdAt = j.getCreatedAt();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getJobNumber() { return jobNumber; }
    public void setJobNumber(String jobNumber) { this.jobNumber = jobNumber; }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public String getBookingNumber() { return bookingNumber; }
    public void setBookingNumber(String bookingNumber) { this.bookingNumber = bookingNumber; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getVehicleDetails() { return vehicleDetails; }
    public void setVehicleDetails(String vehicleDetails) { this.vehicleDetails = vehicleDetails; }

    public String getRegNumber() { return regNumber; }
    public void setRegNumber(String regNumber) { this.regNumber = regNumber; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public Long getMechanicId() { return mechanicId; }
    public void setMechanicId(Long mechanicId) { this.mechanicId = mechanicId; }

    public String getMechanicName() { return mechanicName; }
    public void setMechanicName(String mechanicName) { this.mechanicName = mechanicName; }

    public JobStatus getStatus() { return status; }
    public void setStatus(JobStatus status) { this.status = status; }

    public String getDiagnosisNotes() { return diagnosisNotes; }
    public void setDiagnosisNotes(String diagnosisNotes) { this.diagnosisNotes = diagnosisNotes; }

    public String getWorkSummary() { return workSummary; }
    public void setWorkSummary(String workSummary) { this.workSummary = workSummary; }

    public BigDecimal getLaborCost() { return laborCost; }
    public void setLaborCost(BigDecimal laborCost) { this.laborCost = laborCost; }

    public BigDecimal getPartsCost() { return partsCost; }
    public void setPartsCost(BigDecimal partsCost) { this.partsCost = partsCost; }

    public BigDecimal getTotalJobCost() { return totalJobCost; }
    public void setTotalJobCost(BigDecimal totalJobCost) { this.totalJobCost = totalJobCost; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getCompletionTime() { return completionTime; }
    public void setCompletionTime(LocalDateTime completionTime) { this.completionTime = completionTime; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
