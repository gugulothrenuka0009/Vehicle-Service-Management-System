-- ==========================================================
-- Sample Seed Data: Vehicle Service Management System
-- ==========================================================
USE vehicle_service_db;

-- 1. USERS (Passwords hashed with BCrypt for 'admin123', 'advisor123', 'mechanic123', 'customer123')
-- Default password: password123 -> $2a$10$wN1QZc7d6L7Qz6zVdZ5cQe7fLzQ0F2aN2m7y5Y6k7K8j9l0m1n2o3 (Or handled by Spring Boot DataSeeder)
INSERT INTO users (id, name, email, password, phone, role, is_active) VALUES
(1, 'System Administrator', 'admin@vehicleservice.com', '$2a$10$7Ez8yvjRkJdFm2g6UfB8sOU28YVf4W5lR2pG6XQ1ZqA5zVb5mO.uK', '+91 9876543210', 'ADMIN', TRUE),
(2, 'Rajesh Sharma (Advisor)', 'advisor@vehicleservice.com', '$2a$10$7Ez8yvjRkJdFm2g6UfB8sOU28YVf4W5lR2pG6XQ1ZqA5zVb5mO.uK', '+91 9876543211', 'SERVICE_ADVISOR', TRUE),
(3, 'Vikram Singh (Mechanic)', 'mechanic@vehicleservice.com', '$2a$10$7Ez8yvjRkJdFm2g6UfB8sOU28YVf4W5lR2pG6XQ1ZqA5zVb5mO.uK', '+91 9876543212', 'MECHANIC', TRUE),
(4, 'Suresh Patel (Mechanic)', 'mechanic2@vehicleservice.com', '$2a$10$7Ez8yvjRkJdFm2g6UfB8sOU28YVf4W5lR2pG6XQ1ZqA5zVb5mO.uK', '+91 9876543213', 'MECHANIC', TRUE),
(5, 'Amit Verma (Customer)', 'customer@vehicleservice.com', '$2a$10$7Ez8yvjRkJdFm2g6UfB8sOU28YVf4W5lR2pG6XQ1ZqA5zVb5mO.uK', '+91 9876543214', 'CUSTOMER', TRUE),
(6, 'Priya Nair (Customer)', 'priya@vehicleservice.com', '$2a$10$7Ez8yvjRkJdFm2g6UfB8sOU28YVf4W5lR2pG6XQ1ZqA5zVb5mO.uK', '+91 9876543215', 'CUSTOMER', TRUE);

-- 2. VEHICLES
INSERT INTO vehicles (id, user_id, reg_number, brand, model, year, fuel_type, mileage) VALUES
(1, 5, 'MH-12-AB-1234', 'Honda', 'City ZX', 2021, 'PETROL', 32500),
(2, 5, 'MH-12-CD-5678', 'Hyundai', 'Creta SX(O)', 2022, 'DIESEL', 21000),
(3, 6, 'DL-01-EF-9012', 'Tata', 'Nexon EV', 2023, 'ELECTRIC', 14200),
(4, 6, 'KA-03-GH-3456', 'Toyota', 'Innova Crysta', 2020, 'DIESEL', 58000);

-- 3. SERVICES
INSERT INTO services (id, name, code, category, description, base_price, estimated_duration_minutes, is_active) VALUES
(1, 'General Periodic Service', 'SRV-GEN-01', 'PERIODIC_MAINTENANCE', 'Comprehensive engine oil replacement, oil filter change, fluid top-up, spark plug check, 40-point vehicle safety inspection.', 2499.00, 180, TRUE),
(2, 'Major Engine Tune-up & Diagnostics', 'SRV-ENG-02', 'REPAIR', 'Complete computer scan diagnostics, throttle body cleaning, injector calibration, and compression test.', 3999.00, 240, TRUE),
(3, 'Brake System Overhaul', 'SRV-BRK-03', 'REPAIR', 'Front & rear brake pad inspection, rotor resurfacing, caliper lubrication, and brake fluid flushing.', 1899.00, 120, TRUE),
(4, 'Air Conditioning & Climate Service', 'SRV-AC-04', 'GENERAL_SERVICE', 'Cabin AC filter change, refrigerant pressure check & gas refill, evaporator foam cleaning, blower motor inspection.', 2199.00, 90, TRUE),
(5, 'Wheel Balancing & 3D Alignment', 'SRV-WHL-05', 'GENERAL_SERVICE', 'Computerized 4-wheel laser alignment, high-speed dynamic balancing, tire rotation, and tread depth check.', 1199.00, 60, TRUE),
(6, 'Comprehensive Pre-Purchase Inspection', 'SRV-INS-06', 'INSPECTION', 'Detailed 100-point inspection covering engine, chassis, transmission, bodywork, OBD diagnostics, and road test report.', 1499.00, 90, TRUE);

