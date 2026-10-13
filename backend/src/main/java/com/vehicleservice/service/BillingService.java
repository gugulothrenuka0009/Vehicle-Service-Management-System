package com.vehicleservice.service;

import com.vehicleservice.dto.InvoiceDto;
import com.vehicleservice.dto.PaymentDto;
import com.vehicleservice.dto.PaymentRequest;
import com.vehicleservice.entity.*;
import com.vehicleservice.exception.BadRequestException;
import com.vehicleservice.exception.ResourceNotFoundException;
import com.vehicleservice.repository.BookingRepository;
import com.vehicleservice.repository.InvoiceRepository;
import com.vehicleservice.repository.PaymentRepository;
import com.vehicleservice.repository.ServiceRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BillingService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final ServiceRecordRepository serviceRecordRepository;
    private final NotificationService notificationService;

    public BillingService(InvoiceRepository invoiceRepository,
                          PaymentRepository paymentRepository,
                          BookingRepository bookingRepository,
                          ServiceRecordRepository serviceRecordRepository,
                          NotificationService notificationService) {
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.serviceRecordRepository = serviceRecordRepository;
        this.notificationService = notificationService;
    }

    public List<InvoiceDto> getAllInvoices() {
        return invoiceRepository.findAll().stream().map(InvoiceDto::new).collect(Collectors.toList());
    }

    public List<InvoiceDto> getCustomerInvoices(Long customerId) {
        return invoiceRepository.findByCustomerIdOrderByCreatedAtDesc(customerId).stream()
                .map(InvoiceDto::new)
                .collect(Collectors.toList());
    }

    public InvoiceDto getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
        return new InvoiceDto(invoice);
    }

    public Optional<InvoiceDto> getInvoiceByBookingId(Long bookingId) {
        return invoiceRepository.findByBookingId(bookingId).map(InvoiceDto::new);
    }

    @Transactional
    public Invoice generateInvoice(Booking booking, BigDecimal partsCost, BigDecimal laborCost) {
        // Return existing invoice if already generated
        Optional<Invoice> existing = invoiceRepository.findByBookingId(booking.getId());
        if (existing.isPresent()) {
            return existing.get();
        }

        BigDecimal parts = partsCost != null ? partsCost : BigDecimal.ZERO;
        BigDecimal labor = laborCost != null ? laborCost : (booking.getService() != null ? booking.getService().getBasePrice() : BigDecimal.ZERO);
        BigDecimal subtotal = parts.add(labor);
        // 18% GST standard vehicle service tax rate
        BigDecimal tax = subtotal.multiply(new BigDecimal("0.18")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal discount = BigDecimal.ZERO;
        BigDecimal total = subtotal.add(tax).subtract(discount);

        String invoiceNumber = "INV-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM")) + "-" +
                UUID.randomUUID().toString().substring(0, 5).toUpperCase();

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(invoiceNumber);
        invoice.setBooking(booking);
        invoice.setCustomer(booking.getCustomer());
        invoice.setVehicle(booking.getVehicle());
        invoice.setPartsTotal(parts);
        invoice.setLaborTotal(labor);
        invoice.setTaxAmount(tax);
        invoice.setDiscountAmount(discount);
        invoice.setFinalAmount(total);
        invoice.setStatus(InvoiceStatus.UNPAID);
        invoice.setInvoiceDate(LocalDate.now());
        invoice.setDueDate(LocalDate.now().plusDays(3));

        Invoice saved = invoiceRepository.save(invoice);

        notificationService.createNotification(
                booking.getCustomer(),
                "Invoice Generated",
                "Invoice " + invoiceNumber + " for amount ₹" + total + " has been generated for your service on " +
                        booking.getVehicle().getRegNumber() + ". Please complete payment.",
                NotificationType.INVOICE
        );

        return saved;
    }

    @Transactional
    public PaymentDto processPayment(Long customerId, PaymentRequest request) {
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + request.getInvoiceId()));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new BadRequestException("Invoice " + invoice.getInvoiceNumber() + " is already paid");
        }

        if (customerId != null && !invoice.getCustomer().getId().equals(customerId)) {
            throw new BadRequestException("You can only pay for your own invoices");
        }

        // Validate amount
        if (request.getAmount() == null || request.getAmount().compareTo(invoice.getFinalAmount()) < 0) {
            throw new BadRequestException("Payment amount cannot be less than invoice total ₹" + invoice.getFinalAmount());
        }

        String paymentRef = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Payment payment = new Payment(
                paymentRef,
                invoice,
                invoice.getCustomer(),
                request.getAmount(),
                request.getPaymentMethod(),
                PaymentStatus.SUCCESS,
                request.getNotes() != null ? request.getNotes() : "Simulated payment via " + request.getPaymentMethod()
        );

        Payment savedPayment = paymentRepository.save(payment);

        // Update invoice status to PAID
        invoice.setStatus(InvoiceStatus.PAID);
        invoiceRepository.save(invoice);

        // Also update booking status to COMPLETED if not already
        Booking booking = invoice.getBooking();
        if (booking != null && booking.getStatus() != BookingStatus.COMPLETED) {
            booking.setStatus(BookingStatus.COMPLETED);
            bookingRepository.save(booking);
        }

        // Send payment confirmation notification
        notificationService.createNotification(
                invoice.getCustomer(),
                "Payment Received Successfully",
                "Payment of ₹" + request.getAmount() + " via " + request.getPaymentMethod() +
                        " for Invoice " + invoice.getInvoiceNumber() + " was successful. Ref: " + paymentRef + ".",
                NotificationType.PAYMENT
        );

        return new PaymentDto(savedPayment);
    }

    public List<PaymentDto> getAllPayments() {
        return paymentRepository.findAll().stream().map(PaymentDto::new).collect(Collectors.toList());
    }

    public List<PaymentDto> getCustomerPayments(Long customerId) {
        return paymentRepository.findByCustomerIdOrderByTransactionDateDesc(customerId).stream()
                .map(PaymentDto::new)
                .collect(Collectors.toList());
    }
}
