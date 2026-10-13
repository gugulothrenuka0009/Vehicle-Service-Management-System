import React, { useState } from 'react';
import { useAuth, DEMO_ACCOUNTS } from '../context/AuthContext';
import { Wrench, Car, Shield, UserCheck, Key, Mail, Phone, User, ArrowRight } from 'lucide-react';

const Auth = () => {
  const { login, register, loading } = useAuth();
  const [isRegister, setIsRegister] = useState(false);
  const [error, setError] = useState('');

  // Form states
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [name, setName] = useState('');
  const [phone, setPhone] = useState('');
  const [role, setRole] = useState('CUSTOMER');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (isRegister) {
      if (!name || !email || !password) {
        setError('Please fill in all required fields');
        return;
      }
      const res = await register({ name, email, password, phone, role });
      if (!res.success) setError(res.message);
    } else {
      if (!email || !password) {
        setError('Please enter your email and password');
        return;
      }
      const res = await login(email, password);
      if (!res.success) setError(res.message);
    }
  };

  const handleQuickLogin = async (roleKey) => {
    setError('');
    const acc = DEMO_ACCOUNTS[roleKey];
    setEmail(acc.email);
    setPassword(acc.password);
    await login(acc.email, acc.password);
  };

  return (
    <div style={{
      minHeight: '100vh',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      background: 'linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 50%, #f8fafc 100%)',
      padding: '1.5rem',
    }}>
      <div style={{
        maxWidth: '1050px',
        width: '100%',
        display: 'grid',
        gridTemplateColumns: '1.1fr 1fr',
        background: 'white',
        borderRadius: '24px',
        boxShadow: 'var(--shadow-xl)',
        overflow: 'hidden',
        border: '1px solid var(--border)',
      }}>
        {/* Left Side: Brand & Quick Demo Logins */}
        <div style={{
          background: 'linear-gradient(145deg, #0284c7 0%, #0369a1 60%, #075985 100%)',
          padding: '3rem',
          color: 'white',
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'space-between',
        }}>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '1.5rem' }}>
              <div style={{ background: 'rgba(255, 255, 255, 0.2)', padding: '10px', borderRadius: '12px' }}>
                <Wrench size={30} color="white" />
              </div>
              <h1 style={{ fontSize: '1.6rem', fontWeight: 800, letterSpacing: '-0.02em' }}>
                VehicleCare <span style={{ color: '#bae6fd' }}>PRO</span>
              </h1>
            </div>

            <p style={{ fontSize: '1.05rem', color: '#e0f2fe', lineHeight: 1.6, marginBottom: '2.5rem' }}>
              Next-generation Vehicle Workshop Management System connecting Customers, Service Advisors, Mechanics, and Workshop Admins seamlessly.
            </p>

            <div style={{ background: 'rgba(255, 255, 255, 0.1)', backdropFilter: 'blur(10px)', borderRadius: '16px', padding: '1.5rem', border: '1px solid rgba(255, 255, 255, 0.2)' }}>
              <h3 style={{ fontSize: '0.95rem', fontWeight: 700, marginBottom: '1rem', color: 'white', display: 'flex', alignItems: 'center', gap: '8px' }}>
                <Key size={16} /> Instant 1-Click Demo Login
              </h3>
              <p style={{ fontSize: '0.8rem', color: '#bae6fd', marginBottom: '1rem' }}>
                Select any user role below to explore its tailored dashboard:
              </p>
              
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.75rem' }}>
                <button
                  type="button"
                  onClick={() => handleQuickLogin('CUSTOMER')}
                  style={{
                    background: 'rgba(255, 255, 255, 0.95)',
                    color: '#0369a1',
                    border: 'none',
                    padding: '0.65rem 0.85rem',
                    borderRadius: '8px',
                    fontWeight: 700,
                    fontSize: '0.85rem',
                    cursor: 'pointer',
                    display: 'flex',
                    alignItems: 'center',
                    gap: '6px',
                  }}
                >
                  <Car size={16} color="#0284c7" /> Customer
                </button>

                <button
                  type="button"
                  onClick={() => handleQuickLogin('SERVICE_ADVISOR')}
                  style={{
                    background: 'rgba(255, 255, 255, 0.95)',
                    color: '#0369a1',
                    border: 'none',
                    padding: '0.65rem 0.85rem',
                    borderRadius: '8px',
                    fontWeight: 700,
                    fontSize: '0.85rem',
                    cursor: 'pointer',
                    display: 'flex',
                    alignItems: 'center',
                    gap: '6px',
                  }}
                >
                  <UserCheck size={16} color="#0369a1" /> Advisor
                </button>

                <button
                  type="button"
                  onClick={() => handleQuickLogin('MECHANIC')}
                  style={{
                    background: 'rgba(255, 255, 255, 0.95)',
                    color: '#c2410c',
                    border: 'none',
                    padding: '0.65rem 0.85rem',
                    borderRadius: '8px',
                    fontWeight: 700,
                    fontSize: '0.85rem',
                    cursor: 'pointer',
                    display: 'flex',
                    alignItems: 'center',
                    gap: '6px',
                  }}
                >
                  <Wrench size={16} color="#ea580c" /> Mechanic
                </button>

                <button
                  type="button"
                  onClick={() => handleQuickLogin('ADMIN')}
                  style={{
                    background: 'rgba(255, 255, 255, 0.95)',
                    color: '#6d28d9',
                    border: 'none',
                    padding: '0.65rem 0.85rem',
                    borderRadius: '8px',
                    fontWeight: 700,
                    fontSize: '0.85rem',
                    cursor: 'pointer',
                    display: 'flex',
                    alignItems: 'center',
                    gap: '6px',
                  }}
                >
                  <Shield size={16} color="#8b5cf6" /> Admin
                </button>
              </div>
            </div>
          </div>

          <div style={{ fontSize: '0.8rem', color: '#bae6fd', marginTop: '2rem' }}>
            Spring Boot 3 REST API + MySQL + React + JWT Security
          </div>
        </div>

        {/* Right Side: Auth Form */}
        <div style={{ padding: '3rem' }}>
          <div style={{ marginBottom: '2rem' }}>
            <h2 style={{ fontSize: '1.6rem', fontWeight: 800, color: 'var(--text-main)', marginBottom: '0.35rem' }}>
              {isRegister ? 'Create an Account' : 'Welcome Back'}
            </h2>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.925rem' }}>
              {isRegister
                ? 'Sign up to register your vehicles and book service appointments'
                : 'Enter your credentials to access your workshop dashboard'}
            </p>
          </div>

          {error && (
            <div style={{
              background: 'var(--danger-bg)',
              color: 'var(--danger)',
              padding: '0.75rem 1rem',
              borderRadius: '8px',
              fontSize: '0.875rem',
              marginBottom: '1.25rem',
              fontWeight: 500,
              border: '1px solid #fecaca',
            }}>
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit}>
            {isRegister && (
              <>
                <div className="form-group">
                  <label>Full Name</label>
                  <input
                    type="text"
                    className="form-control"
                    placeholder="e.g. John Doe"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                  />
                </div>

                <div className="form-group">
                  <label>Phone Number</label>
                  <input
                    type="tel"
                    className="form-control"
                    placeholder="+91 9876543210"
                    value={phone}
                    onChange={(e) => setPhone(e.target.value)}
                  />
                </div>
              </>
            )}

            <div className="form-group">
              <label>Email Address</label>
              <input
                type="email"
                className="form-control"
                placeholder="name@example.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />
            </div>

            <div className="form-group">
              <label>Password</label>
              <input
                type="password"
                className="form-control"
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </div>

            {isRegister && (
              <div className="form-group">
                <label>Account Role</label>
                <select
                  className="form-control"
                  value={role}
                  onChange={(e) => setRole(e.target.value)}
                >
                  <option value="CUSTOMER">Customer (Vehicle Owner)</option>
                  <option value="SERVICE_ADVISOR">Service Advisor</option>
                  <option value="MECHANIC">Mechanic (Technician)</option>
                </select>
              </div>
            )}

            <button
              type="submit"
              className="btn btn-primary"
              disabled={loading}
              style={{ width: '100%', padding: '0.75rem', marginTop: '0.5rem' }}
            >
              {loading ? 'Please wait...' : isRegister ? 'Register Account' : 'Sign In'}
            </button>
          </form>

          <div style={{ textAlign: 'center', marginTop: '1.75rem' }}>
            <button
              type="button"
              onClick={() => { setIsRegister(!isRegister); setError(''); }}
              style={{
                background: 'none',
                border: 'none',
                color: 'var(--primary)',
                fontSize: '0.9rem',
                fontWeight: 600,
                cursor: 'pointer',
              }}
            >
              {isRegister ? 'Already have an account? Sign In' : "Don't have an account? Register here"}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Auth;
