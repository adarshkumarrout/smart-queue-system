import React, { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getQueueSnapshot, joinQueue } from '../services/queueService';
import { connectWebSocket, subscribeToQueue, disconnectWebSocket } from '../websocket/wsClient';
import { useAuth } from '../context/AuthContext';
import Notification from '../components/Notification';

export default function QueuePage() {
  const { queueId } = useParams();
  const [queue, setQueue] = useState(null);
  const [joining, setJoining] = useState(false);
  const [priority, setPriority] = useState('NORMAL');
  const [notification, setNotification] = useState(null);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const { user } = useAuth();
  const navigate = useNavigate();

  const fetchQueue = useCallback(async () => {
    try {
      const res = await getQueueSnapshot(queueId);
      setQueue(res.data.data);
    } catch (e) { console.error(e); }
  }, [queueId]);

  useEffect(() => {
    fetchQueue();
    connectWebSocket(user.userId, fetchQueue, (n) => setNotification(n.message));
    const sub = subscribeToQueue(queueId, fetchQueue);
    return () => { disconnectWebSocket(); sub && sub.unsubscribe(); };
  }, [queueId, user.userId, fetchQueue]);

  const handleJoin = async () => {
    setJoining(true); setError(''); setSuccess('');
    try {
      await joinQueue({ queueId: parseInt(queueId), priorityType: priority });
      setSuccess('✅ Successfully joined the queue!');
      fetchQueue();
    } catch (e) {
      setError(e.response?.data?.message || 'Failed to join queue');
    } finally { setJoining(false); }
  };

  if (!queue) return <div className="loading">Loading queue...</div>;

  return (
    <div className="container" style={{maxWidth:'700px'}}>
      {notification && <Notification message={notification} onClose={() => setNotification(null)} />}
      <button onClick={() => navigate(-1)} style={{background:'none',border:'none',cursor:'pointer',color:'#4f46e5',fontWeight:600,marginBottom:'16px',fontSize:'14px'}}>
        ← Back
      </button>

      <div className="card">
        <div style={{display:'flex',justifyContent:'space-between',alignItems:'flex-start',marginBottom:'20px'}}>
          <div>
            <h2 style={{fontSize:'22px',fontWeight:700}}>{queue.name}</h2>
            <p style={{color:'#6b7280',marginTop:'4px'}}>{queue.branchName}</p>
          </div>
          <span className={`badge ${queue.status==='ACTIVE'?'badge-green':queue.status==='PAUSED'?'badge-yellow':'badge-red'}`}>{queue.status}</span>
        </div>

        <div className="grid-3" style={{marginBottom:'20px'}}>
          {[['🧑‍🤝‍🧑', queue.waitingCount, 'Waiting'],['⏱️', queue.estimatedWaitMinutes + ' min', 'Est. Wait'],['👨‍💼', queue.activeStaff, 'Active Staff']].map(([icon,val,label]) => (
            <div key={label} style={{background:'#f9fafb',borderRadius:'10px',padding:'16px',textAlign:'center'}}>
              <div style={{fontSize:'24px'}}>{icon}</div>
              <div style={{fontSize:'22px',fontWeight:700,color:'#4f46e5',marginTop:'4px'}}>{val}</div>
              <div style={{fontSize:'12px',color:'#6b7280'}}>{label}</div>
            </div>
          ))}
        </div>

        {queue.status === 'ACTIVE' && (
          <div>
            <div className="form-group">
              <label>Priority Type</label>
              <select value={priority} onChange={e => setPriority(e.target.value)}>
                <option value="NORMAL">Normal</option>
                <option value="VIP">⭐ VIP</option>
                <option value="EMERGENCY">🚨 Emergency</option>
              </select>
            </div>
            {error && <div className="error" style={{padding:'10px',background:'#fee2e2',borderRadius:'8px',marginBottom:'12px'}}>{error}</div>}
            {success && <div style={{color:'#065f46',padding:'10px',background:'#d1fae5',borderRadius:'8px',marginBottom:'12px'}}>{success}</div>}
            <button className="btn btn-primary" style={{width:'100%',padding:'14px',fontSize:'16px'}} onClick={handleJoin} disabled={joining}>
              {joining ? 'Joining...' : '🎫 Join Queue'}
            </button>
          </div>
        )}
        {queue.status !== 'ACTIVE' && (
          <div style={{textAlign:'center',color:'#6b7280',padding:'20px',background:'#f9fafb',borderRadius:'10px'}}>
            This queue is currently {queue.status.toLowerCase()}
          </div>
        )}
      </div>
    </div>
  );
}
