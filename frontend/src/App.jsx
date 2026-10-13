import React, { useState, useEffect } from 'react';
import { AuthProvider, useAuth } from './context/AuthContext';
import Navbar from './components/Navbar';
import Sidebar from './components/Sidebar';
import Auth from './pages/Auth';
import CustomerDashboard from './pages/CustomerDashboard';
import AdvisorDashboard from './pages/AdvisorDashboard';
import MechanicDashboard from './pages/MechanicDashboard';
import AdminDashboard from './pages/AdminDashboard';
import './index.css';

const MainApp = () => {
  const { isAuthenticated, role, loading } = useAuth();
  const [activeTab, setActiveTab] = useState('dashboard');

  useEffect(() => {
    switch (role) {
      case 'SERVICE_ADVISOR':
        setActiveTab('requests');
        break;
      case 'MECHANIC':
        setActiveTab('jobs');
        break;
      case 'ADMIN':
        setActiveTab('analytics');
        break;
      default:
        setActiveTab('dashboard');
        break;
    }
  }, [role]);

  if (loading) {
    return (
      <div style={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        background: 'var(--bg-main)',
        color: 'var(--primary)',
        fontWeight: 700,
        fontSize: '1.25rem',
      }}>
        Loading VehicleCare PRO...
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Auth />;
  }

  const renderDashboard = () => {
    switch (role) {
      case 'CUSTOMER':
        return <CustomerDashboard activeTab={activeTab} />;
      case 'SERVICE_ADVISOR':
        return <AdvisorDashboard activeTab={activeTab} />;
      case 'MECHANIC':
        return <MechanicDashboard activeTab={activeTab} />;
      case 'ADMIN':
        return <AdminDashboard activeTab={activeTab} />;
      default:
        return <CustomerDashboard activeTab={activeTab} />;
    }
  };

  return (
    <div className="app-layout">
      <Sidebar activeTab={activeTab} setActiveTab={setActiveTab} />
      <div className="main-content">
        <Navbar />
        <main style={{ flex: 1 }}>{renderDashboard()}</main>
      </div>
    </div>
  );
};

export default function App() {
  return (
    <AuthProvider>
      <MainApp />
    </AuthProvider>
  );
}
