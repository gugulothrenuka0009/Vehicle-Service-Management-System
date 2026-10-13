import React from 'react';

const StatCard = ({ title, value, icon: Icon, color = '#0284c7', bgColor = '#e0f2fe', subtitle }) => {
  return (
    <div className="stat-card">
      <div className="stat-info">
        <h4>{title}</h4>
        <div className="stat-value">{value}</div>
        {subtitle && <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginTop: '0.25rem' }}>{subtitle}</p>}
      </div>
      <div className="stat-icon-wrapper" style={{ backgroundColor: bgColor, color: color }}>
        {Icon && <Icon size={24} />}
      </div>
    </div>
  );
};

export default StatCard;
