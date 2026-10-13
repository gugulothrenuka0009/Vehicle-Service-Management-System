import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import StatCard from '../components/StatCard';
import StatusBadge from '../components/StatusBadge';
import Modal from '../components/Modal';
import api from '../services/api';
import {
  Wrench,
  CheckCircle,
  Clock,
  Package,
  Plus,
  Trash2,
  FileText,
  DollarSign,
  Car,
  AlertTriangle,
} from 'lucide-react';
import confetti from 'canvas-confetti';

const MechanicDashboard = ({ activeTab = 'jobs' }) => {
  const { user } = useAuth();
  const [tab, setTab] = useState(activeTab);

  const [jobs, setJobs] = useState([]);
  const [inventory, setInventory] = useState([]);
  const [loading, setLoading] = useState(true);

  // Update Work Modal
  const [showUpdateModal, setShowUpdateModal] = useState(false);
  const [selectedJob, setSelectedJob] = useState(null);
  const [jobStatus, setJobStatus] = useState('IN_PROGRESS');
  const [diagnosisNotes, setDiagnosisNotes] = useState('');
  const [workSummary, setWorkSummary] = useState('');
  const [laborCost, setLaborCost] = useState('1200');

  // Used parts state: [{ inventoryId, quantity, unitPrice, itemName }]
  const [partsUsed, setPartsUsed] = useState([]);
  const [selectedPartId, setSelectedPartId] = useState('');
  const [partQty, setPartQty] = useState(1);

  useEffect(() => {
    setTab(activeTab);
  }, [activeTab]);

  useEffect(() => {
    fetchData();
  }, [user]);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [jRes, iRes] = await Promise.all([
        api.get('/jobs'),
        api.get('/inventory'),
      ]);
      setJobs(jRes.data?.data || []);
      setInventory(iRes.data?.data || []);
    } catch {
      // Mock Fallback
      setInventory([
        { id: 1, itemCode: 'PART-OIL-5W30', itemName: 'Synthetic Engine Oil 5W-30 (4L)', unitPrice: 1850.00, quantityInStock: 45, category: 'Lubricants' },
        { id: 2, itemCode: 'PART-FIL-OIL01', itemName: 'OEM Engine Oil Filter', unitPrice: 350.00, quantityInStock: 60, category: 'Filters' },
        { id: 3, itemCode: 'PART-FIL-AIR02', itemName: 'Engine Air Filter Element', unitPrice: 480.00, quantityInStock: 35, category: 'Filters' },
        { id: 5, itemCode: 'PART-BRK-PAD01', itemName: 'Ceramic Front Brake Pad Set', unitPrice: 2200.00, quantityInStock: 18, category: 'Brakes' },
        { id: 7, itemCode: 'PART-FLUID-DOT4', itemName: 'Brake Fluid DOT 4 (500ml)', unitPrice: 280.00, quantityInStock: 40, category: 'Fluids' },
        { id: 8, itemCode: 'PART-SPK-PLUG', itemName: 'Iridium Spark Plugs (Pack of 4)', unitPrice: 1450.00, quantityInStock: 22, category: 'Ignition' },
      ]);
      setJobs([
        {
          id: 1,
          jobNumber: 'JOB-202610-001',
          bookingNumber: 'BK-202610-002',
          customerName: 'Amit Verma',
          customerPhone: '+91 9876543214',
          regNumber: 'MH-12-CD-5678',
          vehicleDetails: 'Hyundai Creta SX (2022)',
          serviceName: 'Brake System Overhaul',
          status: 'IN_PROGRESS',
          diagnosisNotes: 'Front pads worn down to 2.5mm. Disc has minor runout. Brake fluid contains moisture.',
          workSummary: 'Replacing front brake pads with ceramic pads and flushing hydraulic system with DOT 4 fluid.',
          laborCost: 1200.00,
          partsCost: 2480.00,
          totalJobCost: 3680.00,
        },
        {
          id: 2,
          jobNumber: 'JOB-202610-002',
          bookingNumber: 'BK-202610-004',
          customerName: 'Priya Nair',
          customerPhone: '+91 9876543215',
          regNumber: 'KA-03-GH-3456',
          vehicleDetails: 'Toyota Innova Crysta (2020)',
          serviceName: 'General Periodic Service',
          status: 'COMPLETED',
          diagnosisNotes: 'Periodic maintenance check passed. Air filter replaced, oil drained.',
          workSummary: 'Drained engine oil, replaced oil filter, cleaned spark plugs, inspected underbody.',
          laborCost: 1500.00,
          partsCost: 2680.00,
          totalJobCost: 4180.00,
        },
      ]);
    } finally {
      setLoading(false);
    }
  };

  const openUpdateModal = (job) => {
    setSelectedJob(job);
    setJobStatus(job.status || 'IN_PROGRESS');
    setDiagnosisNotes(job.diagnosisNotes || '');
    setWorkSummary(job.workSummary || '');
    setLaborCost(job.laborCost || '1200');
    setPartsUsed([]);
    setShowUpdateModal(true);
  };

  const handleAddPartToJob = () => {
    if (!selectedPartId) return;
    const invItem = inventory.find((i) => i.id === Number(selectedPartId));
    if (!invItem) return;

    const existingIndex = partsUsed.findIndex((p) => p.inventoryId === invItem.id);
    if (existingIndex >= 0) {
      const copy = [...partsUsed];
      copy[existingIndex].quantity += Number(partQty);
      setPartsUsed(copy);
    } else {
      setPartsUsed([
        ...partsUsed,
        {
          inventoryId: invItem.id,
          itemName: invItem.itemName,
          unitPrice: invItem.unitPrice,
          quantity: Number(partQty),
        },
      ]);
    }
    setSelectedPartId('');
    setPartQty(1);
  };

  const handleRemovePart = (index) => {
    setPartsUsed(partsUsed.filter((_, i) => i !== index));
  };

  const handleUpdateJobSubmit = async (e) => {
    e.preventDefault();
    const partsTotal = partsUsed.reduce((acc, p) => acc + p.unitPrice * p.quantity, 0);
    const calculatedPartsCost = (selectedJob.partsCost || 0) + partsTotal;
    const calculatedTotalCost = Number(laborCost) + calculatedPartsCost;

    try {
      await api.put(`/jobs/${selectedJob.id}`, {
        status: jobStatus,
        diagnosisNotes,
        workSummary,
        laborCost: Number(laborCost),
        partsUsed: partsUsed.map((p) => ({ inventoryId: p.inventoryId, quantity: p.quantity })),
      });
    } catch {
      // local fallback
    }

    setJobs((prev) =>
      prev.map((j) =>
        j.id === selectedJob.id
          ? {
              ...j,
              status: jobStatus,
              diagnosisNotes,
              workSummary,
              laborCost: Number(laborCost),
              partsCost: calculatedPartsCost,
              totalJobCost: calculatedTotalCost,
            }
          : j
      )
    );

    if (jobStatus === 'COMPLETED') {
      confetti({ particleCount: 90, spread: 60, origin: { y: 0.6 } });
    }

    setShowUpdateModal(false);
    alert(`Job ${selectedJob.jobNumber} updated to ${jobStatus}!`);
  };

  const activeJobs = jobs.filter((j) => j.status !== 'COMPLETED');
  const completedJobs = jobs.filter((j) => j.status === 'COMPLETED');

  return (
    <div className="page-container">
      {/* Header */}
      <div style={{ marginBottom: '2rem' }}>
        <h1 style={{ fontSize: '1.75rem', fontWeight: 800 }}>Mechanic Workshop Console 🔧</h1>
        <p style={{ color: 'var(--text-muted)' }}>
          Assigned repair tasks, vehicle diagnostics, parts consumption, and job execution
        </p>
      </div>

      {/* Metrics */}
      <div className="stats-grid">
        <StatCard
          title="Active Work Orders"
          value={activeJobs.length}
          icon={Wrench}
          color="#ea580c"
          bgColor="#fff7ed"
          subtitle="Currently on lift / assigned"
        />
        <StatCard
          title="Jobs Completed"
          value={completedJobs.length}
          icon={CheckCircle}
          color="#10b981"
          bgColor="#ecfdf5"
          subtitle="Ready for vehicle handover"
        />
        <StatCard
          title="Spare Parts Available"
          value={inventory.length}
          icon={Package}
          color="#0284c7"
          bgColor="#e0f2fe"
          subtitle="Workshop stock inventory"
        />
      </div>

      {/* TABS */}
      <div className="tabs">
        <button
          className={`tab-btn ${tab === 'jobs' ? 'active' : ''}`}
          onClick={() => setTab('jobs')}
        >
          <Wrench size={16} /> My Assigned Jobs ({activeJobs.length})
        </button>
        <button
          className={`tab-btn ${tab === 'history' ? 'active' : ''}`}
          onClick={() => setTab('history')}
        >
          <CheckCircle size={16} /> Completed Jobs ({completedJobs.length})
        </button>
        <button
          className={`tab-btn ${tab === 'inventory' ? 'active' : ''}`}
          onClick={() => setTab('inventory')}
        >
          <Package size={16} /> Parts Catalog ({inventory.length})
        </button>
      </div>

      {/* TAB CONTENT: ACTIVE JOBS */}
      {tab === 'jobs' && (
        <div className="table-card">
          <div className="table-toolbar">
            <h3 style={{ fontSize: '1.05rem', fontWeight: 700 }}>Assigned Work Orders</h3>
            <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
              Showing only tasks assigned to technician: <strong>{user?.name || 'Vikram Singh'}</strong>
            </span>
          </div>

          <div style={{ overflowX: 'auto' }}>
            <table className="data-table">
              <thead>
                <tr>
                  <th>Job Number</th>
                  <th>Vehicle</th>
                  <th>Service Requested</th>
                  <th>Diagnosis / Work Notes</th>
                  <th>Parts & Labor</th>
                  <th>Status</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {activeJobs.length === 0 ? (
                  <tr>
                    <td colSpan={7} style={{ textAlign: 'center', padding: '2rem', color: 'var(--text-muted)' }}>
                      No active jobs currently assigned. Ready for new service allocations!
                    </td>
                  </tr>
                ) : (
                  activeJobs.map((j) => (
                    <tr key={j.id}>
                      <td>
                        <div style={{ fontWeight: 700, color: 'var(--primary)' }}>{j.jobNumber}</div>
                        <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Ref: {j.bookingNumber}</div>
                      </td>
                      <td>
                        <div style={{ fontWeight: 600 }}>{j.vehicleDetails}</div>
                        <span className="badge badge-confirmed" style={{ fontSize: '0.7rem', marginTop: '2px' }}>
                          {j.regNumber}
                        </span>
                      </td>
                      <td>
                        <div style={{ fontWeight: 600 }}>{j.serviceName}</div>
                        <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Customer: {j.customerName}</div>
                      </td>
                      <td style={{ maxWidth: '280px' }}>
                        {j.diagnosisNotes ? (
                          <div style={{ fontSize: '0.85rem', color: 'var(--text-main)', lineHeight: 1.4 }}>
                            {j.diagnosisNotes}
                          </div>
                        ) : (
                          <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)', fontStyle: 'italic' }}>
                            Awaiting initial vehicle inspection...
                          </span>
                        )}
                      </td>
                      <td>
                        <div style={{ fontSize: '0.85rem', fontWeight: 600 }}>
                          Parts: ₹{Number(j.partsCost || 0).toLocaleString('en-IN')}
                        </div>
                        <div style={{ fontSize: '0.85rem', fontWeight: 600 }}>
                          Labor: ₹{Number(j.laborCost || 0).toLocaleString('en-IN')}
                        </div>
                        <div style={{ fontWeight: 800, color: 'var(--primary)', marginTop: '2px' }}>
                          Total: ₹{Number(j.totalJobCost || 0).toLocaleString('en-IN')}
                        </div>
                      </td>
                      <td>
                        <StatusBadge status={j.status} />
                      </td>
                      <td>
                        <button
                          onClick={() => openUpdateModal(j)}
                          className="btn btn-primary btn-sm"
                        >
                          <Wrench size={14} /> Update Work
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* TAB CONTENT: COMPLETED JOBS */}
      {tab === 'history' && (
        <div className="table-card">
          <div className="table-toolbar">
            <h3 style={{ fontSize: '1.05rem', fontWeight: 700 }}>Service Completion Log</h3>
          </div>

          <div style={{ overflowX: 'auto' }}>
            <table className="data-table">
              <thead>
                <tr>
                  <th>Job Number</th>
                  <th>Vehicle</th>
                  <th>Work Completed Summary</th>
                  <th>Parts Cost</th>
                  <th>Labor Cost</th>
                  <th>Total Billed</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {completedJobs.map((j) => (
                  <tr key={j.id}>
                    <td style={{ fontWeight: 700, color: 'var(--primary)' }}>{j.jobNumber}</td>
                    <td>{j.vehicleDetails} ({j.regNumber})</td>
                    <td style={{ maxWidth: '300px', fontSize: '0.85rem' }}>{j.workSummary}</td>
                    <td>₹{Number(j.partsCost || 0).toLocaleString('en-IN')}</td>
                    <td>₹{Number(j.laborCost || 0).toLocaleString('en-IN')}</td>
                    <td style={{ fontWeight: 800, color: 'var(--text-main)' }}>
                      ₹{Number(j.totalJobCost || 0).toLocaleString('en-IN')}
                    </td>
                    <td><StatusBadge status="COMPLETED" /></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* TAB CONTENT: INVENTORY BROWSER */}
      {tab === 'inventory' && (
        <div className="table-card">
          <div className="table-toolbar">
            <h3 style={{ fontSize: '1.05rem', fontWeight: 700 }}>Workshop Spare Parts & Stock Availability</h3>
          </div>

          <div style={{ overflowX: 'auto' }}>
            <table className="data-table">
              <thead>
                <tr>
                  <th>Part Code</th>
                  <th>Item Name</th>
                  <th>Category</th>
                  <th>Unit Price</th>
                  <th>Available In Stock</th>
                  <th>Storage Location</th>
                </tr>
              </thead>
              <tbody>
                {inventory.map((inv) => (
                  <tr key={inv.id}>
                    <td style={{ fontWeight: 700, color: 'var(--primary)' }}>{inv.itemCode}</td>
                    <td style={{ fontWeight: 600 }}>{inv.itemName}</td>
                    <td>{inv.category}</td>
                    <td style={{ fontWeight: 700 }}>₹{Number(inv.unitPrice).toLocaleString('en-IN')}</td>
                    <td>
                      <span className={inv.quantityInStock <= 5 ? 'badge badge-low-stock' : 'badge badge-active'}>
                        {inv.quantityInStock} units
                      </span>
                    </td>
                    <td>{inv.location || 'Shelf A1'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* MODAL: UPDATE WORK, DIAGNOSIS, AND CONSUMED PARTS */}
      <Modal
        isOpen={showUpdateModal}
        onClose={() => setShowUpdateModal(false)}
        title="Update Repair Progress & Spare Parts"
        maxWidth="680px"
      >
        {selectedJob && (
          <form onSubmit={handleUpdateJobSubmit}>
            <div style={{
              background: 'var(--bg-surface-alt)',
              padding: '1rem',
              borderRadius: '8px',
              marginBottom: '1.25rem',
              border: '1px solid var(--border)',
            }}>
              <div style={{ fontWeight: 800, color: 'var(--text-main)', fontSize: '1.05rem' }}>
                Job: {selectedJob.jobNumber} - {selectedJob.vehicleDetails}
              </div>
              <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                Plate: <strong>{selectedJob.regNumber}</strong> | Requested: {selectedJob.serviceName}
              </div>
            </div>

            <div className="form-group">
              <label>Current Status *</label>
              <select
                className="form-control"
                value={jobStatus}
                onChange={(e) => setJobStatus(e.target.value)}
                required
              >
                <option value="IN_PROGRESS">IN_PROGRESS (Under active repair on bay)</option>
                <option value="ON_HOLD">ON_HOLD (Awaiting extra parts approval)</option>
                <option value="COMPLETED">COMPLETED (Repair finished & ready for billing)</option>
              </select>
            </div>

            <div className="form-group">
              <label>Diagnosis & Technical Inspection Notes *</label>
              <textarea
                className="form-control"
                rows={2}
                placeholder="e.g. Inspected brake pads, 2.5mm friction material remaining. Calipers cleaned."
                value={diagnosisNotes}
                onChange={(e) => setDiagnosisNotes(e.target.value)}
                required
              />
            </div>

            <div className="form-group">
              <label>Work Summary & Repairs Carried Out *</label>
              <textarea
                className="form-control"
                rows={2}
                placeholder="e.g. Installed new ceramic brake pads, bled hydraulic fluid, test driven 5 km."
                value={workSummary}
                onChange={(e) => setWorkSummary(e.target.value)}
                required
              />
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Labor Charges (₹) *</label>
                <input
                  type="number"
                  className="form-control"
                  value={laborCost}
                  onChange={(e) => setLaborCost(e.target.value)}
                  required
                />
              </div>
            </div>

            {/* Consumed Spare Parts Section */}
            <div style={{
              border: '1px solid var(--border)',
              borderRadius: '8px',
              padding: '1rem',
              marginBottom: '1.25rem',
              background: '#fafafa',
            }}>
              <h4 style={{ fontSize: '0.9rem', fontWeight: 700, marginBottom: '0.5rem', display: 'flex', alignItems: 'center', gap: '6px' }}>
                <Package size={16} /> Add Used Spare Parts & Consumables
              </h4>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 90px auto', gap: '0.5rem', marginBottom: '0.75rem' }}>
                <select
                  className="form-control"
                  value={selectedPartId}
                  onChange={(e) => setSelectedPartId(e.target.value)}
                >
                  <option value="">-- Choose Spare Part --</option>
                  {inventory.map((inv) => (
                    <option key={inv.id} value={inv.id}>
                      {inv.itemName} (₹{inv.unitPrice}) [Stock: {inv.quantityInStock}]
                    </option>
                  ))}
                </select>

                <input
                  type="number"
                  min="1"
                  className="form-control"
                  value={partQty}
                  onChange={(e) => setPartQty(e.target.value)}
                />

                <button
                  type="button"
                  onClick={handleAddPartToJob}
                  className="btn btn-outline btn-sm"
                  style={{ height: '38px' }}
                >
                  <Plus size={16} /> Add
                </button>
              </div>

              {partsUsed.length > 0 && (
                <div style={{ background: 'white', borderRadius: '6px', border: '1px solid var(--border)', padding: '0.5rem' }}>
                  <div style={{ fontSize: '0.8rem', fontWeight: 700, color: 'var(--text-muted)', marginBottom: '4px' }}>
                    Parts added to this update:
                  </div>
                  {partsUsed.map((p, idx) => (
                    <div
                      key={idx}
                      style={{
                        display: 'flex',
                        justifyContent: 'space-between',
                        alignItems: 'center',
                        fontSize: '0.85rem',
                        padding: '4px 0',
                        borderBottom: idx < partsUsed.length - 1 ? '1px dashed var(--border)' : 'none',
                      }}
                    >
                      <span>{p.itemName} (x{p.quantity})</span>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                        <span style={{ fontWeight: 700 }}>₹{(p.unitPrice * p.quantity).toLocaleString('en-IN')}</span>
                        <button
                          type="button"
                          onClick={() => handleRemovePart(idx)}
                          style={{ background: 'none', border: 'none', color: 'var(--danger)', cursor: 'pointer' }}
                        >
                          <Trash2 size={14} />
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>

            <div className="modal-footer" style={{ borderTop: 'none', padding: '0.5rem 0 0' }}>
              <button type="button" onClick={() => setShowUpdateModal(false)} className="btn btn-outline">
                Cancel
              </button>
              <button
                type="submit"
                className={`btn ${jobStatus === 'COMPLETED' ? 'btn-success' : 'btn-primary'}`}
              >
                {jobStatus === 'COMPLETED' ? 'Mark Completed & Generate Bill' : 'Save Progress'}
              </button>
            </div>
          </form>
        )}
      </Modal>
    </div>
  );
};

export default MechanicDashboard;
