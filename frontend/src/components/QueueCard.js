import React from 'react';
import { useNavigate } from 'react-router-dom';

export default function QueueCard({ queue }) {
  const navigate = useNavigate();
  const statusBadge = { ACTIVE:'badge-green', PAUSED:'badge-yellow', CLOSED:'badge-red' };

  return (
    <div className="queue-card" onClick={() => navigate(`/queue/${queue.id}`)} style={{cursor:'pointer'}}>
      <div style={{display:'flex',justifyContent:'space-between',alignItems:'flex-start'}}>
        <div>
          <h3 style={{fontWeight:700,fontSize:'16px'}}>{queue.name}</h3>
          <p style={{color:'#6b7280',fontSize:'13px',marginTop:'2px'}}>{queue.branchName}</p>
        </div>
        <span className={`badge ${statusBadge[queue.status]}`}>{queue.status}</span>
      </div>
      <div style={{display:'grid',gridTemplateColumns:'1fr 1fr 1fr',gap:'12px',marginTop:'16px'}}>
        <div style={{textAlign:'center',background:'#f9fafb',borderRadius:'8px',padding:'10px'}}>
          <div style={{fontSize:'20px',fontWeight:700,color:'#4f46e5'}}>{queue.waitingCount}</div>
          <div style={{fontSize:'11px',color:'#6b7280'}}>Waiting</div>
        </div>
        <div style={{textAlign:'center',background:'#f9fafb',borderRadius:'8px',padding:'10px'}}>
          <div style={{fontSize:'20px',fontWeight:700,color:'#10b981'}}>{queue.estimatedWaitMinutes}</div>
          <div style={{fontSize:'11px',color:'#6b7280'}}>Est. Min</div>
        </div>
        <div style={{textAlign:'center',background:'#f9fafb',borderRadius:'8px',padding:'10px'}}>
          <div style={{fontSize:'20px',fontWeight:700,color:'#f59e0b'}}>{queue.activeStaff}</div>
          <div style={{fontSize:'11px',color:'#6b7280'}}>Staff</div>
        </div>
      </div>
    </div>
  );
}
