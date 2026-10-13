package com.vehicleservice.dto;

import com.vehicleservice.entity.Payment;
import com.vehicleservice.entity.PaymentMethod;
import com.vehicleservice.entity.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentDto {
    private Long id;
    private String paymentReference;
    private Long invoiceId;
    private String invoiceNumber;
    private Long customerId;
    private String customerName;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private LocalDateTime transactionDate;
    private String notes;

    public PaymentDto() {}

    public PaymentDto(Payment p) {
        if (p != null) {
            this.id = p.getId();
            this.paymentReference = p.getPaymentReference();
            if (p.getInvoice() != null) {
                this.invoiceId = p.getInvoice().getId();
                this.invoiceNumber = p.getInvoice().getInvoiceNumber();
            }
            if (p.getCustomer() != null) {
                this.customerId = p.getCustomer().getId();
                this.customerName = p.getCustomer().getName();
            }
            this.amount = p.getAmount();
            this.paymentMethod = p.getPaymentMethod();
            this.status = p.getStatus();
            this.transactionDate = p.getTransactionDate();
            this.notes = p.getNotes();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPaymentReference() { return paymentReference; }
    public void setPaymentReference(String paymentReference) { this.paymentReference = paymentReference; }

    public Long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Long invoiceId) { this.invoiceId = invoiceId; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
