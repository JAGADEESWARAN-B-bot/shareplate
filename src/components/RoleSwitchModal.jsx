import React, { useState } from 'react';
import { X, CheckCircle, Shield, User, Store } from 'lucide-react';

export default function RoleSwitchModal({ currentUser, allUsers, onSelectUser, onAddUser, onClose }) {
  const [isRegister, setIsRegister] = useState(false);
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [phone, setPhone] = useState('');
  const [accountType, setAccountType] = useState('Individual');
  const [location, setLocation] = useState('');

  const handleRegister = (e) => {
    e.preventDefault();
    if (!fullName.trim() || !email.trim()) return;
    const newUser = {
      id: 'usr_' + Math.random().toString(36).substring(2, 8),
      fullName,
      email,
      phone: phone || '+1 (555) 000-1122',
      accountType,
      location: location || 'Central District',
      isAdmin: false
    };
    onAddUser(newUser);
    onSelectUser(newUser);
    onClose();
  };

  return (
    <div style={{
      position: 'fixed',
      inset: 0,
      backgroundColor: 'rgba(0,0,0,0.6)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      zIndex: 1200,
      padding: '1rem'
    }}>
      <div className="card" style={{ maxWidth: '480px', width: '100%', padding: '1.75rem', position: 'relative' }}>
        <button
          onClick={onClose}
          style={{ position: 'absolute', top: '16px', right: '16px', background: 'transparent', color: 'var(--color-charcoal)', opacity: 0.7 }}
        >
          <X size={20} />
        </button>

        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '1.25rem', paddingRight: '2rem' }}>
          <div>
            <h2 style={{ fontSize: '1.35rem', fontWeight: 800, color: 'var(--color-deep-forest)' }}>
              {isRegister ? 'Register New Member' : 'Switch Active Profile'}
            </h2>
            <p style={{ fontSize: '0.85rem', color: 'var(--color-charcoal)', opacity: 0.75 }}>
              {isRegister ? 'Create a local donor or recipient profile' : 'Test the platform under different community roles'}
            </p>
          </div>
        </div>

        {!isRegister ? (
          <div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem', marginBottom: '1.25rem' }}>
              {allUsers.map((u) => {
                const isSelected = u.id === currentUser?.id;
                return (
                  <div
                    key={u.id}
                    onClick={() => {
                      onSelectUser(u);
                      onClose();
                    }}
                    style={{
                      border: '1.5px solid',
                      borderColor: isSelected ? 'var(--color-emerald)' : 'var(--color-border)',
                      backgroundColor: isSelected ? 'var(--color-soft-mint)' : 'var(--color-warm-ivory)',
                      borderRadius: '0.75rem',
                      padding: '0.85rem 1rem',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'space-between',
                      cursor: 'pointer'
                    }}
                  >
                    <div>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                        <strong style={{ color: 'var(--color-deep-forest)' }}>{u.fullName}</strong>
                        {u.isAdmin && (
                          <span style={{ fontSize: '0.7rem', padding: '0.1rem 0.4rem', backgroundColor: 'var(--color-amber-bg)', color: 'var(--color-amber)', borderRadius: '4px', fontWeight: 700 }}>
                            ADMIN
                          </span>
                        )}
                      </div>
                      <div style={{ fontSize: '0.8rem', color: 'var(--color-charcoal)', opacity: 0.75 }}>
                        {u.accountType} • {u.location}
                      </div>
                    </div>
                    {isSelected && <CheckCircle size={18} style={{ color: 'var(--color-emerald)' }} />}
                  </div>
                );
              })}
            </div>

            <button
              className="btn-outline"
              onClick={() => setIsRegister(true)}
              style={{ width: '100%', fontSize: '0.875rem' }}
            >
              + Create New Profile
            </button>
          </div>
        ) : (
          <form onSubmit={handleRegister} style={{ display: 'flex', flexDirection: 'column', gap: '0.85rem' }}>
            <div>
              <label style={{ display: 'block', fontSize: '0.8rem', fontWeight: 600, marginBottom: '0.2rem' }}>Full Name / Business Name *</label>
              <input type="text" value={fullName} onChange={(e) => setFullName(e.target.value)} style={{ width: '100%', padding: '0.6rem', borderRadius: '0.5rem', border: '1px solid var(--color-border)' }} required />
            </div>

            <div>
              <label style={{ display: 'block', fontSize: '0.8rem', fontWeight: 600, marginBottom: '0.2rem' }}>Email *</label>
              <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} style={{ width: '100%', padding: '0.6rem', borderRadius: '0.5rem', border: '1px solid var(--color-border)' }} required />
            </div>

            <div>
              <label style={{ display: 'block', fontSize: '0.8rem', fontWeight: 600, marginBottom: '0.2rem' }}>WhatsApp Phone</label>
              <input type="text" value={phone} onChange={(e) => setPhone(e.target.value)} placeholder="+1 (555) 000-0000" style={{ width: '100%', padding: '0.6rem', borderRadius: '0.5rem', border: '1px solid var(--color-border)' }} />
            </div>

            <div>
              <label style={{ display: 'block', fontSize: '0.8rem', fontWeight: 600, marginBottom: '0.2rem' }}>Account Type</label>
              <select value={accountType} onChange={(e) => setAccountType(e.target.value)} style={{ width: '100%', padding: '0.6rem', borderRadius: '0.5rem', border: '1px solid var(--color-border)' }}>
                <option value="Individual">Individual Donor/Recipient</option>
                <option value="Restaurant">Restaurant / Caterer</option>
                <option value="NGO/Charity">NGO / Charity</option>
                <option value="Community Organization">Community Organization / Pantry</option>
              </select>
            </div>

            <div>
              <label style={{ display: 'block', fontSize: '0.8rem', fontWeight: 600, marginBottom: '0.2rem' }}>Neighborhood / Location</label>
              <input type="text" value={location} onChange={(e) => setLocation(e.target.value)} placeholder="e.g. Westside Market" style={{ width: '100%', padding: '0.6rem', borderRadius: '0.5rem', border: '1px solid var(--color-border)' }} />
            </div>

            <div style={{ display: 'flex', gap: '0.5rem', marginTop: '0.5rem' }}>
              <button type="button" className="btn-outline" onClick={() => setIsRegister(false)} style={{ flex: 1 }}>Back</button>
              <button type="submit" className="btn-primary" style={{ flex: 1 }}>Register & Switch</button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
}
