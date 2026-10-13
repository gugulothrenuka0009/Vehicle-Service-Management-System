package com.vehicleservice.dto;

import com.vehicleservice.entity.Invoice;
import com.vehicleservice.entity.InvoiceStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class InvoiceDto {
    private Long id;
    private String invoiceNumber;
    private Long bookingId;
    private String bookingNumber;
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private Long vehicleId;
    private String vehicleDetails;
    private String regNumber;
    private String serviceName;
    private BigDecimal partsTotal;
    private BigDecimal laborTotal;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private InvoiceStatus status;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private LocalDateTime createdAt;

    public InvoiceDto() {}

    public InvoiceDto(Invoice inv) {
        if (inv != null) {
            this.id = inv.getId();
            this.invoiceNumber = inv.getInvoiceNumber();
            if (inv.getBooking() != null) {
                this.bookingId = inv.getBooking().getId();
                this.bookingNumber = inv.getBooking().getBookingNumber();
                if (inv.getBooking().getService() != null) {
                    this.serviceName = inv.getBooking().getService().getName();
                }
            }
            if (inv.getCustomer() != null) {
                this.customerId = inv.getCustomer().getId();
                this.customerName = inv.getCustomer().getName();
                this.customerEmail = inv.getCustomer().getEmail();
                this.customerPhone = inv.getCustomer().getPhone();
            }
            if (inv.getVehicle() != null) {
                this.vehicleId = inv.getVehicle().getId();
                this.regNumber = inv.getVehicle().getRegNumber();
                this.vehicleDetails = inv.getVehicle().getBrand() + " " + inv.getVehicle().getModel() + " (" + inv.getVehicle().getYear() + ")";
            }
            this.partsTotal = inv.getPartsTotal();
            this.laborTotal = inv.getLaborTotal();
            this.taxAmount = inv.getTaxAmount();
            this.discountAmount = inv.getDiscountAmount();
            this.finalAmount = inv.getFinalAmount();
            this.status = inv.getStatus();
            this.invoiceDate = inv.getInvoiceDate();
            this.dueDate = inv.getDueDate();
            this.createdAt = inv.getCreatedAt();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

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

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public BigDecimal getPartsTotal() { return partsTotal; }
    public void setPartsTotal(BigDecimal partsTotal) { this.partsTotal = partsTotal; }

    public BigDecimal getLaborTotal() { return laborTotal; }
    public void setLaborTotal(BigDecimal laborTotal) { this.laborTotal = laborTotal; }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public BigDecimal getFinalAmount() { return finalAmount; }
    public void setFinalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; }

    public InvoiceStatus getStatus() { return status; }
    public void setStatus(InvoiceStatus status) { this.status = status; }

    public LocalDate getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(LocalDate invoiceDate) { this.invoiceDate = invoiceDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
