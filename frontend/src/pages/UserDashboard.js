import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { getMyTokens } from '../services/queueService';
import { getBusinesses, getBranches, getQueuesByBranch } from '../services/publicService';
import { useAuth } from '../context/AuthContext';
import { connectWebSocket, disconnectWebSocket } from '../websocket/wsClient';
import TokenCard from '../components/TokenCard';
import QueueCard from '../components/QueueCard';
import Notification from '../components/Notification';

export default function UserDashboard() {
  const [tab, setTab] = useState('status');
  const [tokens, setTokens] = useState([]);
  const [businesses, setBusinesses] = useState([]);
  const [branches, setBranches] = useState([]);
  const [queues, setQueues] = useState([]);
  const [selectedBiz, setSelectedBiz] = useState(null);
  const [selectedBranch, setSelectedBranch] = useState(null);
  const [loading, setLoading] = useState(true);
  const [queuesLoading, setQueuesLoading] = useState(false);
  const [notification, setNotification] = useState(null);
  const { user } = useAuth();
  const navigate = useNavigate();

  const fetchTokens = useCallback(async () => {
    try {
      const res = await getMyTokens();
      setTokens(res.data.data || []);
    } catch (e) { console.error(e); }
    finally { setLoading(false); }
  }, []);

  const fetchBusinesses = useCallback(async () => {
    try {
      const res = await getBusinesses();
      setBusinesses(res.data.data || []);
    } catch (e) { console.error(e); }
  }, []);

  useEffect(() => {
    fetchTokens();
    fetchBusinesses();
    connectWebSocket(user.userId, fetchTokens, (n) => setNotification(n.message));
    return () => disconnectWebSocket();
  }, [user.userId, fetchTokens, fetchBusinesses]);

  const handleSelectBiz = async (biz) => {
    setSelectedBiz(biz);
    setSelectedBranch(null);
    setQueues([]);
    try {
      const res = await getBranches(biz.id);
      setBranches(res.data.data || []);
    } catch (e) { console.error(e); }
  };

  const handleSelectBranch = async (branch) => {
    setSelectedBranch(branch);
    setQueuesLoading(true);
    try {
      const res = await getQueuesByBranch(branch.id);
      setQueues(res.data.data || []);
    } catch (e) { console.error(e); }
    finally { setQueuesLoading(false); }
  };

  const activeToken = tokens.find(t => t.status === 'WAITING' || t.status === 'CALLED');
  const historyTokens = tokens.filter(t => t.status !== 'WAITING' && t.status !== 'CALLED');

  return (
    <div className="container">
      {notification && <Notification message={notification} onClose={() => setNotification(null)} />}

      <h2 style={{ fontSize: '22px', fontWeight: 700, marginBottom: '20px' }}>
        Welcome, {user.username} 👋
      </h2>

      <div className="tabs">
        {[['status', '🎫 My Status'], ['browse', '🔍 Browse Queues'], ['history', '📜 History']].map(([key, label]) => (
          <div key={key} className={`tab ${tab === key ? 'active' : ''}`} onClick={() => setTab(key)}>
            {label}
          </div>
        ))}
      </div>

      {/* ── MY STATUS TAB ── */}
      {tab === 'status' && (
        <>
          {loading ? (
            <div className="loading">Loading your tokens...</div>
          ) : activeToken ? (
            <div>
              <h3 style={{ marginBottom: '12px', color: '#374151', fontWeight: 600 }}>
                🟢 Active Token
              </h3>
              <TokenCard token={activeToken} />
              <div style={{ marginTop: '16px', textAlign: 'center' }}>
                <button
                  className="btn btn-secondary"
                  onClick={() => navigate(`/queue/${activeToken.queueId}`)}
                >
                  View Queue Details
                </button>
              </div>
            </div>
          ) : (
            <div className="card" style={{ textAlign: 'center', padding: '48px' }}>
              <div style={{ fontSize: '56px', marginBottom: '16px' }}>🎫</div>
              <h3 style={{ fontWeight: 600, marginBottom: '8px' }}>No active tokens</h3>
              <p style={{ color: '#6b7280', fontSize: '14px', marginBottom: '20px' }}>
                You're not in any queue right now
              </p>
              <button className="btn btn-primary" onClick={() => setTab('browse')}>
                Browse & Join a Queue →
              </button>
            </div>
          )}
        </>
      )}

      {/* ── BROWSE QUEUES TAB ── */}
      {tab === 'browse' && (
        <div>
          {/* Step 1 — Pick Business */}
          <div className="card" style={{ marginBottom: '16px' }}>
            <h3 style={{ fontWeight: 700, marginBottom: '12px', fontSize: '15px', color: '#374151' }}>
              Step 1 — Select a Business
            </h3>
            {businesses.length === 0 ? (
              <p style={{ color: '#6b7280', fontSize: '14px' }}>No businesses available yet.</p>
            ) : (
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '10px' }}>
                {businesses.map(b => (
                  <button
                    key={b.id}
                    onClick={() => handleSelectBiz(b)}
                    style={{
                      padding: '10px 20px',
                      borderRadius: '8px',
                      border: '2px solid',
                      borderColor: selectedBiz?.id === b.id ? '#4f46e5' : '#e5e7eb',
                      background: selectedBiz?.id === b.id ? '#ede9fe' : 'white',
                      color: selectedBiz?.id === b.id ? '#4f46e5' : '#374151',
                      fontWeight: selectedBiz?.id === b.id ? 700 : 400,
                      cursor: 'pointer',
                      fontSize: '14px',
                      transition: 'all 0.15s'
                    }}
                  >
                    🏢 {b.name}
                  </button>
                ))}
              </div>
            )}
          </div>

          {/* Step 2 — Pick Branch */}
          {selectedBiz && (
            <div className="card" style={{ marginBottom: '16px' }}>
              <h3 style={{ fontWeight: 700, marginBottom: '12px', fontSize: '15px', color: '#374151' }}>
                Step 2 — Select a Branch
                <span style={{ color: '#4f46e5', fontWeight: 400, fontSize: '13px', marginLeft: '8px' }}>
                  {selectedBiz.name}
                </span>
              </h3>
              {branches.length === 0 ? (
                <p style={{ color: '#6b7280', fontSize: '14px' }}>No branches found.</p>
              ) : (
                <div style={{ display: 'flex', flexWrap: 'wrap', gap: '10px' }}>
                  {branches.map(b => (
                    <button
                      key={b.id}
                      onClick={() => handleSelectBranch(b)}
                      style={{
                        padding: '10px 20px',
                        borderRadius: '8px',
                        border: '2px solid',
                        borderColor: selectedBranch?.id === b.id ? '#10b981' : '#e5e7eb',
                        background: selectedBranch?.id === b.id ? '#d1fae5' : 'white',
                        color: selectedBranch?.id === b.id ? '#065f46' : '#374151',
                        fontWeight: selectedBranch?.id === b.id ? 700 : 400,
                        cursor: 'pointer',
                        fontSize: '14px',
                        transition: 'all 0.15s'
                      }}
                    >
                      📍 {b.name} {b.city ? `— ${b.city}` : ''}
                    </button>
                  ))}
                </div>
              )}
            </div>
          )}

          {/* Step 3 — Pick Queue */}
          {selectedBranch && (
            <div>
              <h3 style={{ fontWeight: 700, marginBottom: '12px', fontSize: '15px', color: '#374151' }}>
                Step 3 — Choose a Queue
                <span style={{ color: '#10b981', fontWeight: 400, fontSize: '13px', marginLeft: '8px' }}>
                  {selectedBranch.name}
                </span>
              </h3>
              {queuesLoading ? (
                <div className="loading">Loading queues...</div>
              ) : queues.length === 0 ? (
                <div className="card" style={{ textAlign: 'center', padding: '32px', color: '#6b7280' }}>
                  No active queues at this branch right now.
                </div>
              ) : (
                <div className="grid-2">
                  {queues.map(q => (
                    <QueueCard key={q.id} queue={q} />
                  ))}
                </div>
              )}
            </div>
          )}

          {!selectedBiz && businesses.length > 0 && (
            <div style={{ textAlign: 'center', padding: '32px', color: '#9ca3af', fontSize: '14px' }}>
              ☝️ Select a business above to see its branches and queues
            </div>
          )}
        </div>
      )}

      {/* ── HISTORY TAB ── */}
      {tab === 'history' && (
        <>
          {loading ? (
            <div className="loading">Loading history...</div>
          ) : historyTokens.length === 0 ? (
            <div className="card" style={{ textAlign: 'center', padding: '40px', color: '#6b7280' }}>
              <div style={{ fontSize: '40px', marginBottom: '12px' }}>📭</div>
              <p>No history yet</p>
            </div>
          ) : (
            <div style={{ background: 'white', borderRadius: '12px', overflow: 'hidden', boxShadow: '0 2px 8px rgba(0,0,0,0.06)' }}>
              <table className="table">
                <thead>
                  <tr>
                    <th>Token</th>
                    <th>Queue</th>
                    <th>Priority</th>
                    <th>Status</th>
                    <th>Date</th>
                  </tr>
                </thead>
                <tbody>
                  {historyTokens.map(t => (
                    <tr key={t.id}>
                      <td style={{ fontWeight: 600 }}>{t.tokenNumber}</td>
                      <td>{t.queueName}</td>
                      <td>
                        <span className={`badge ${t.priorityType === 'EMERGENCY' ? 'badge-red' : t.priorityType === 'VIP' ? 'badge-purple' : 'badge-blue'}`}>
                          {t.priorityType}
                        </span>
                      </td>
                      <td>
                        <span className={`badge ${t.status === 'SERVED' ? 'badge-green' : t.status === 'NO_SHOW' ? 'badge-red' : 'badge-yellow'}`}>
                          {t.status}
                        </span>
                      </td>
                      <td style={{ color: '#6b7280', fontSize: '13px' }}>
                        {t.joinedAt ? new Date(t.joinedAt).toLocaleDateString() : '—'}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </>
      )}
    </div>
  );
}
