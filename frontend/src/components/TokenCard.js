import React from 'react';

export default function TokenCard({ token }) {
  if (!token) return null;
  const statusColor = { WAITING:'#f59e0b', CALLED:'#3b82f6', SERVING:'#10b981', SERVED:'#6b7280', NO_SHOW:'#ef4444', CANCELLED:'#6b7280' };
  const priorityLabel = { NORMAL:'Normal', VIP:'⭐ VIP', EMERGENCY:'🚨 Emergency' };

  return (
    <div className="token-card">
      <div className="token-label">YOUR TOKEN</div>
      <div className="token-number">{token.tokenNumber}</div>
      <div className="token-label" style={{marginTop:'8px'}}>{token.queueName}</div>
      <div style={{display:'flex',justifyContent:'center',gap:'24px',marginTop:'20px'}}>
        <div style={{textAlign:'center'}}>
          <div style={{fontSize:'28px',fontWeight:700}}>{token.positionInQueue ?? '—'}</div>
          <div style={{fontSize:'12px',opacity:0.8}}>Position</div>
        </div>
        <div style={{textAlign:'center'}}>
          <div style={{fontSize:'28px',fontWeight:700}}>{token.estimatedWaitMinutes ?? '—'}</div>
          <div style={{fontSize:'12px',opacity:0.8}}>Est. Min</div>
        </div>
      </div>
      <div style={{marginTop:'16px',display:'flex',justifyContent:'center',gap:'8px',flexWrap:'wrap'}}>
        <span style={{background:statusColor[token.status]||'#6b7280',color:'white',padding:'4px 14px',borderRadius:'20px',fontSize:'12px',fontWeight:600}}>
          {token.status}
        </span>
        <span style={{background:'rgba(255,255,255,0.2)',color:'white',padding:'4px 14px',borderRadius:'20px',fontSize:'12px'}}>
          {priorityLabel[token.priorityType]}
        </span>
      </div>
    </div>
  );
}
