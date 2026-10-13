import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import StatCard from '../components/StatCard';
import StatusBadge from '../components/StatusBadge';
import Modal from '../components/Modal';
import api from '../services/api';
import {
  Calendar,
  Car,
  Clock,
  Plus,
  CreditCard,
  FileText,
  AlertCircle,
  CheckCircle2,
  Printer,
  ChevronRight,
  ShieldCheck,
  X,
} from 'lucide-react';
import confetti from 'canvas-confetti';

const CustomerDashboard = ({ activeTab = 'dashboard' }) => {
  const { user } = useAuth();
  const [tab, setTab] = useState(activeTab);

  const [bookings, setBookings] = useState([]);
  const [vehicles, setVehicles] = useState([]);
  const [services, setServices] = useState([]);
  const [invoices, setInvoices] = useState([]);
  const [loading, setLoading] = useState(true);

  // Modals
  const [showBookModal, setShowBookModal] = useState(false);
  const [showVehicleModal, setShowVehicleModal] = useState(false);
  const [showPayModal, setShowPayModal] = useState(false);
  const [selectedInvoice, setSelectedInvoice] = useState(null);
  const [showInvoicePrint, setShowInvoicePrint] = useState(false);

  // Booking Form State
  const [selectedVehicleId, setSelectedVehicleId] = useState('');
  const [selectedServiceId, setSelectedServiceId] = useState('');
  const [appointmentDate, setAppointmentDate] = useState('');
  const [appointmentTime, setAppointmentTime] = useState('10:00 AM');
  const [bookingNotes, setBookingNotes] = useState('');

  // Vehicle Form State
  const [vReg, setVReg] = useState('');
  const [vBrand, setVBrand] = useState('');
  const [vModel, setVModel] = useState('');
  const [vYear, setVYear] = useState(new Date().getFullYear());
  const [vFuel, setVFuel] = useState('PETROL');
  const [vMileage, setVMileage] = useState(15000);

  // Payment Form State
  const [payMethod, setPayMethod] = useState('UPI');
  const [cardNumber, setCardNumber] = useState('4532 •••• •••• 8892');
  const [upiId, setUpiId] = useState('customer@okhdfcbank');
  const [paySuccess, setPaySuccess] = useState(false);

  useEffect(() => {
    setTab(activeTab);
  }, [activeTab]);

  useEffect(() => {
    fetchData();
  }, [user]);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [bRes, vRes, sRes, iRes] = await Promise.all([
        api.get('/bookings'),
        api.get('/vehicles'),
        api.get('/services'),
        api.get('/billing/invoices'),
      ]);
      setBookings(bRes.data?.data || []);
      setVehicles(vRes.data?.data || []);
      setServices(sRes.data?.data || []);
      setInvoices(iRes.data?.data || []);
    } catch {
      // Mock Fallback
      setVehicles([
        { id: 1, regNumber: 'MH-12-AB-1234', brand: 'Honda', model: 'City ZX', year: 2021, fuelType: 'PETROL', mileage: 32500 },
        { id: 2, regNumber: 'MH-12-CD-5678', brand: 'Hyundai', model: 'Creta SX', year: 2022, fuelType: 'DIESEL', mileage: 21000 },
      ]);
      setServices([
        { id: 1, name: 'General Periodic Service', code: 'SRV-GEN-01', basePrice: 2499.00, estimatedDurationMinutes: 180, category: 'PERIODIC_MAINTENANCE' },
        { id: 2, name: 'Major Engine Diagnostics', code: 'SRV-ENG-02', basePrice: 3999.00, estimatedDurationMinutes: 240, category: 'REPAIR' },
        { id: 3, name: 'Brake System Overhaul', code: 'SRV-BRK-03', basePrice: 1899.00, estimatedDurationMinutes: 120, category: 'REPAIR' },
      ]);
      setBookings([
        { id: 1, bookingNumber: 'BK-202610-001', regNumber: 'MH-12-AB-1234', vehicleDetails: 'Honda City ZX (2021)', serviceName: 'General Periodic Service', appointmentDate: '2026-10-14', appointmentTime: '10:00 AM', status: 'CONFIRMED', estimatedCost: 2499.00 },
        { id: 2, bookingNumber: 'BK-202610-002', regNumber: 'MH-12-CD-5678', vehicleDetails: 'Hyundai Creta SX (2022)', serviceName: 'Brake System Overhaul', appointmentDate: '2026-10-12', appointmentTime: '02:00 PM', status: 'IN_PROGRESS', estimatedCost: 3800.00 },
      ]);
      setInvoices([
        { id: 2, invoiceNumber: 'INV-202610-002', bookingNumber: 'BK-202610-002', regNumber: 'MH-12-CD-5678', vehicleDetails: 'Hyundai Creta SX', serviceName: 'Brake System Overhaul', partsTotal: 2480.00, laborTotal: 1200.00, taxAmount: 662.40, discountAmount: 0.00, finalAmount: 4342.40, status: 'UNPAID', invoiceDate: '2026-10-12', dueDate: '2026-10-15' },
      ]);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateBooking = async (e) => {
    e.preventDefault();
    if (!selectedVehicleId || !selectedServiceId || !appointmentDate) {
      alert('Please fill in vehicle, service, and date.');
      return;
    }

    try {
      const res = await api.post('/bookings', {
        vehicleId: Number(selectedVehicleId),
        serviceId: Number(selectedServiceId),
        appointmentDate,
        appointmentTime,
        notes: bookingNotes,
      });
      if (res.data?.success) {
        setBookings([res.data.data, ...bookings]);
        setShowBookModal(false);
        alert('Booking successfully placed!');
      }
    } catch {
      // Fallback local booking
      const veh = vehicles.find((v) => v.id === Number(selectedVehicleId));
      const srv = services.find((s) => s.id === Number(selectedServiceId));
      const newB = {
        id: Date.now(),
        bookingNumber: `BK-${new Date().toISOString().slice(0, 10).replace(/-/g, '')}-${Math.floor(100 + Math.random() * 900)}`,
        regNumber: veh?.regNumber || 'MH-12-AB-1234',
        vehicleDetails: `${veh?.brand} ${veh?.model}`,
        serviceName: srv?.name,
        appointmentDate,
        appointmentTime,
        status: 'PENDING',
        estimatedCost: srv?.basePrice || 2499.00,
      };
      setBookings([newB, ...bookings]);
      setShowBookModal(false);
    }
  };

  const handleAddVehicle = async (e) => {
    e.preventDefault();
    if (!vReg || !vBrand || !vModel) {
      alert('Please fill in registration number, brand, and model.');
      return;
    }

    try {
      const res = await api.post('/vehicles', {
        regNumber: vReg.toUpperCase(),
        brand: vBrand,
        model: vModel,
        year: Number(vYear),
        fuelType: vFuel,
        mileage: Number(vMileage),
      });
      if (res.data?.success) {
        setVehicles([...vehicles, res.data.data]);
        setShowVehicleModal(false);
      }
    } catch {
      // Local fallback
      const newV = {
        id: Date.now(),
        regNumber: vReg.toUpperCase(),
        brand: vBrand,
        model: vModel,
        year: Number(vYear),
        fuelType: vFuel,
        mileage: Number(vMileage),
      };
      setVehicles([...vehicles, newV]);
      setShowVehicleModal(false);
    }
  };

  const handleCancelBooking = async (id) => {
    if (!window.confirm('Are you sure you want to cancel this booking?')) return;
    try {
      await api.patch(`/bookings/${id}/cancel`);
    } catch {
      // fallback
    }
    setBookings((prev) =>
      prev.map((b) => (b.id === id ? { ...b, status: 'CANCELLED' } : b))
    );
  };

  const handleSimulatedPayment = async () => {
    try {
      await api.post('/billing/pay', {
        invoiceId: selectedInvoice.id,
        amount: selectedInvoice.finalAmount,
        paymentMethod: payMethod,
        notes: `Simulated payment via ${payMethod}`,
      });
    } catch {
      // fallback
    }

    setPaySuccess(true);
    confetti({ particleCount: 100, spread: 70, origin: { y: 0.6 } });

    setTimeout(() => {
      setInvoices((prev) =>
        prev.map((inv) =>
          inv.id === selectedInvoice.id ? { ...inv, status: 'PAID' } : inv
        )
      );
      setBookings((prev) =>
        prev.map((b) =>
          b.id === selectedInvoice.bookingId ? { ...b, status: 'COMPLETED' } : b
        )
      );
      setShowPayModal(false);
      setPaySuccess(false);
    }, 1500);
  };

  const totalSpent = invoices
    .filter((i) => i.status === 'PAID')
    .reduce((acc, i) => acc + (Number(i.finalAmount) || 0), 0);

  return (
    <div className="page-container">
      {/* Top Banner & Quick Action */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
        <div>
          <h1 style={{ fontSize: '1.75rem', fontWeight: 800 }}>Welcome, {user?.name || 'Customer'} 👋</h1>
          <p style={{ color: 'var(--text-muted)' }}>Track vehicle appointments, maintenance history, and invoices</p>
        </div>
        <div style={{ display: 'flex', gap: '0.75rem' }}>
          <button onClick={() => setShowVehicleModal(true)} className="btn btn-outline">
            <Plus size={16} /> Add Vehicle
          </button>
          <button onClick={() => setShowBookModal(true)} className="btn btn-primary">
            <Calendar size={16} /> Book Service Appointment
          </button>
        </div>
      </div>

      {/* Metrics Grid */}
      <div className="stats-grid">
        <StatCard
          title="My Vehicles"
          value={vehicles.length}
          icon={Car}
          color="#0284c7"
          bgColor="#e0f2fe"
          subtitle="Registered in garage"
        />
        <StatCard
          title="Active Bookings"
          value={bookings.filter((b) => b.status === 'PENDING' || b.status === 'CONFIRMED' || b.status === 'IN_PROGRESS').length}
          icon={Clock}
          color="#ea580c"
          bgColor="#fff7ed"
          subtitle="In pipeline / workshop"
        />
        <StatCard
          title="Pending Payments"
          value={invoices.filter((i) => i.status === 'UNPAID').length}
          icon={AlertCircle}
          color="#ef4444"
          bgColor="#fef2f2"
          subtitle="Invoices awaiting payment"
        />
        <StatCard
          title="Total Maintenance Spent"
          value={`₹${totalSpent.toLocaleString('en-IN')}`}
          icon={CreditCard}
          color="#10b981"
          bgColor="#ecfdf5"
          subtitle="Lifetime service costs"
        />
      </div>

      {/* TABS */}
      <div className="tabs">
        <button
          className={`tab-btn ${tab === 'dashboard' ? 'active' : ''}`}
          onClick={() => setTab('dashboard')}
        >
          <Clock size={16} /> Active Appointments & History
        </button>
        <button
          className={`tab-btn ${tab === 'vehicles' ? 'active' : ''}`}
          onClick={() => setTab('vehicles')}
        >
          <Car size={16} /> My Vehicles ({vehicles.length})
        </button>
        <button
          className={`tab-btn ${tab === 'invoices' ? 'active' : ''}`}
          onClick={() => setTab('invoices')}
        >
          <FileText size={16} /> Invoices & Billing ({invoices.length})
        </button>
      </div>

      {/* TAB CONTENT: BOOKINGS */}
      {tab === 'dashboard' && (
        <div className="table-card">
          <div className="table-toolbar">
            <h3 style={{ fontSize: '1.05rem', fontWeight: 700 }}>Service Appointments</h3>
            <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Total: {bookings.length}</span>
          </div>

          <div style={{ overflowX: 'auto' }}>
            <table className="data-table">
              <thead>
                <tr>
                  <th>Booking No</th>
                  <th>Vehicle</th>
                  <th>Service Package</th>
                  <th>Date & Time</th>
                  <th>Estimated Cost</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {bookings.length === 0 ? (
                  <tr>
                    <td colSpan={7} style={{ textAlign: 'center', padding: '2rem', color: 'var(--text-muted)' }}>
                      No service appointments yet. Click "Book Service Appointment" above to schedule!
                    </td>
                  </tr>
                ) : (
                  bookings.map((b) => (
                    <tr key={b.id}>
                      <td style={{ fontWeight: 700, color: 'var(--primary)' }}>{b.bookingNumber}</td>
                      <td>
                        <div style={{ fontWeight: 600 }}>{b.vehicleDetails || b.regNumber}</div>
                        <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{b.regNumber}</div>
                      </td>
                      <td>{b.serviceName}</td>
                      <td>
                        <div style={{ fontWeight: 500 }}>{b.appointmentDate}</div>
                        <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{b.appointmentTime}</div>
                      </td>
                      <td style={{ fontWeight: 700 }}>₹{Number(b.estimatedCost || 0).toLocaleString('en-IN')}</td>
                      <td>
                        <StatusBadge status={b.status} />
                      </td>
                      <td>
                        {(b.status === 'PENDING' || b.status === 'CONFIRMED') && (
                          <button
                            onClick={() => handleCancelBooking(b.id)}
                            className="btn btn-outline btn-sm"
                            style={{ color: 'var(--danger)', borderColor: '#fca5a5' }}
                          >
                            Cancel
                          </button>
                        )}
                        {b.status === 'COMPLETED' && (
                          <span style={{ fontSize: '0.8rem', color: 'var(--success)', fontWeight: 600, display: 'flex', alignItems: 'center', gap: '4px' }}>
                            <CheckCircle2 size={14} /> Serviced
                          </span>
                        )}
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* TAB CONTENT: VEHICLES */}
      {tab === 'vehicles' && (
        <div>
          <div className="section-header">
            <h2>Registered Garage Vehicles</h2>
            <button onClick={() => setShowVehicleModal(true)} className="btn btn-primary btn-sm">
              <Plus size={16} /> Register Vehicle
            </button>
          </div>

          <div className="vehicle-grid">
            {vehicles.map((v) => (
              <div key={v.id} className="vehicle-card">
                <div>
                  <div className="vehicle-card-top">
                    <span className="vehicle-reg">{v.regNumber}</span>
                    <span style={{ fontSize: '0.75rem', fontWeight: 700, color: 'var(--text-muted)' }}>{v.year}</span>
                  </div>
                  <div className="vehicle-name">{v.brand} {v.model}</div>
                  <div className="vehicle-meta">
                    <span>⛽ {v.fuelType}</span>
                    <span>🛣️ {Number(v.mileage || 0).toLocaleString('en-IN')} km</span>
                  </div>
                </div>

                <div style={{ marginTop: '1.25rem', paddingTop: '1rem', borderTop: '1px solid var(--border)', display: 'flex', justifyContent: 'flex-end' }}>
                  <button
                    onClick={() => {
                      setSelectedVehicleId(v.id);
                      setShowBookModal(true);
                    }}
                    className="btn btn-outline btn-sm"
                    style={{ width: '100%' }}
                  >
                    Schedule Service
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* TAB CONTENT: INVOICES & PAYMENTS */}
      {tab === 'invoices' && (
        <div className="table-card">
          <div className="table-toolbar">
            <h3 style={{ fontSize: '1.05rem', fontWeight: 700 }}>Invoices & Service Bills</h3>
          </div>

          <div style={{ overflowX: 'auto' }}>
            <table className="data-table">
              <thead>
                <tr>
                  <th>Invoice No</th>
                  <th>Vehicle</th>
                  <th>Parts</th>
                  <th>Labor</th>
                  <th>Tax (GST 18%)</th>
                  <th>Final Amount</th>
                  <th>Status</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {invoices.length === 0 ? (
                  <tr>
                    <td colSpan={8} style={{ textAlign: 'center', padding: '2rem', color: 'var(--text-muted)' }}>
                      No invoices issued yet. Invoices are generated automatically once mechanic completes workshop repairs!
                    </td>
                  </tr>
                ) : (
                  invoices.map((inv) => (
                    <tr key={inv.id}>
                      <td style={{ fontWeight: 700, color: 'var(--primary)' }}>{inv.invoiceNumber}</td>
                      <td>{inv.vehicleDetails || inv.regNumber}</td>
                      <td>₹{Number(inv.partsTotal || 0).toLocaleString('en-IN')}</td>
                      <td>₹{Number(inv.laborTotal || 0).toLocaleString('en-IN')}</td>
                      <td>₹{Number(inv.taxAmount || 0).toLocaleString('en-IN')}</td>
                      <td style={{ fontWeight: 800, color: 'var(--text-main)', fontSize: '0.95rem' }}>
                        ₹{Number(inv.finalAmount || 0).toLocaleString('en-IN')}
                      </td>
                      <td>
                        <StatusBadge status={inv.status} />
                      </td>
                      <td>
                        <div style={{ display: 'flex', gap: '6px' }}>
                          <button
                            onClick={() => {
                              setSelectedInvoice(inv);
                              setShowInvoicePrint(true);
                            }}
                            className="btn btn-outline btn-sm"
                            title="Print / View Invoice"
                          >
                            <Printer size={14} />
                          </button>
                          {inv.status === 'UNPAID' && (
                            <button
                              onClick={() => {
                                setSelectedInvoice(inv);
                                setShowPayModal(true);
                              }}
                              className="btn btn-primary btn-sm"
                            >
                              <CreditCard size={14} /> Pay Now
                            </button>
                          )}
                        </div>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* MODAL: BOOK APPOINTMENT */}
      <Modal
        isOpen={showBookModal}
        onClose={() => setShowBookModal(false)}
        title="Book Service Appointment"
      >
        <form onSubmit={handleCreateBooking}>
          <div className="form-group">
            <label>Select Vehicle *</label>
            <select
              className="form-control"
              value={selectedVehicleId}
              onChange={(e) => setSelectedVehicleId(e.target.value)}
              required
            >
              <option value="">-- Choose Your Registered Vehicle --</option>
              {vehicles.map((v) => (
                <option key={v.id} value={v.id}>
                  {v.regNumber} - {v.brand} {v.model} ({v.year})
                </option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label>Select Service Package *</label>
            <select
              className="form-control"
              value={selectedServiceId}
              onChange={(e) => setSelectedServiceId(e.target.value)}
              required
            >
              <option value="">-- Choose Workshop Service --</option>
              {services.map((s) => (
                <option key={s.id} value={s.id}>
                  {s.name} - ₹{Number(s.basePrice).toLocaleString('en-IN')} (~{s.estimatedDurationMinutes} mins)
                </option>
              ))}
            </select>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>Appointment Date *</label>
              <input
                type="date"
                className="form-control"
                min={new Date().toISOString().split('T')[0]}
                value={appointmentDate}
                onChange={(e) => setAppointmentDate(e.target.value)}
                required
              />
            </div>
            <div className="form-group">
              <label>Preferred Time Slot *</label>
              <select
                className="form-control"
                value={appointmentTime}
                onChange={(e) => setAppointmentTime(e.target.value)}
                required
              >
                <option value="09:00 AM">09:00 AM - Morning Slot</option>
                <option value="10:30 AM">10:30 AM - Morning Slot</option>
                <option value="01:00 PM">01:00 PM - Afternoon Slot</option>
                <option value="02:30 PM">02:30 PM - Afternoon Slot</option>
                <option value="04:00 PM">04:00 PM - Evening Slot</option>
              </select>
            </div>
          </div>

          <div className="form-group">
            <label>Notes / Vehicle Problem Symptoms</label>
            <textarea
              className="form-control"
              rows={3}
              placeholder="e.g. Unusual rattling noise from front suspension, brake pedal soft, AC cooling weak..."
              value={bookingNotes}
              onChange={(e) => setBookingNotes(e.target.value)}
            />
          </div>

          <div className="modal-footer" style={{ padding: '0.75rem 0 0', borderTop: 'none' }}>
            <button type="button" onClick={() => setShowBookModal(false)} className="btn btn-outline">
              Cancel
            </button>
            <button type="submit" className="btn btn-primary">
              Confirm & Book Appointment
            </button>
          </div>
        </form>
      </Modal>

      {/* MODAL: ADD VEHICLE */}
      <Modal
        isOpen={showVehicleModal}
        onClose={() => setShowVehicleModal(false)}
        title="Register New Vehicle"
      >
        <form onSubmit={handleAddVehicle}>
          <div className="form-group">
            <label>Registration Number (License Plate) *</label>
            <input
              type="text"
              className="form-control"
              placeholder="e.g. MH-12-AB-1234"
              value={vReg}
              onChange={(e) => setVReg(e.target.value)}
              required
            />
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>Vehicle Brand (Make) *</label>
              <input
                type="text"
                className="form-control"
                placeholder="e.g. Honda, Hyundai, Tata"
                value={vBrand}
                onChange={(e) => setVBrand(e.target.value)}
                required
              />
            </div>
            <div className="form-group">
              <label>Model *</label>
              <input
                type="text"
                className="form-control"
                placeholder="e.g. City, Creta, Nexon"
                value={vModel}
                onChange={(e) => setVModel(e.target.value)}
                required
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>Year</label>
              <input
                type="number"
                className="form-control"
                value={vYear}
                onChange={(e) => setVYear(e.target.value)}
              />
            </div>
            <div className="form-group">
              <label>Fuel Type</label>
              <select
                className="form-control"
                value={vFuel}
                onChange={(e) => setVFuel(e.target.value)}
              >
                <option value="PETROL">Petrol</option>
                <option value="DIESEL">Diesel</option>
                <option value="CNG">CNG</option>
                <option value="ELECTRIC">Electric (EV)</option>
                <option value="HYBRID">Hybrid</option>
              </select>
            </div>
          </div>

          <div className="form-group">
            <label>Current Odometer / Mileage (km)</label>
            <input
              type="number"
              className="form-control"
              placeholder="e.g. 25000"
              value={vMileage}
              onChange={(e) => setVMileage(e.target.value)}
            />
          </div>

          <div className="modal-footer" style={{ padding: '0.75rem 0 0', borderTop: 'none' }}>
            <button type="button" onClick={() => setShowVehicleModal(false)} className="btn btn-outline">
              Cancel
            </button>
            <button type="submit" className="btn btn-primary">
              Save Vehicle to Garage
            </button>
          </div>
        </form>
      </Modal>

      {/* MODAL: SIMULATED PAYMENT */}
      <Modal
        isOpen={showPayModal}
        onClose={() => setShowPayModal(false)}
        title="Simulated Payment Gateway"
      >
        {selectedInvoice && (
          <div>
            <div style={{
              background: 'var(--bg-surface-alt)',
              padding: '1rem',
              borderRadius: '8px',
              marginBottom: '1.25rem',
              border: '1px solid var(--border)'
            }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '4px' }}>
                <span style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>Invoice Number:</span>
                <span style={{ fontWeight: 700 }}>{selectedInvoice.invoiceNumber}</span>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '4px' }}>
                <span style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>Vehicle:</span>
                <span style={{ fontWeight: 600 }}>{selectedInvoice.vehicleDetails || selectedInvoice.regNumber}</span>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '0.5rem', paddingTop: '0.5rem', borderTop: '1px solid var(--border)' }}>
                <span style={{ fontWeight: 700 }}>Total Payable Amount:</span>
                <span style={{ fontWeight: 800, fontSize: '1.15rem', color: 'var(--primary)' }}>
                  ₹{Number(selectedInvoice.finalAmount).toLocaleString('en-IN')}
                </span>
              </div>
            </div>

            <div style={{ marginBottom: '1.25rem' }}>
              <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 700, marginBottom: '0.5rem' }}>
                Choose Payment Method (Simulated):
              </label>
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '0.5rem' }}>
                {['UPI', 'CARD', 'CASH'].map((m) => (
                  <button
                    key={m}
                    type="button"
                    onClick={() => setPayMethod(m)}
                    style={{
                      padding: '0.75rem',
                      borderRadius: '8px',
                      border: `2px solid ${payMethod === m ? 'var(--primary)' : 'var(--border)'}`,
                      background: payMethod === m ? 'var(--primary-light)' : 'white',
                      fontWeight: 700,
                      color: payMethod === m ? 'var(--primary)' : 'var(--text-main)',
                      cursor: 'pointer',
                    }}
                  >
                    {m === 'CARD' ? '💳 Credit/Debit' : m === 'UPI' ? '📱 UPI / QR' : '💵 Cash at Counter'}
                  </button>
                ))}
              </div>
            </div>

            {payMethod === 'CARD' && (
              <div className="form-group">
                <label>Simulated Card Number</label>
                <input
                  type="text"
                  className="form-control"
                  value={cardNumber}
                  onChange={(e) => setCardNumber(e.target.value)}
                />
                <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>* Test mode: Simulated transaction</span>
              </div>
            )}

            {payMethod === 'UPI' && (
              <div className="form-group">
                <label>Virtual Payment Address (VPA / UPI ID)</label>
                <input
                  type="text"
                  className="form-control"
                  value={upiId}
                  onChange={(e) => setUpiId(e.target.value)}
                />
                <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>* Test mode: Simulated instant settlement</span>
              </div>
            )}

            {payMethod === 'CASH' && (
              <div style={{ padding: '0.75rem', background: '#fef3c7', borderRadius: '6px', fontSize: '0.85rem', color: '#92400e', marginBottom: '1rem' }}>
                Cash payment will mark bill as paid at the workshop cashier reception counter.
              </div>
            )}

            <div className="modal-footer" style={{ padding: '1rem 0 0', borderTop: 'none' }}>
              <button
                type="button"
                onClick={() => setShowPayModal(false)}
                className="btn btn-outline"
                disabled={paySuccess}
              >
                Cancel
              </button>
              <button
                type="button"
                onClick={handleSimulatedPayment}
                className="btn btn-success"
                disabled={paySuccess}
                style={{ minWidth: '160px' }}
              >
                {paySuccess ? '✅ Payment Successful!' : `Pay ₹${Number(selectedInvoice.finalAmount).toLocaleString('en-IN')}`}
              </button>
            </div>
          </div>
        )}
      </Modal>

      {/* MODAL: PRINTABLE INVOICE VIEW */}
      <Modal
        isOpen={showInvoicePrint}
        onClose={() => setShowInvoicePrint(false)}
        title="Official Workshop Service Invoice"
        maxWidth="750px"
      >
        {selectedInvoice && (
          <div>
            <div className="invoice-container">
              <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '2px solid var(--primary)', paddingBottom: '1rem', marginBottom: '1.5rem' }}>
                <div>
                  <h2 style={{ color: 'var(--primary)', fontWeight: 800 }}>AUTOCARE PRO WORKSHOP</h2>
                  <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Authorized Multi-Brand Auto Care Center</p>
                  <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>GSTIN: 27AABCV1234F1Z5 | Support: +91 98765 43210</p>
                </div>
                <div style={{ textAlign: 'right' }}>
                  <h3 style={{ fontSize: '1.25rem', fontWeight: 800 }}>TAX INVOICE</h3>
                  <div style={{ fontWeight: 700, color: 'var(--text-main)', marginTop: '4px' }}>{selectedInvoice.invoiceNumber}</div>
                  <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Date: {selectedInvoice.invoiceDate}</div>
                  <div style={{ marginTop: '4px' }}><StatusBadge status={selectedInvoice.status} /></div>
                </div>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '2rem', marginBottom: '1.5rem' }}>
                <div>
                  <h4 style={{ fontSize: '0.85rem', textTransform: 'uppercase', color: 'var(--text-muted)', fontWeight: 700 }}>BILLED TO:</h4>
                  <div style={{ fontWeight: 700, marginTop: '4px' }}>{selectedInvoice.customerName || user?.name}</div>
                  <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>{selectedInvoice.customerEmail || user?.email}</div>
                  <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>{selectedInvoice.customerPhone || user?.phone}</div>
                </div>
                <div>
                  <h4 style={{ fontSize: '0.85rem', textTransform: 'uppercase', color: 'var(--text-muted)', fontWeight: 700 }}>VEHICLE DETAILS:</h4>
                  <div style={{ fontWeight: 700, marginTop: '4px', color: 'var(--primary)' }}>{selectedInvoice.regNumber}</div>
                  <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>{selectedInvoice.vehicleDetails}</div>
                </div>
              </div>

              <table style={{ width: '100%', borderCollapse: 'collapse', marginBottom: '1.5rem' }}>
                <thead>
                  <tr style={{ background: 'var(--bg-surface-alt)', borderBottom: '1px solid var(--border)' }}>
                    <th style={{ padding: '8px', textAlign: 'left', fontSize: '0.85rem' }}>Description</th>
                    <th style={{ padding: '8px', textAlign: 'right', fontSize: '0.85rem' }}>Amount (INR)</th>
                  </tr>
                </thead>
                <tbody>
                  <tr style={{ borderBottom: '1px solid var(--border)' }}>
                    <td style={{ padding: '8px', fontSize: '0.9rem' }}>Labor & Technical Charges ({selectedInvoice.serviceName || 'Service Overhaul'})</td>
                    <td style={{ padding: '8px', textAlign: 'right', fontSize: '0.9rem' }}>₹{Number(selectedInvoice.laborTotal).toFixed(2)}</td>
                  </tr>
                  <tr style={{ borderBottom: '1px solid var(--border)' }}>
                    <td style={{ padding: '8px', fontSize: '0.9rem' }}>Spare Parts & Consumables Total</td>
                    <td style={{ padding: '8px', textAlign: 'right', fontSize: '0.9rem' }}>₹{Number(selectedInvoice.partsTotal).toFixed(2)}</td>
                  </tr>
                  {Number(selectedInvoice.discountAmount) > 0 && (
                    <tr style={{ borderBottom: '1px solid var(--border)' }}>
                      <td style={{ padding: '8px', fontSize: '0.9rem', color: 'var(--success)' }}>Promotional Discount</td>
                      <td style={{ padding: '8px', textAlign: 'right', fontSize: '0.9rem', color: 'var(--success)' }}>-₹{Number(selectedInvoice.discountAmount).toFixed(2)}</td>
                    </tr>
                  )}
                  <tr style={{ borderBottom: '1px solid var(--border)' }}>
                    <td style={{ padding: '8px', fontSize: '0.9rem', color: 'var(--text-muted)' }}>Goods & Services Tax (GST 18%)</td>
                    <td style={{ padding: '8px', textAlign: 'right', fontSize: '0.9rem', color: 'var(--text-muted)' }}>₹{Number(selectedInvoice.taxAmount).toFixed(2)}</td>
                  </tr>
                  <tr>
                    <td style={{ padding: '12px 8px', fontWeight: 800, fontSize: '1.05rem' }}>GRAND TOTAL</td>
                    <td style={{ padding: '12px 8px', textAlign: 'right', fontWeight: 800, fontSize: '1.15rem', color: 'var(--primary)' }}>
                      ₹{Number(selectedInvoice.finalAmount).toFixed(2)}
                    </td>
                  </tr>
                </tbody>
              </table>

              <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', textAlign: 'center', borderTop: '1px solid var(--border)', paddingTop: '1rem' }}>
                Thank you for servicing with AutoCare Pro. All genuine parts carry 6-month warranty.
              </div>
            </div>

            <div className="modal-footer" style={{ borderTop: 'none', padding: '1rem 0 0' }}>
              <button onClick={() => window.print()} className="btn btn-outline">
                <Printer size={16} /> Print / Export PDF
              </button>
              <button onClick={() => setShowInvoicePrint(false)} className="btn btn-primary">
                Done
              </button>
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
};

export default CustomerDashboard;