-- 4. INVENTORY / SPARE PARTS
INSERT INTO inventory (id, item_code, item_name, category, unit_price, quantity_in_stock, min_reorder_level, location, is_active) VALUES
(1, 'PART-OIL-5W30', 'Fully Synthetic Engine Oil 5W-30 (4L)', 'Lubricants', 1850.00, 45, 10, 'Rack A-1', TRUE),
(2, 'PART-FIL-OIL01', 'OEM High Flow Engine Oil Filter', 'Filters', 350.00, 60, 15, 'Rack A-2', TRUE),
(3, 'PART-FIL-AIR02', 'Engine Air Filter Element', 'Filters', 480.00, 35, 10, 'Rack A-3', TRUE),
(4, 'PART-FIL-CAB03', 'Activated Carbon Cabin AC Filter', 'Filters', 650.00, 28, 8, 'Rack A-4', TRUE),
(5, 'PART-BRK-PAD01', 'Ceramic Front Brake Pad Set', 'Brakes', 2200.00, 18, 5, 'Rack B-1', TRUE),
(6, 'PART-BRK-PAD02', 'Rear Brake Shoe Set', 'Brakes', 1600.00, 14, 5, 'Rack B-2', TRUE),
(7, 'PART-FLUID-DOT4', 'Brake Fluid DOT 4 (500ml)', 'Fluids', 280.00, 40, 12, 'Rack B-3', TRUE),
(8, 'PART-SPK-PLUG', 'Iridium Spark Plugs (Pack of 4)', 'Ignition', 1450.00, 22, 6, 'Rack C-1', TRUE),
(9, 'PART-WIP-BLD24', 'Frameless Silicone Wiper Blades (Pair)', 'Accessories', 890.00, 4, 10, 'Rack D-1', TRUE), -- LOW STOCK
(10, 'PART-COOL-5L', 'Long Life Radiator Coolant 5L', 'Fluids', 750.00, 3, 8, 'Rack B-4', TRUE); -- LOW STOCK

-- 5. BOOKINGS
INSERT INTO bookings (id, booking_number, customer_id, vehicle_id, service_id, advisor_id, appointment_date, appointment_time, notes, status, estimated_cost) VALUES
(1, 'BK-202610-001', 5, 1, 1, 2, '2026-10-14', '10:00 AM', 'Regular 30k km maintenance and slight brake squeak.', 'CONFIRMED', 4500.00),
(2, 'BK-202610-002', 5, 2, 3, 2, '2026-10-15', '02:00 PM', 'Brake pedal feels soft during hard stopping.', 'IN_PROGRESS', 3800.00),
(3, 'BK-202610-003', 6, 3, 4, NULL, '2026-10-16', '11:30 AM', 'AC cooling is insufficient in traffic.', 'PENDING', 2200.00),
(4, 'BK-202610-004', 6, 4, 1, 2, '2026-10-10', '09:00 AM', 'Completed 60k service, engine oil changed, all fluids topped up.', 'COMPLETED', 5450.00);

-- 6. SERVICE JOBS
INSERT INTO service_jobs (id, job_number, booking_id, mechanic_id, status, diagnosis_notes, work_summary, labor_cost, parts_cost, total_job_cost, start_time, completion_time) VALUES
(1, 'JOB-202610-001', 2, 3, 'IN_PROGRESS', 'Front pads worn down to 2mm. Disc has minor ridges but within spec.', 'Replacing front brake pads and flushing DOT 4 brake fluid.', 1200.00, 2480.00, 3680.00, '2026-10-12 11:00:00', NULL),
(2, 'JOB-202610-002', 4, 3, 'COMPLETED', 'Vehicle in good overall mechanical condition. Oil dark, air filter choked.', 'Oil drained and replaced with 5W-30 synthetic, new oil filter installed, air filter replaced, multipoint safety inspection passed.', 1500.00, 2680.00, 4180.00, '2026-10-10 09:30:00', '2026-10-10 13:45:00');

-- 7. MECHANIC ASSIGNMENTS
INSERT INTO mechanic_assignments (id, job_id, mechanic_id, assigned_by, assigned_at, status) VALUES
(1, 1, 3, 2, '2026-10-12 10:30:00', 'ACTIVE'),
(2, 2, 3, 2, '2026-10-10 09:15:00', 'COMPLETED');

-- 8. INVOICES
INSERT INTO invoices (id, invoice_number, booking_id, customer_id, vehicle_id, parts_total, labor_total, tax_amount, discount_amount, final_amount, status, invoice_date, due_date) VALUES
(1, 'INV-202610-001', 4, 6, 4, 2680.00, 1500.00, 752.40, 200.00, 4732.40, 'PAID', '2026-10-10', '2026-10-10'),
(2, 'INV-202610-002', 2, 5, 2, 2480.00, 1200.00, 662.40, 0.00, 4342.40, 'UNPAID', '2026-10-12', '2026-10-15');

-- 9. PAYMENTS
INSERT INTO payments (id, payment_reference, invoice_id, customer_id, amount, payment_method, status, transaction_date, notes) VALUES
(1, 'PAY-TXN-984321', 1, 6, 4732.40, 'UPI', 'SUCCESS', '2026-10-10 14:10:00', 'Payment completed via UPI reference 9843210984');

-- 10. NOTIFICATIONS
INSERT INTO notifications (id, user_id, title, message, type, is_read, created_at) VALUES
(1, 5, 'Booking Confirmed', 'Your booking BK-202610-001 for Honda City ZX is confirmed for Oct 14 at 10:00 AM.', 'BOOKING', FALSE, NOW()),
(2, 5, 'Service In Progress', 'Work has commenced on your vehicle Hyundai Creta SX(O) by Senior Mechanic Vikram Singh.', 'STATUS_UPDATE', FALSE, NOW()),
(3, 6, 'Invoice Generated', 'Invoice INV-202610-001 for Rs 4,732.40 has been generated and paid successfully.', 'INVOICE', TRUE, '2026-10-10 14:15:00'),
(4, 2, 'New Booking Request', 'New booking request BK-202610-003 received for Tata Nexon EV.', 'BOOKING', FALSE, NOW()),
(5, 3, 'New Job Assigned', 'You have been assigned job JOB-202610-001 for Hyundai Creta brake repair.', 'ASSIGNMENT', FALSE, NOW()),
(6, 1, 'Low Stock Alert', 'Part PART-WIP-BLD24 is at 4 units (threshold: 10 units).', 'SYSTEM', FALSE, NOW());
