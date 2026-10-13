import React from 'react';
import { useAuth, DEMO_ACCOUNTS } from '../context/AuthContext';
import {
  LayoutDashboard,
  Calendar,
  Car,
  FileText,
  Users,
  Settings,
  Package,
  CheckSquare,
  Wrench,
  BarChart3,
  RefreshCw,
} from 'lucide-react';

const Sidebar = ({ activeTab, setActiveTab }) => {
  const { user, role, login } = useAuth();

  const getMenuItems = () => {
    switch (role) {
      case 'CUSTOMER':
        return [
          { id: 'dashboard', label: 'My Bookings', icon: Calendar },
          { id: 'vehicles', label: 'My Vehicles', icon: Car },
          { id: 'invoices', label: 'Invoices & Payments', icon: FileText },
        ];
      case 'SERVICE_ADVISOR':
        return [
          { id: 'requests', label: 'Service Requests', icon: Calendar },
          { id: 'assigned', label: 'Assigned Jobs', icon: Wrench },
          { id: 'vehicles', label: 'Customer Vehicles', icon: Car },
        ];
      case 'MECHANIC':
        return [
          { id: 'jobs', label: 'Assigned Jobs', icon: CheckSquare },
          { id: 'history', label: 'Completed Jobs', icon: FileText },
          { id: 'inventory', label: 'Parts Catalog', icon: Package },
        ];
      case 'ADMIN':
        return [
          { id: 'analytics', label: 'Dashboard & Reports', icon: LayoutDashboard },
          { id: 'bookings', label: 'All Bookings', icon: Calendar },
          { id: 'users', label: 'User Management', icon: Users },
          { id: 'services', label: 'Services Catalog', icon: Settings },
          { id: 'inventory', label: 'Inventory & Stock', icon: Package },
        ];
      default:
        return [];
    }
  };

  const menuItems = getMenuItems();

  const handleRoleSwitch = async (targetRole) => {
    const acc = DEMO_ACCOUNTS[targetRole];
    if (acc) {
      await login(acc.email, acc.password);
      setActiveTab(
        targetRole === 'CUSTOMER' ? 'dashboard' :
        targetRole === 'SERVICE_ADVISOR' ? 'requests' :
        targetRole === 'MECHANIC' ? 'jobs' : 'analytics'
      );
    }
  };

  return (
    <aside className="sidebar">
      <div className="sidebar-nav">
        <div style={{ padding: '0.5rem 0.75rem', fontSize: '0.75rem', fontWeight: 800, color: 'var(--text-light)', textTransform: 'uppercase' }}>
          {role?.replace('_', ' ')} MENU
        </div>

        {menuItems.map((item) => {
          const Icon = item.icon;
          const isActive = activeTab === item.id;
          return (
            <button
              key={item.id}
              className={`sidebar-item ${isActive ? 'active' : ''}`}
              onClick={() => setActiveTab(item.id)}
            >
              <Icon size={18} />
              <span>{item.label}</span>
            </button>
          );
        })}
      </div>

      {/* Role Switcher Demo Box */}
      <div className="sidebar-footer">
        <div style={{ fontSize: '0.75rem', fontWeight: 700, color: 'var(--text-muted)', marginBottom: '0.5rem', display: 'flex', alignItems: 'center', gap: '4px' }}>
          <RefreshCw size={12} />
          <span>Quick Role Switch (Demo)</span>
        </div>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '4px' }}>
          {Object.keys(DEMO_ACCOUNTS).map((rKey) => (
            <button
              key={rKey}
              onClick={() => handleRoleSwitch(rKey)}
              style={{
                fontSize: '0.7rem',
                padding: '4px 6px',
                borderRadius: '6px',
                border: '1px solid var(--border)',
                background: role === rKey ? 'var(--primary-light)' : '#f8fafc',
                color: role === rKey ? 'var(--primary)' : 'var(--text-main)',
                fontWeight: role === rKey ? 700 : 500,
                cursor: 'pointer',
              }}
            >
              {rKey === 'SERVICE_ADVISOR' ? 'Advisor' : rKey.charAt(0) + rKey.slice(1).toLowerCase()}
            </button>
          ))}
        </div>
      </div>
    </aside>
  );
};

export default Sidebar;
