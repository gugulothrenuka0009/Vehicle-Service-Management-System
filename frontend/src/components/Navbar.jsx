import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { Wrench, Bell, LogOut, CheckCircle, Car } from 'lucide-react';
import api from '../services/api';

const Navbar = () => {
  const { user, logout } = useAuth();
  const [notifications, setNotifications] = useState([]);
  const [showNotifs, setShowNotifs] = useState(false);
  const [unreadCount, setUnreadCount] = useState(0);

  useEffect(() => {
    if (user) {
      fetchNotifications();
    }
  }, [user]);

  const fetchNotifications = async () => {
    try {
      const res = await api.get('/notifications');
      if (res.data?.success) {
        setNotifications(res.data.data);
        setUnreadCount(res.data.data.filter((n) => !n.read).length);
      }
    } catch {
      // Mock fallback notifications
      const mockNotifs = [
        { id: 1, title: 'Welcome to VehicleCare', message: `Logged in as ${user?.role}`, read: false, createdAt: new Date().toISOString() },
        { id: 2, title: 'System Active', message: 'Vehicle Service Management is online', read: true, createdAt: new Date().toISOString() },
      ];
      setNotifications(mockNotifs);
      setUnreadCount(mockNotifs.filter((n) => !n.read).length);
    }
  };

  const markAllRead = async () => {
    try {
      await api.patch('/notifications/read-all');
    } catch {
      // fallback
    }
    setNotifications((prev) => prev.map((n) => ({ ...n, read: true })));
    setUnreadCount(0);
  };

  const getRoleBadgeColor = (role) => {
    switch (role) {
      case 'ADMIN': return '#8b5cf6';
      case 'SERVICE_ADVISOR': return '#0284c7';
      case 'MECHANIC': return '#ea580c';
      case 'CUSTOMER': return '#10b981';
      default: return '#64748b';
    }
  };

  return (
    <header className="navbar">
      <div className="navbar-brand">
        <div style={{
          background: 'linear-gradient(135deg, #0284c7, #0369a1)',
          padding: '8px',
          borderRadius: '10px',
          color: 'white',
          display: 'flex',
          alignItems: 'center'
        }}>
          <Wrench size={22} color="white" />
        </div>
        <div>
          <span style={{ color: '#0f172a', fontWeight: 800 }}>AutoCare</span>{' '}
          <span style={{ color: 'var(--primary)', fontWeight: 800 }}>PRO</span>
        </div>
      </div>

      <div className="navbar-actions">
        {/* Notifications */}
        <div style={{ position: 'relative' }}>
          <button
            className="notif-btn"
            onClick={() => setShowNotifs(!showNotifs)}
            aria-label="Notifications"
          >
            <Bell size={20} />
            {unreadCount > 0 && <span className="notif-badge">{unreadCount}</span>}
          </button>

          {showNotifs && (
            <div
              style={{
                position: 'absolute',
                top: '50px',
                right: 0,
                width: '340px',
                background: 'white',
                borderRadius: '12px',
                boxShadow: 'var(--shadow-xl)',
                border: '1px solid var(--border)',
                zIndex: 100,
                padding: '1rem',
              }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.75rem', paddingBottom: '0.5rem', borderBottom: '1px solid var(--border)' }}>
                <h4 style={{ fontSize: '0.95rem', fontWeight: 700 }}>Notifications</h4>
                {unreadCount > 0 && (
                  <button
                    onClick={markAllRead}
                    style={{ background: 'none', border: 'none', color: 'var(--primary)', fontSize: '0.75rem', fontWeight: 600, cursor: 'pointer' }}
                  >
                    Mark all read
                  </button>
                )}
              </div>
              <div style={{ maxHeight: '280px', overflowY: 'auto', display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
                {notifications.length === 0 ? (
                  <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', textAlign: 'center', padding: '1rem' }}>No notifications</p>
                ) : (
                  notifications.map((n) => (
                    <div
                      key={n.id}
                      style={{
                        padding: '0.65rem',
                        borderRadius: '8px',
                        background: n.read ? '#fafafa' : 'var(--primary-light)',
                        borderLeft: `3px solid ${n.read ? '#cbd5e1' : 'var(--primary)'}`,
                      }}
                    >
                      <div style={{ fontSize: '0.85rem', fontWeight: 700, color: 'var(--text-main)' }}>{n.title}</div>
                      <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginTop: '2px' }}>{n.message}</div>
                    </div>
                  ))
                )}
              </div>
            </div>
          )}
        </div>

        {/* User Pill */}
        <div className="user-pill">
          <div className="user-avatar">{user?.name ? user.name.charAt(0).toUpperCase() : 'U'}</div>
          <div>
            <div style={{ fontSize: '0.85rem', fontWeight: 700, lineHeight: 1.2 }}>{user?.name || 'User'}</div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '4px', marginTop: '2px' }}>
              <span
                style={{
                  fontSize: '0.65rem',
                  fontWeight: 800,
                  textTransform: 'uppercase',
                  color: getRoleBadgeColor(user?.role),
                  backgroundColor: `${getRoleBadgeColor(user?.role)}15`,
                  padding: '1px 6px',
                  borderRadius: '4px',
                }}
              >
                {user?.role?.replace('_', ' ')}
              </span>
            </div>
          </div>
        </div>

        {/* Logout */}
        <button
          onClick={logout}
          className="btn btn-outline btn-sm"
          style={{ gap: '0.35rem', color: 'var(--danger)' }}
          title="Sign out"
        >
          <LogOut size={16} />
          <span>Logout</span>
        </button>
      </div>
    </header>
  );
};

export default Navbar;
