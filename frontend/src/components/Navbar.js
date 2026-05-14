import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => { logout(); navigate('/login'); };

  return (
    <nav style={{background:'#4f46e5',padding:'0 24px',display:'flex',alignItems:'center',justifyContent:'space-between',height:'60px',boxShadow:'0 2px 8px rgba(0,0,0,0.15)'}}>
      <Link to="/" style={{color:'white',textDecoration:'none',fontWeight:700,fontSize:'20px',letterSpacing:'0.5px'}}>
        ⚡ SmartQueue
      </Link>
      {user && (
        <div style={{display:'flex',alignItems:'center',gap:'16px'}}>
          <span style={{color:'rgba(255,255,255,0.85)',fontSize:'14px'}}>
            👤 {user.username} <span style={{background:'rgba(255,255,255,0.2)',padding:'2px 8px',borderRadius:'20px',fontSize:'11px',marginLeft:'6px'}}>{user.role}</span>
          </span>
          <button onClick={handleLogout} style={{background:'rgba(255,255,255,0.15)',color:'white',border:'none',padding:'7px 16px',borderRadius:'8px',cursor:'pointer',fontWeight:500,fontSize:'13px'}}>
            Logout
          </button>
        </div>
      )}
    </nav>
  );
}
