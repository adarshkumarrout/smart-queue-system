import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function RegisterPage() {
  const [form, setForm] = useState({ username:'', email:'', password:'', fullName:'', phoneNumber:'' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { register } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(''); setLoading(true);
    try {
      await register(form);
      navigate('/');
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed');
    } finally { setLoading(false); }
  };

  const f = (field) => ({ value: form[field], onChange: e => setForm({...form, [field]: e.target.value}) });

  return (
    <div style={{minHeight:'calc(100vh - 60px)',display:'flex',alignItems:'center',justifyContent:'center',padding:'24px'}}>
      <div style={{background:'white',borderRadius:'16px',padding:'40px',width:'100%',maxWidth:'440px',boxShadow:'0 4px 24px rgba(0,0,0,0.1)'}}>
        <div style={{textAlign:'center',marginBottom:'28px'}}>
          <h1 style={{fontSize:'24px',fontWeight:700}}>Create Account</h1>
          <p style={{color:'#6b7280',marginTop:'4px',fontSize:'14px'}}>Join SmartQueue today</p>
        </div>
        <form onSubmit={handleSubmit}>
          {[['fullName','Full Name','text'],['username','Username','text'],['email','Email','email'],['phoneNumber','Phone (optional)','tel'],['password','Password','password']].map(([k,l,t]) => (
            <div className="form-group" key={k}>
              <label>{l}</label>
              <input type={t} placeholder={`Enter ${l.toLowerCase()}`} {...f(k)} required={k !== 'phoneNumber'} />
            </div>
          ))}
          {error && <div className="error" style={{marginBottom:'12px',padding:'10px',background:'#fee2e2',borderRadius:'8px'}}>{error}</div>}
          <button type="submit" className="btn btn-primary" style={{width:'100%',padding:'12px'}} disabled={loading}>
            {loading ? 'Creating account...' : 'Register'}
          </button>
        </form>
        <p style={{textAlign:'center',marginTop:'20px',fontSize:'14px',color:'#6b7280'}}>
          Already have an account? <Link to="/login" style={{color:'#4f46e5',fontWeight:600}}>Login</Link>
        </p>
      </div>
    </div>
  );
}
