import React, { useState, useEffect, useCallback } from 'react';
import {
  getBusinesses, createBusiness,
  getBranches, createBranch,
  createQueue, getStaff, addStaff,
  getAnalytics, markNoShow
} from '../services/adminService';
import { getQueuesByBranch, serveNext, getQueueTokens } from '../services/queueService';
import { useAuth } from '../context/AuthContext';

// ── Shared style constant ─────────────────────────────
const sel = {
  width: '100%', padding: '10px 12px', border: '1px solid #d1d5db',
  borderRadius: 8, fontSize: 14, background: 'white', boxSizing: 'border-box'
};

// ── Helper components (MUST be outside AdminDashboard to prevent remount on every render) ──
const Label = ({ children }) => (
  <label style={{ display: 'block', marginBottom: 6, fontWeight: 600, fontSize: 13, color: '#374151' }}>
    {children}
  </label>
);

const Field = ({ children }) => (
  <div style={{ marginBottom: 14 }}>{children}</div>
);

const Input = ({ ...props }) => (
  <input
    style={{ width: '100%', padding: '10px 12px', border: '1px solid #d1d5db', borderRadius: 8, fontSize: 14, boxSizing: 'border-box' }}
    {...props}
  />
);

const CardTitle = ({ icon, text }) => (
  <div style={{ fontWeight: 700, fontSize: 15, marginBottom: 16, display: 'flex', alignItems: 'center', gap: 8 }}>
    <span>{icon}</span><span>{text}</span>
  </div>
);

