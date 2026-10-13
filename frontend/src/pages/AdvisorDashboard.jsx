import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import StatCard from '../components/StatCard';
import StatusBadge from '../components/StatusBadge';
import Modal from '../components/Modal';
import api from '../services/api';
import {
  Calendar,
  Wrench,
  Clock,
  CheckCircle,
  AlertCircle,
  UserCheck,
  Search,
  Filter,
  DollarSign,
  Car,
} from 'lucide-react';

const AdvisorDashboard = ({ activeTab = 'requests' }) => {
  const { user } = useAuth();
  const [tab, setTab] = useState(activeTab);

  const [bookings, setBookings] = useState([]);
  const [mechanics, setMechanics] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');

  // Assign Mechanic Modal
  const [showAssignModal, setShowAssignModal] = useState(false);
  const [selectedBooking, setSelectedBooking] = useState(null);
  const [selectedMechanicId, setSelectedMechanicId] = useState('');
  const [estimatedCost, setEstimatedCost] = useState('');
  const [advisorNotes, setAdvisorNotes] = useState('');

  useEffect(() => {
    setTab(activeTab);
  }, [activeTab]);

  useEffect(() => {
    fetchData();
  }, [user]);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [bRes, mRes] = await Promise.all([
        api.get('/bookings'),
        api.get('/users/mechanics'),
      ]);
      setBookings(bRes.data?.data || []);
      setMechanics(mRes.data?.data || []);
    } catch {
      // Mock fallback data
      setMechanics([
        { id: 3, name: 'Vikram Singh', email: 'mechanic@vehicleservice.com', phone: '+91 9876543212', active: true },
        { id: 4, name: 'Suresh Patel', email: 'mechanic2@vehicleservice.com', phone: '+91 9876543213', active: true },
      ]);
      setBookings([
        {
          id: 1,
          bookingNumber: 'BK-202610-001',
          customerName: 'Amit Verma',
          customerPhone: '+91 9876543214',
          regNumber: 'MH-12-AB-1234',
          vehicleDetails: 'Honda City ZX (2021)',
          serviceName: 'General Periodic Service',
          appointmentDate: '2026-10-14',
          appointmentTime: '10:00 AM',
          notes: 'Regular scheduled 30k maintenance and check front brake pad wear.',
          status: 'PENDING',
          estimatedCost: 2499.00,
        },
        {
          id: 2,
          bookingNumber: 'BK-202610-002',
          customerName: 'Amit Verma',
          customerPhone: '+91 9876543214',
          regNumber: 'MH-12-CD-5678',
          vehicleDetails: 'Hyundai Creta SX (2022)',
          serviceName: 'Brake System Overhaul',
          appointmentDate: '2026-10-12',
          appointmentTime: '02:00 PM',
          notes: 'Brake pedal feels soft during hard stopping.',
          status: 'IN_PROGRESS',
          advisorName: 'Rajesh Sharma',
          estimatedCost: 3800.00,
        },
        {
          id: 3,
          bookingNumber: 'BK-202610-003',
          customerName: 'Priya Nair',
          customerPhone: '+91 9876543215',
          regNumber: 'DL-01-EF-9012',
          vehicleDetails: 'Tata Nexon EV (2023)',
          serviceName: 'Air Conditioning & Climate Service',
          appointmentDate: '2026-10-16',
          appointmentTime: '11:30 AM',
          notes: 'AC cooling feels weak in heavy traffic.',
          status: 'PENDING',
          estimatedCost: 2199.00,
        },
        {
          id: 4,
          bookingNumber: 'BK-202610-004',
          customerName: 'Priya Nair',
          customerPhone: '+91 9876543215',
          regNumber: 'KA-03-GH-3456',
          vehicleDetails: 'Toyota Innova Crysta (2020)',
          serviceName: 'General Periodic Service',
          appointmentDate: '2026-10-10',
          appointmentTime: '09:00 AM',
          notes: 'Completed 60k km major service.',
          status: 'COMPLETED',
          advisorName: 'Rajesh Sharma',
          estimatedCost: 5450.00,
        },
      ]);
    } finally {
      setLoading(false);
    }
  };

  const openAssignModal = (booking) => {
    setSelectedBooking(booking);
    setEstimatedCost(booking.estimatedCost || '');
    setSelectedMechanicId(mechanics[0]?.id || '');
    setShowAssignModal(true);
  };

  const handleAssignSubmit = async (e) => {
    e.preventDefault();
    if (!selectedMechanicId) {
      alert('Please select a mechanic');
      return;
    }

    try {
      await api.post('/jobs/assign', {
        bookingId: selectedBooking.id,
        mechanicId: Number(selectedMechanicId),
      });
      if (estimatedCost) {
        await api.patch(`/bookings/${selectedBooking.id}/status`, {
          status: 'IN_PROGRESS',
          estimatedCost: Number(estimatedCost),
        });
      }
    } catch {
      // local fallback
    }

    const assignedMech = mechanics.find((m) => m.id === Number(selectedMechanicId));
    setBookings((prev) =>
      prev.map((b) =>
        b.id === selectedBooking.id
          ? {
              ...b,
              status: 'IN_PROGRESS',
              advisorName: user?.name || 'Rajesh Sharma',
              estimatedCost: Number(estimatedCost) || b.estimatedCost,
              mechanicName: assignedMech?.name || 'Vikram Singh',
            }
          : b
      )
    );

    setShowAssignModal(false);
    alert(`Booking ${selectedBooking.bookingNumber} successfully assigned to ${assignedMech?.name || 'Mechanic'}!`);
  };

  const handleUpdateStatus = async (bookingId, newStatus) => {
    try {
      await api.patch(`/bookings/${bookingId}/status`, {
        status: newStatus,
      });
    } catch {
      // fallback
    }
    setBookings((prev) =>
      prev.map((b) => (b.id === bookingId ? { ...b, status: newStatus } : b))
    );
  };

  const filteredBookings = bookings.filter((b) => {
    const matchesSearch =
      (b.bookingNumber && b.bookingNumber.toLowerCase().includes(searchTerm.toLowerCase())) ||
      (b.customerName && b.customerName.toLowerCase().includes(searchTerm.toLowerCase())) ||
      (b.regNumber && b.regNumber.toLowerCase().includes(searchTerm.toLowerCase())) ||
      (b.serviceName && b.serviceName.toLowerCase().includes(searchTerm.toLowerCase()));

    const matchesStatus = statusFilter === 'ALL' || b.status === statusFilter;
    return matchesSearch && matchesStatus;
  });

  const pendingCount = bookings.filter((b) => b.status === 'PENDING').length;
  const inProgressCount = bookings.filter((b) => b.status === 'IN_PROGRESS').length;
  const completedCount = bookings.filter((b) => b.status === 'COMPLETED').length;

  return (
    <div className="page-container">
      {/* Header */}
      <div style={{ marginBottom: '2rem' }}>
        <h1 style={{ fontSize: '1.75rem', fontWeight: 800 }}>Service Advisor Desk 📋</h1>
        <p style={{ color: 'var(--text-muted)' }}>
          Review inbound customer service bookings, assign skilled mechanics, and manage repair workflows
        </p>
      </div>

      {/* Metrics */}
      <div className="stats-grid">
        <StatCard
          title="Total Service Requests"
          value={bookings.length}
          icon={Calendar}
          color="#0284c7"
          bgColor="#e0f2fe"
          subtitle="All received appointments"
        />
        <StatCard
          title="Pending Allocation"
          value={pendingCount}
          icon={Clock}
          color="#f59e0b"
          bgColor="#fffbeb"
          subtitle="Awaiting mechanic assignment"
        />
        <StatCard
          title="Active on Workshop Floor"
          value={inProgressCount}
          icon={Wrench}
          color="#0284c7"
          bgColor="#e0f2fe"
          subtitle="Repairs currently underway"
        />
        <StatCard
          title="Completed Jobs"
          value={completedCount}
          icon={CheckCircle}
          color="#10b981"
          bgColor="#ecfdf5"
          subtitle="Ready for billing / delivered"
        />
      </div>

      {/* Requests Management Table */}
      <div className="table-card">
        <div className="table-toolbar">
          <div className="search-input-wrapper">
            <Search size={16} />
            <input
              type="text"
              placeholder="Search by customer, plate MH-12, booking #..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>

          <div style={{ display: 'flex', gap: '0.75rem', alignItems: 'center' }}>
            <Filter size={16} color="var(--text-muted)" />
            <select
              className="form-control"
              style={{ width: 'auto', padding: '0.45rem 0.75rem' }}
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
            >
              <option value="ALL">All Statuses</option>
              <option value="PENDING">Pending Approval</option>
              <option value="CONFIRMED">Confirmed</option>
              <option value="IN_PROGRESS">In Progress</option>
              <option value="COMPLETED">Completed</option>
            </select>
          </div>
        </div>

        <div style={{ overflowX: 'auto' }}>
          <table className="data-table">
            <thead>
              <tr>
                <th>Booking Details</th>
                <th>Customer & Phone</th>
                <th>Vehicle & License</th>
                <th>Service & Notes</th>
                <th>Slot</th>
                <th>Est. Cost</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredBookings.length === 0 ? (
                <tr>
                  <td colSpan={8} style={{ textAlign: 'center', padding: '2rem', color: 'var(--text-muted)' }}>
                    No bookings matching criteria
                  </td>
                </tr>
              ) : (
                filteredBookings.map((b) => (
                  <tr key={b.id}>
                    <td>
                      <div style={{ fontWeight: 700, color: 'var(--primary)' }}>{b.bookingNumber}</div>
                      <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{b.appointmentDate}</div>
                    </td>
                    <td>
                      <div style={{ fontWeight: 600 }}>{b.customerName}</div>
                      <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{b.customerPhone}</div>
                    </td>
                    <td>
                      <div style={{ fontWeight: 600 }}>{b.vehicleDetails || b.regNumber}</div>
                      <span className="badge badge-confirmed" style={{ fontSize: '0.7rem', marginTop: '2px' }}>
                        {b.regNumber}
                      </span>
                    </td>
                    <td style={{ maxWidth: '240px' }}>
                      <div style={{ fontWeight: 600, color: 'var(--text-main)' }}>{b.serviceName}</div>
                      {b.notes && (
                        <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                          ⚠️ {b.notes}
                        </div>
                      )}
                    </td>
                    <td>
                      <span style={{ fontSize: '0.85rem', fontWeight: 600 }}>{b.appointmentTime}</span>
                    </td>
                    <td>
                      <span style={{ fontWeight: 700 }}>₹{Number(b.estimatedCost || 0).toLocaleString('en-IN')}</span>
                    </td>
                    <td>
                      <StatusBadge status={b.status} />
                    </td>
                    <td>
                      <div style={{ display: 'flex', gap: '6px' }}>
                        {b.status === 'PENDING' && (
                          <button
                            onClick={() => openAssignModal(b)}
                            className="btn btn-primary btn-sm"
                            title="Assign to Mechanic"
                          >
                            <UserCheck size={14} /> Assign
                          </button>
                        )}
                        {b.status === 'CONFIRMED' && (
                          <button
                            onClick={() => openAssignModal(b)}
                            className="btn btn-primary btn-sm"
                          >
                            <Wrench size={14} /> Assign Mechanic
                          </button>
                        )}
                        {b.status === 'IN_PROGRESS' && (
                          <button
                            onClick={() => handleUpdateStatus(b.id, 'COMPLETED')}
                            className="btn btn-success btn-sm"
                            title="Mark Service Finished"
                          >
                            <CheckCircle size={14} /> Finish
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

      {/* ASSIGN MECHANIC MODAL */}
      <Modal
        isOpen={showAssignModal}
        onClose={() => setShowAssignModal(false)}
        title="Assign Booking to Mechanic"
      >
        {selectedBooking && (
          <form onSubmit={handleAssignSubmit}>
            <div style={{
              background: 'var(--bg-surface-alt)',
              padding: '1rem',
              borderRadius: '8px',
              marginBottom: '1.25rem',
              border: '1px solid var(--border)',
            }}>
              <div style={{ fontWeight: 700, fontSize: '1rem', color: 'var(--text-main)', marginBottom: '4px' }}>
                {selectedBooking.vehicleDetails} ({selectedBooking.regNumber})
              </div>
              <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                Customer: <strong>{selectedBooking.customerName}</strong> | Slot: {selectedBooking.appointmentDate} at {selectedBooking.appointmentTime}
              </div>
              <div style={{ fontSize: '0.85rem', color: 'var(--primary)', marginTop: '4px', fontWeight: 600 }}>
                Package: {selectedBooking.serviceName}
              </div>
              {selectedBooking.notes && (
                <div style={{ marginTop: '0.5rem', padding: '0.5rem', background: '#fffbeb', borderRadius: '4px', fontSize: '0.8rem', color: '#92400e' }}>
                  Customer Notes: "{selectedBooking.notes}"
                </div>
              )}
            </div>

            <div className="form-group">
              <label>Select Available Mechanic *</label>
              <select
                className="form-control"
                value={selectedMechanicId}
                onChange={(e) => setSelectedMechanicId(e.target.value)}
                required
              >
                {mechanics.map((m) => (
                  <option key={m.id} value={m.id}>
                    🔧 {m.name} ({m.phone || m.email})
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label>Estimated Service Cost (₹)</label>
              <input
                type="number"
                className="form-control"
                placeholder="2499"
                value={estimatedCost}
                onChange={(e) => setEstimatedCost(e.target.value)}
              />
            </div>

            <div className="form-group">
              <label>Service Advisor Instructions to Mechanic</label>
              <textarea
                className="form-control"
                rows={2}
                placeholder="e.g. Conduct thorough brake pad thickness test and check caliper sliding pins."
                value={advisorNotes}
                onChange={(e) => setAdvisorNotes(e.target.value)}
              />
            </div>

            <div className="modal-footer" style={{ borderTop: 'none', padding: '0.75rem 0 0' }}>
              <button type="button" onClick={() => setShowAssignModal(false)} className="btn btn-outline">
                Cancel
              </button>
              <button type="submit" className="btn btn-primary">
                Dispatch & Assign Job
              </button>
            </div>
          </form>
        )}
      </Modal>
    </div>
  );
};

export default AdvisorDashboard;
