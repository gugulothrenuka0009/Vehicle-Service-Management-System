package com.vehicleservice.config;

import com.vehicleservice.entity.*;
import com.vehicleservice.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ServiceRepository serviceRepository;
    private final InventoryRepository inventoryRepository;
    private final BookingRepository bookingRepository;
    private final ServiceJobRepository serviceJobRepository;
    private final MechanicAssignmentRepository assignmentRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
                      VehicleRepository vehicleRepository,
                      ServiceRepository serviceRepository,
                      InventoryRepository inventoryRepository,
                      BookingRepository bookingRepository,
                      ServiceJobRepository serviceJobRepository,
                      MechanicAssignmentRepository assignmentRepository,
                      InvoiceRepository invoiceRepository,
                      PaymentRepository paymentRepository,
                      NotificationRepository notificationRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.serviceRepository = serviceRepository;
        this.inventoryRepository = inventoryRepository;
        this.bookingRepository = bookingRepository;
        this.serviceJobRepository = serviceJobRepository;
        this.assignmentRepository = assignmentRepository;
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.notificationRepository = notificationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return; // Data already seeded
        }

        System.out.println(">>> SEEDING INITIAL VEHICLE SERVICE DATA...");

        // 1. Users
        User admin = userRepository.save(new User(null, "System Administrator", "admin@vehicleservice.com",
                passwordEncoder.encode("admin123"), "+91 9876543210", Role.ADMIN, true));

        User advisor = userRepository.save(new User(null, "Rajesh Sharma", "advisor@vehicleservice.com",
                passwordEncoder.encode("advisor123"), "+91 9876543211", Role.SERVICE_ADVISOR, true));

        User mechanic1 = userRepository.save(new User(null, "Vikram Singh", "mechanic@vehicleservice.com",
                passwordEncoder.encode("mechanic123"), "+91 9876543212", Role.MECHANIC, true));

        User mechanic2 = userRepository.save(new User(null, "Suresh Patel", "mechanic2@vehicleservice.com",
                passwordEncoder.encode("mechanic123"), "+91 9876543213", Role.MECHANIC, true));

        User customer1 = userRepository.save(new User(null, "Amit Verma", "customer@vehicleservice.com",
                passwordEncoder.encode("customer123"), "+91 9876543214", Role.CUSTOMER, true));

        User customer2 = userRepository.save(new User(null, "Priya Nair", "priya@vehicleservice.com",
                passwordEncoder.encode("customer123"), "+91 9876543215", Role.CUSTOMER, true));

        // 2. Vehicles
        Vehicle v1 = vehicleRepository.save(new Vehicle(null, customer1, "MH-12-AB-1234", "Honda", "City ZX", 2021, "PETROL", 32500));
        Vehicle v2 = vehicleRepository.save(new Vehicle(null, customer1, "MH-12-CD-5678", "Hyundai", "Creta SX", 2022, "DIESEL", 21000));
        Vehicle v3 = vehicleRepository.save(new Vehicle(null, customer2, "DL-01-EF-9012", "Tata", "Nexon EV", 2023, "ELECTRIC", 14200));
        Vehicle v4 = vehicleRepository.save(new Vehicle(null, customer2, "KA-03-GH-3456", "Toyota", "Innova Crysta", 2020, "DIESEL", 58000));

        // 3. Services Catalog
        ServiceItem s1 = serviceRepository.save(new ServiceItem(null, "General Periodic Service", "SRV-GEN-01",
                ServiceCategory.PERIODIC_MAINTENANCE, "Synthetic engine oil replacement, oil filter change, fluid top-up, spark plug check, 40-point safety inspection.",
                new BigDecimal("2499.00"), 180, true));

        ServiceItem s2 = serviceRepository.save(new ServiceItem(null, "Major Engine Tune-up", "SRV-ENG-02",
                ServiceCategory.REPAIR, "Full computer scan diagnostics, throttle body cleaning, injector calibration, compression test.",
                new BigDecimal("3999.00"), 240, true));

        ServiceItem s3 = serviceRepository.save(new ServiceItem(null, "Brake System Overhaul", "SRV-BRK-03",
                ServiceCategory.REPAIR, "Front & rear brake pad inspection, rotor resurfacing, caliper lubrication, and brake fluid flushing.",
                new BigDecimal("1899.00"), 120, true));

        ServiceItem s4 = serviceRepository.save(new ServiceItem(null, "Air Conditioning & Climate Service", "SRV-AC-04",
                ServiceCategory.GENERAL_SERVICE, "Cabin filter replacement, AC gas refill, evaporator foam sanitization, cooling efficiency test.",
                new BigDecimal("2199.00"), 90, true));

        ServiceItem s5 = serviceRepository.save(new ServiceItem(null, "Wheel Balancing & 3D Alignment", "SRV-WHL-05",
                ServiceCategory.GENERAL_SERVICE, "Computerized 4-wheel alignment, high-speed dynamic balancing, tire rotation.",
                new BigDecimal("1199.00"), 60, true));

        ServiceItem s6 = serviceRepository.save(new ServiceItem(null, "Comprehensive Vehicle Inspection", "SRV-INS-06",
                ServiceCategory.INSPECTION, "Detailed 100-point inspection covering engine, body, suspension, electricals, and road test.",
                new BigDecimal("1499.00"), 90, true));

        // 4. Inventory / Spare Parts
        inventoryRepository.saveAll(List.of(
                new Inventory(null, "PART-OIL-5W30", "Synthetic Engine Oil 5W-30 (4L)", "Lubricants", new BigDecimal("1850.00"), 45, 10, "Rack A-1", true),
                new Inventory(null, "PART-FIL-OIL01", "OEM Engine Oil Filter", "Filters", new BigDecimal("350.00"), 60, 15, "Rack A-2", true),
                new Inventory(null, "PART-FIL-AIR02", "Engine Air Filter Element", "Filters", new BigDecimal("480.00"), 35, 10, "Rack A-3", true),
                new Inventory(null, "PART-FIL-CAB03", "Carbon Cabin AC Filter", "Filters", new BigDecimal("650.00"), 28, 8, "Rack A-4", true),
                new Inventory(null, "PART-BRK-PAD01", "Ceramic Front Brake Pad Set", "Brakes", new BigDecimal("2200.00"), 18, 5, "Rack B-1", true),
                new Inventory(null, "PART-BRK-PAD02", "Rear Brake Shoe Set", "Brakes", new BigDecimal("1600.00"), 14, 5, "Rack B-2", true),
                new Inventory(null, "PART-FLUID-DOT4", "Brake Fluid DOT 4 (500ml)", "Fluids", new BigDecimal("280.00"), 40, 12, "Rack B-3", true),
                new Inventory(null, "PART-SPK-PLUG", "Iridium Spark Plugs (Pack of 4)", "Ignition", new BigDecimal("1450.00"), 22, 6, "Rack C-1", true),
                new Inventory(null, "PART-WIP-BLD24", "Silicone Wiper Blades (Pair)", "Accessories", new BigDecimal("890.00"), 3, 10, "Rack D-1", true), // LOW STOCK
                new Inventory(null, "PART-COOL-5L", "Radiator Coolant Green (5L)", "Fluids", new BigDecimal("750.00"), 4, 8, "Rack B-4", true) // LOW STOCK
        ));

        // 5. Sample Bookings
        Booking b1 = new Booking();
        b1.setBookingNumber("BK-202610-001");
        b1.setCustomer(customer1);
        b1.setVehicle(v1);
        b1.setService(s1);
        b1.setAdvisor(advisor);
        b1.setAppointmentDate(LocalDate.now().plusDays(2));
        b1.setAppointmentTime("10:00 AM");
        b1.setNotes("Regular scheduled 30,000 km periodic service.");
        b1.setStatus(BookingStatus.CONFIRMED);
        b1.setEstimatedCost(s1.getBasePrice());
        bookingRepository.save(b1);

        Booking b2 = new Booking();
        b2.setBookingNumber("BK-202610-002");
        b2.setCustomer(customer1);
        b2.setVehicle(v2);
        b2.setService(s3);
        b2.setAdvisor(advisor);
        b2.setAppointmentDate(LocalDate.now());
        b2.setAppointmentTime("02:00 PM");
        b2.setNotes("Brake pedal feels soft during hard stopping.");
        b2.setStatus(BookingStatus.IN_PROGRESS);
        b2.setEstimatedCost(s3.getBasePrice());
        Booking savedB2 = bookingRepository.save(b2);

        Booking b3 = new Booking();
        b3.setBookingNumber("BK-202610-003");
        b3.setCustomer(customer2);
        b3.setVehicle(v3);
        b3.setService(s4);
        b3.setAppointmentDate(LocalDate.now().plusDays(3));
        b3.setAppointmentTime("11:30 AM");
        b3.setNotes("AC cooling feels weak in heavy traffic.");
        b3.setStatus(BookingStatus.PENDING);
        b3.setEstimatedCost(s4.getBasePrice());
        bookingRepository.save(b3);

        Booking b4 = new Booking();
        b4.setBookingNumber("BK-202610-004");
        b4.setCustomer(customer2);
        b4.setVehicle(v4);
        b4.setService(s1);
        b4.setAdvisor(advisor);
        b4.setAppointmentDate(LocalDate.now().minusDays(2));
        b4.setAppointmentTime("09:00 AM");
        b4.setNotes("Completed 60k km major service.");
        b4.setStatus(BookingStatus.COMPLETED);
        b4.setEstimatedCost(new BigDecimal("5450.00"));
        Booking savedB4 = bookingRepository.save(b4);

        // 6. Service Jobs
        ServiceJob job1 = new ServiceJob();
        job1.setJobNumber("JOB-202610-001");
        job1.setBooking(savedB2);
        job1.setMechanic(mechanic1);
        job1.setStatus(JobStatus.IN_PROGRESS);
        job1.setDiagnosisNotes("Front pads worn to 2.5mm. Brake fluid has moisture content.");
        job1.setWorkSummary("Replacing front brake pads and bleeding brake hydraulic line.");
        job1.setLaborCost(new BigDecimal("1200.00"));
        job1.setPartsCost(new BigDecimal("2480.00"));
        job1.setTotalJobCost(new BigDecimal("3680.00"));
        job1.setStartTime(LocalDateTime.now().minusHours(2));
        serviceJobRepository.save(job1);

        assignmentRepository.save(new MechanicAssignment(job1, mechanic1, advisor));

        ServiceJob job2 = new ServiceJob();
        job2.setJobNumber("JOB-202610-002");
        job2.setBooking(savedB4);
        job2.setMechanic(mechanic1);
        job2.setStatus(JobStatus.COMPLETED);
        job2.setDiagnosisNotes("Periodic inspection passed. Oil was dark, air filter choked.");
        job2.setWorkSummary("Full synthetic oil replaced, new oil filter installed, air filter replaced, spark plugs cleaned.");
        job2.setLaborCost(new BigDecimal("1500.00"));
        job2.setPartsCost(new BigDecimal("2680.00"));
        job2.setTotalJobCost(new BigDecimal("4180.00"));
        job2.setStartTime(LocalDateTime.now().minusDays(2).withHour(9));
        job2.setCompletionTime(LocalDateTime.now().minusDays(2).withHour(13));
        serviceJobRepository.save(job2);

        // 7. Invoices
        Invoice inv1 = new Invoice();
        inv1.setInvoiceNumber("INV-202610-001");
        inv1.setBooking(savedB4);
        inv1.setCustomer(customer2);
        inv1.setVehicle(v4);
        inv1.setPartsTotal(new BigDecimal("2680.00"));
        inv1.setLaborTotal(new BigDecimal("1500.00"));
        inv1.setTaxAmount(new BigDecimal("752.40"));
        inv1.setDiscountAmount(new BigDecimal("200.00"));
        inv1.setFinalAmount(new BigDecimal("4732.40"));
        inv1.setStatus(InvoiceStatus.PAID);
        inv1.setInvoiceDate(LocalDate.now().minusDays(2));
        inv1.setDueDate(LocalDate.now().minusDays(2));
        Invoice savedInv1 = invoiceRepository.save(inv1);

        Invoice inv2 = new Invoice();
        inv2.setInvoiceNumber("INV-202610-002");
        inv2.setBooking(savedB2);
        inv2.setCustomer(customer1);
        inv2.setVehicle(v2);
        inv2.setPartsTotal(new BigDecimal("2480.00"));
        inv2.setLaborTotal(new BigDecimal("1200.00"));
        inv2.setTaxAmount(new BigDecimal("662.40"));
        inv2.setDiscountAmount(BigDecimal.ZERO);
        inv2.setFinalAmount(new BigDecimal("4342.40"));
        inv2.setStatus(InvoiceStatus.UNPAID);
        inv2.setInvoiceDate(LocalDate.now());
        inv2.setDueDate(LocalDate.now().plusDays(3));
        invoiceRepository.save(inv2);

        // 8. Payments
        paymentRepository.save(new Payment(
                "PAY-TXN-984321",
                savedInv1,
                customer2,
                new BigDecimal("4732.40"),
                PaymentMethod.UPI,
                PaymentStatus.SUCCESS,
                "UPI payment reference ID 9843210984"
        ));

        // 9. Notifications
        notificationRepository.saveAll(List.of(
                new Notification(customer1, "Booking Confirmed", "Your booking BK-202610-001 for Honda City ZX is confirmed for Oct 14.", NotificationType.BOOKING),
                new Notification(customer1, "Work In Progress", "Mechanic Vikram Singh has commenced repairs on your Hyundai Creta.", NotificationType.STATUS_UPDATE),
                new Notification(customer2, "Payment Successful", "Payment of ₹4,732.40 for Invoice INV-202610-001 has been recorded.", NotificationType.PAYMENT),
                new Notification(mechanic1, "New Job Assigned", "You have been assigned job JOB-202610-001 for brake repair.", NotificationType.ASSIGNMENT),
                new Notification(admin, "Low Stock Warning", "Silicone Wiper Blades are low on stock (3 remaining).", NotificationType.SYSTEM)
        ));

        System.out.println(">>> DATA SEEDING COMPLETE! Default accounts initialized.");
    }
}
