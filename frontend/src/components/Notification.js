import React, { useEffect } from 'react';

export default function Notification({ message, onClose }) {
  useEffect(() => {
    const t = setTimeout(onClose, 5000);
    return () => clearTimeout(t);
  }, [onClose]);

  return (
    <div className="notification-popup">
      <div style={{fontWeight:700,marginBottom:'4px'}}>🔔 Your Turn is Near!</div>
      <div style={{fontSize:'14px',opacity:0.9}}>{message}</div>
      <button onClick={onClose} style={{background:'none',border:'none',color:'white',cursor:'pointer',float:'right',marginTop:'4px',fontSize:'18px'}}>×</button>
    </div>
  );
}
