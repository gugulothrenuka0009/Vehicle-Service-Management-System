import React from 'react';

const StatusBadge = ({ status }) => {
  if (!status) return null;
  const s = status.toUpperCase();
  const cls = `badge badge-${s.toLowerCase()}`;
  return (
    <span className={cls}>
      <span style={{ width: 6, height: 6, borderRadius: '50%', backgroundColor: 'currentColor', display: 'inline-block' }}></span>
      {s.replace(/_/g, ' ')}
    </span>
  );
};

export default StatusBadge;