export default function AdminDashboard() {
  const [tab, setTab] = useState('setup');
  const { user } = useAuth();

  // ── Master data ───────────────────────────────────────
  const [businesses, setBusinesses] = useState([]);
  const [allBranches, setAllBranches] = useState([]);

  // ── Setup tab state ───────────────────────────────────
  const [bizForm, setBizForm] = useState({ name: '', description: '', ownerEmail: '' });
  const [branchForm, setBranchForm] = useState({ name: '', address: '', city: '', businessId: '' });
  const [queueForm, setQueueForm] = useState({ name: '', description: '', maxCapacity: 100, avgServiceTimeMinutes: 5, branchId: '' });
  const [queueBizId, setQueueBizId] = useState('');
  const [queueBranches, setQueueBranches] = useState([]);

  // ── Live Queues tab state ─────────────────────────────
  const [liveBizId, setLiveBizId] = useState('');
  const [liveBranches, setLiveBranches] = useState([]);
  const [liveBranchId, setLiveBranchId] = useState('');
  const [queues, setQueues] = useState([]);
  const [selectedQueue, setSelectedQueue] = useState(null);
  const [tokens, setTokens] = useState([]);
  const [analytics, setAnalytics] = useState(null);

  // ── Staff tab state ───────────────────────────────────
  const [staffBranchId, setStaffBranchId] = useState('');
  const [staffList, setStaffList] = useState([]);
  const [staffUserId, setStaffUserId] = useState('');

  // ── Flash message ─────────────────────────────────────
  const [msg, setMsg] = useState({ text: '', error: false });

  const flash = (text, error = false) => {
    setMsg({ text, error });
    setTimeout(() => setMsg({ text: '', error: false }), 4000);
  };

  // ── Load all businesses + all their branches ──────────
  const loadAll = useCallback(async () => {
    try {
      const bizRes = await getBusinesses();

      console.log("RAW BUSINESS RESPONSE:", bizRes.data);

      let bizList = [];
      if (Array.isArray(bizRes.data)) {
        bizList = bizRes.data;
      } else if (Array.isArray(bizRes.data.data)) {
        bizList = bizRes.data.data;
      } else if (Array.isArray(bizRes.data.content)) {
        bizList = bizRes.data.content;
      }

      setBusinesses(bizList);

      const branchPromises = bizList.map(b =>
        getBranches(b.id)
          .then(r => {
            let branchList = [];
            if (Array.isArray(r.data)) {
              branchList = r.data;
            } else if (Array.isArray(r.data.data)) {
              branchList = r.data.data;
            } else if (Array.isArray(r.data.content)) {
              branchList = r.data.content;
            }
            return branchList.map(br => ({
              ...br,
              businessId: b.id,
              businessName: b.name
            }));
          })
          .catch(() => [])
      );

      const branchArrays = await Promise.all(branchPromises);
      setAllBranches(branchArrays.flat());

    } catch (e) {
      console.error(e);
      flash('Failed to load data — check your login token', true);
    }
  }, []);

  // ── Load on mount ─────────────────────────────────────
  useEffect(() => { loadAll(); }, [loadAll]);

  // ── Helper: get branches for a given business ID ──────
  const branchesForBiz = useCallback((bizId) => {
    return allBranches.filter(b => String(b.businessId) === String(bizId));
  }, [allBranches]);

  // ── Business handlers ─────────────────────────────────
  const handleCreateBiz = async () => {
    if (!bizForm.name.trim())       return flash('Business name is required', true);
    if (!bizForm.ownerEmail.trim()) return flash('Owner email is required', true);
    try {
      await createBusiness(bizForm);
      setBizForm({ name: '', description: '', ownerEmail: '' });
      await loadAll();
      flash('✅ Business created!');
    } catch (e) {
      flash(e.response?.data?.message || 'Failed to create business', true);
    }
  };

  // ── Branch handlers ───────────────────────────────────
  const handleCreateBranch = async () => {
    if (!branchForm.businessId) return flash('Select a business', true);
    if (!branchForm.name.trim()) return flash('Branch name is required', true);
    if (!branchForm.address.trim()) return flash('Address is required', true);
    try {
      await createBranch(branchForm);
      setBranchForm({ name: '', address: '', city: '', businessId: '' });
      await loadAll();
      flash('✅ Branch created!');
    } catch (e) {
      flash(e.response?.data?.message || 'Failed to create branch', true);
    }
  };

  // ── Queue handlers ────────────────────────────────────
  const handleQueueBizChange = (bizId) => {
    setQueueBizId(bizId);
    setQueueForm(f => ({ ...f, branchId: '' }));
    setQueueBranches(branchesForBiz(bizId));
  };

  const handleCreateQueue = async () => {
    if (!queueForm.branchId) return flash('Select a branch', true);
    if (!queueForm.name.trim()) return flash('Queue name is required', true);
    try {
      await createQueue(queueForm);
      setQueueForm({ name: '', description: '', maxCapacity: 100, avgServiceTimeMinutes: 5, branchId: '' });
      setQueueBizId('');
      setQueueBranches([]);
      flash('✅ Queue created!');
    } catch (e) {
      flash(e.response?.data?.message || 'Failed to create queue', true);
    }
  };

  // ── Live Queues handlers ──────────────────────────────
  const handleLiveBizChange = (bizId) => {
    setLiveBizId(bizId);
    setLiveBranchId('');
    setQueues([]);
    setSelectedQueue(null);
    setTokens([]);
    setAnalytics(null);
    setLiveBranches(branchesForBiz(bizId));
  };

  const handleLiveBranchChange = async (branchId) => {
    setLiveBranchId(branchId);
    setSelectedQueue(null);
    setTokens([]);
    setAnalytics(null);
    try {
      const r = await getQueuesByBranch(branchId);
      setQueues(r.data?.data || r.data || []);
    } catch (e) {
      flash('Failed to load queues', true);
    }
  };

  const handleSelectQueue = async (q) => {
    setSelectedQueue(q);
    try {
      const [tRes, aRes] = await Promise.all([
        getQueueTokens(q.id),
        getAnalytics(q.id)
      ]);
      setTokens(tRes.data?.data || tRes.data || []);
      setAnalytics(aRes.data?.data || aRes.data);
    } catch (e) {}
  };

  const handleServeNext = async (queueId) => {
    try {
      await serveNext(queueId);
      flash('✅ Token served!');
      const r = await getQueueTokens(queueId);
      setTokens(r.data || []);
      handleLiveBranchChange(liveBranchId);
    } catch (e) {
      flash(e.response?.data?.message || 'No tokens waiting', true);
    }
  };

  const handleMarkNoShow = async (tokenId) => {
    try {
      await markNoShow(tokenId);
      flash('Marked as no-show');
      if (selectedQueue) {
        const r = await getQueueTokens(selectedQueue.id);
        setTokens(r.data?.data || r.data || []);
      }
    } catch (e) {
      flash('Failed to mark no-show', true);
    }
  };

  // ── Staff handlers ────────────────────────────────────
  const handleStaffBranchChange = async (branchId) => {
    setStaffBranchId(branchId);
    try {
      const r = await getStaff(branchId);
      setStaffList(r.data?.data || r.data || []);
    } catch (e) {}
  };

  const handleAddStaff = async () => {
    if (!staffUserId.trim()) return flash('Enter a User ID', true);
    if (!staffBranchId)      return flash('Select a branch', true);
    try {
      await addStaff(staffUserId, staffBranchId);
      setStaffUserId('');
      flash('✅ Staff added!');
      handleStaffBranchChange(staffBranchId);
    } catch (e) {
      flash(e.response?.data?.message || 'Failed to add staff', true);
    }
  };

  // ── Render ────────────────────────────────────────────
  return (
    <div className="container">
      <h2 style={{ fontSize: 22, fontWeight: 700, marginBottom: 4 }}>Admin Dashboard</h2>
      <p style={{ color: '#6b7280', fontSize: 13, marginBottom: 20 }}>
        {businesses.length} business{businesses.length !== 1 ? 'es' : ''} &nbsp;·&nbsp;
        {allBranches.length} branch{allBranches.length !== 1 ? 'es' : ''}
      </p>

      {/* Flash */}
      {msg.text && (
        <div style={{
          background: msg.error ? '#fee2e2' : '#d1fae5',
          color: msg.error ? '#991b1b' : '#065f46',
          padding: '12px 16px', borderRadius: 8, marginBottom: 16,
          fontWeight: 500, fontSize: 14
        }}>
          {msg.text}
        </div>
      )}

      {/* Tabs */}
      <div className="tabs">
        {[['setup', '⚙️ Setup'], ['queues', '📋 Live Queues'], ['staff', '👥 Staff']].map(([k, l]) => (
          <div key={k} className={`tab ${tab === k ? 'active' : ''}`} onClick={() => setTab(k)}>{l}</div>
        ))}
      </div>

      {/* ══════════════ SETUP TAB ══════════════ */}
      {tab === 'setup' && (
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16 }}>

          {/* LEFT — Business + Branch */}
          <div>
            {/* Create Business */}
            <div className="card">
              <CardTitle icon="🏢" text="Create Business" />
              <Field>
                <Label>Business Name *</Label>
                <Input placeholder="e.g. City Hospital"
                  value={bizForm.name}
                  onChange={e => setBizForm({ ...bizForm, name: e.target.value })} />
              </Field>
              <Field>
                <Label>Description</Label>
                <Input placeholder="Short description"
                  value={bizForm.description}
                  onChange={e => setBizForm({ ...bizForm, description: e.target.value })} />
              </Field>
              <Field>
                <Label>Owner Email *</Label>
                <Input type="email" placeholder="owner@example.com"
                  value={bizForm.ownerEmail}
                  onChange={e => setBizForm({ ...bizForm, ownerEmail: e.target.value })} />
              </Field>
              <button className="btn btn-primary" style={{ width: '100%' }} onClick={handleCreateBiz}>
                + Create Business
              </button>

              {businesses.length > 0 && (
                <div style={{ marginTop: 16 }}>
                  <p style={{ fontSize: 11, fontWeight: 700, color: '#9ca3af', marginBottom: 8, letterSpacing: 1 }}>
                    EXISTING BUSINESSES
                  </p>
                  {businesses.map(b => (
                    <div key={b.id} style={{ padding: '8px 12px', background: '#f9fafb', borderRadius: 6, marginBottom: 4, fontSize: 13 }}>
                      🏢 <strong>{b.name}</strong>
                      <span style={{ color: '#9ca3af', marginLeft: 6, fontSize: 11 }}>ID: {b.id}</span>
                    </div>
                  ))}
                </div>
              )}
            </div>

            {/* Create Branch */}
            <div className="card">
              <CardTitle icon="📍" text="Create Branch" />
              <Field>
                <Label>Select Business *</Label>
                <select style={sel}
                  value={branchForm.businessId}
                  onChange={e => setBranchForm({ ...branchForm, businessId: e.target.value })}>
                  <option value="">-- Select a Business --</option>
                  {businesses.map(b => (
                    <option key={b.id} value={b.id}>{b.name}</option>
                  ))}
                </select>
              </Field>
              <Field>
                <Label>Branch Name *</Label>
                <Input placeholder="e.g. Main Branch"
                  value={branchForm.name}
                  onChange={e => setBranchForm({ ...branchForm, name: e.target.value })} />
              </Field>
              <Field>
                <Label>Address *</Label>
                <Input placeholder="123 Main Street"
                  value={branchForm.address}
                  onChange={e => setBranchForm({ ...branchForm, address: e.target.value })} />
              </Field>
              <Field>
                <Label>City</Label>
                <Input placeholder="Bengaluru"
                  value={branchForm.city}
                  onChange={e => setBranchForm({ ...branchForm, city: e.target.value })} />
              </Field>
              <button className="btn btn-primary" style={{ width: '100%' }} onClick={handleCreateBranch}>
                + Create Branch
              </button>

              {allBranches.length > 0 && (
                <div style={{ marginTop: 16 }}>
                  <p style={{ fontSize: 11, fontWeight: 700, color: '#9ca3af', marginBottom: 8, letterSpacing: 1 }}>
                    EXISTING BRANCHES
                  </p>
                  {allBranches.map(b => (
                    <div key={b.id} style={{ padding: '8px 12px', background: '#f9fafb', borderRadius: 6, marginBottom: 4, fontSize: 13 }}>
                      📍 <strong>{b.name}</strong>
                      <span style={{ color: '#9ca3af', marginLeft: 6, fontSize: 11 }}>{b.businessName} · ID: {b.id}</span>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>

          {/* RIGHT — Create Queue */}
          <div>
            <div className="card">
              <CardTitle icon="📋" text="Create Queue" />

              <Field>
                <Label>Step 1 — Select Business *</Label>
                <select style={sel} value={queueBizId} onChange={e => handleQueueBizChange(e.target.value)}>
                  <option value="">-- Select a Business --</option>
                  {businesses.map(b => (
                    <option key={b.id} value={b.id}>{b.name}</option>
                  ))}
                </select>
              </Field>

              <Field>
                <Label>Step 2 — Select Branch *</Label>
                <select
                  style={{ ...sel, opacity: queueBizId ? 1 : 0.5 }}
                  value={queueForm.branchId}
                  onChange={e => setQueueForm({ ...queueForm, branchId: e.target.value })}
                  disabled={!queueBizId}>
                  <option value="">
                    {queueBizId
                      ? queueBranches.length > 0
                        ? '-- Select a Branch --'
                        : '-- No branches yet, create one first --'
                      : '-- Select a Business first --'}
                  </option>
                  {queueBranches.map(b => (
                    <option key={b.id} value={b.id}>{b.name} — {b.city || b.address}</option>
                  ))}
                </select>
              </Field>

              <Field>
                <Label>Queue Name *</Label>
                <Input placeholder="e.g. General Consultation"
                  value={queueForm.name}
                  onChange={e => setQueueForm({ ...queueForm, name: e.target.value })} />
              </Field>
              <Field>
                <Label>Description</Label>
                <Input placeholder="Optional"
                  value={queueForm.description}
                  onChange={e => setQueueForm({ ...queueForm, description: e.target.value })} />
              </Field>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
                <Field>
                  <Label>Max Capacity</Label>
                  <Input type="number" value={queueForm.maxCapacity}
                    onChange={e => setQueueForm({ ...queueForm, maxCapacity: e.target.value })} />
                </Field>
                <Field>
                  <Label>Avg Service (min)</Label>
                  <Input type="number" value={queueForm.avgServiceTimeMinutes}
                    onChange={e => setQueueForm({ ...queueForm, avgServiceTimeMinutes: e.target.value })} />
                </Field>
              </div>
              <button
                className="btn btn-primary"
                style={{ width: '100%', opacity: queueForm.branchId ? 1 : 0.5 }}
                onClick={handleCreateQueue}
                disabled={!queueForm.branchId}>
                + Create Queue
              </button>
            </div>
          </div>
        </div>
      )}

      {/* ══════════════ LIVE QUEUES TAB ══════════════ */}
      {tab === 'queues' && (
        <div>
          <div className="card" style={{ marginBottom: 16 }}>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16 }}>
              <div>
                <Label>Select Business</Label>
                <select style={sel} value={liveBizId} onChange={e => handleLiveBizChange(e.target.value)}>
                  <option value="">-- Select Business --</option>
                  {businesses.map(b => <option key={b.id} value={b.id}>{b.name}</option>)}
                </select>
              </div>
              <div>
                <Label>Select Branch</Label>
                <select
                  style={{ ...sel, opacity: liveBizId ? 1 : 0.5 }}
                  value={liveBranchId}
                  onChange={e => handleLiveBranchChange(e.target.value)}
                  disabled={!liveBizId}>
                  <option value="">{liveBizId ? '-- Select Branch --' : '-- Select Business first --'}</option>
                  {liveBranches.map(b => <option key={b.id} value={b.id}>{b.name}</option>)}
                </select>
              </div>
            </div>
          </div>

          {liveBranchId && queues.length === 0 && (
            <div className="card" style={{ textAlign: 'center', color: '#6b7280', padding: 40 }}>
              No queues found. Go to Setup tab to create one.
            </div>
          )}

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16 }}>
            <div>
              {queues.map(q => (
                <div key={q.id} className="card"
                  style={{ cursor: 'pointer', border: `2px solid ${selectedQueue?.id === q.id ? '#4f46e5' : '#e5e7eb'}`, marginBottom: 12 }}
                  onClick={() => handleSelectQueue(q)}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <div>
                      <strong>{q.name}</strong>
                      <div style={{ fontSize: 13, color: '#6b7280', marginTop: 2 }}>
                        {q.waitingCount} waiting · ~{q.estimatedWaitMinutes} min
                      </div>
                    </div>
                    <span className={`badge ${q.status === 'ACTIVE' ? 'badge-green' : 'badge-yellow'}`}>{q.status}</span>
                  </div>
                  <button className="btn btn-success" style={{ width: '100%', marginTop: 12 }}
                    onClick={e => { e.stopPropagation(); handleServeNext(q.id); }}>
                    ▶ Serve Next
                  </button>
                </div>
              ))}
            </div>

            <div>
              {analytics && selectedQueue && (
                <div className="card" style={{ marginBottom: 12 }}>
                  <p style={{ fontWeight: 700, marginBottom: 12 }}>📊 {selectedQueue.name}</p>
                  <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 8 }}>
                    {[['Served', analytics.totalServed, '#10b981'], ['No-Shows', analytics.totalNoShows, '#ef4444'],
                      ['Waiting', analytics.currentWaiting, '#f59e0b'], ['Avg Wait', analytics.avgWaitTime + ' min', '#4f46e5']
                    ].map(([l, v, c]) => (
                      <div key={l} style={{ background: '#f9fafb', borderRadius: 8, padding: 12, textAlign: 'center' }}>
                        <div style={{ fontSize: 22, fontWeight: 700, color: c }}>{v}</div>
                        <div style={{ fontSize: 11, color: '#6b7280' }}>{l}</div>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {tokens.length > 0 && (
                <div className="card">
                  <p style={{ fontWeight: 700, marginBottom: 12 }}>🎫 Waiting Tokens</p>
                  <table className="table">
                    <thead><tr><th>#</th><th>Token</th><th>Priority</th><th>Action</th></tr></thead>
                    <tbody>
                      {tokens.map((t, i) => (
                        <tr key={t.id}>
                          <td style={{ fontWeight: 600, color: '#4f46e5' }}>{i + 1}</td>
                          <td style={{ fontWeight: 600 }}>{t.tokenNumber}</td>
                          <td>
                            <span className={`badge ${t.priorityType === 'EMERGENCY' ? 'badge-red' : t.priorityType === 'VIP' ? 'badge-purple' : 'badge-blue'}`}>
                              {t.priorityType}
                            </span>
                          </td>
                          <td>
                            <button className="btn btn-danger" style={{ padding: '4px 10px', fontSize: 12 }}
                              onClick={() => handleMarkNoShow(t.id)}>No Show</button>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          </div>
        </div>
      )}

      {/* ══════════════ STAFF TAB ══════════════ */}
      {tab === 'staff' && (
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16 }}>
          <div className="card">
            <CardTitle icon="➕" text="Add Staff Member" />
            <Field>
              <Label>Select Branch *</Label>
              <select style={sel} value={staffBranchId} onChange={e => handleStaffBranchChange(e.target.value)}>
                <option value="">-- Select Branch --</option>
                {allBranches.map(b => (
                  <option key={b.id} value={b.id}>{b.name} ({b.businessName})</option>
                ))}
              </select>
            </Field>
            <Field>
              <Label>User ID *</Label>
              <Input type="number" placeholder="Enter User ID to promote to Staff"
                value={staffUserId} onChange={e => setStaffUserId(e.target.value)} />
              <p style={{ fontSize: 12, color: '#9ca3af', marginTop: 4 }}>
                Find IDs via MySQL: <code>SELECT id, username FROM users;</code>
              </p>
            </Field>
            <button className="btn btn-primary" style={{ width: '100%' }} onClick={handleAddStaff}>
              Add as Staff
            </button>
          </div>

          <div className="card">
            <CardTitle icon="👥" text="Staff Members" />
            {staffList.length === 0 ? (
              <div style={{ textAlign: 'center', color: '#9ca3af', padding: 32 }}>
                Select a branch to view staff
              </div>
            ) : (
              <table className="table">
                <thead><tr><th>Name</th><th>Status</th><th>Served</th></tr></thead>
                <tbody>
                  {staffList.map(s => (
                    <tr key={s.id}>
                      <td>
                        <strong>{s.fullName}</strong>
                        <div style={{ color: '#6b7280', fontSize: 12 }}>@{s.username}</div>
                      </td>
                      <td>
                        <span className={`badge ${s.status === 'AVAILABLE' ? 'badge-green' : s.status === 'BUSY' ? 'badge-yellow' : 'badge-red'}`}>
                          {s.status}
                        </span>
                      </td>
                      <td style={{ fontWeight: 600 }}>{s.tokensServedToday}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </div>
      )}
    </div>
  );
}