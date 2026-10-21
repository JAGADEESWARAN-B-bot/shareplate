import React, { useState } from 'react';
import { Shield, CheckCircle, XCircle, Clock, Eye, AlertTriangle } from 'lucide-react';

export default function AdminPage({ donations, onUpdateStatus, onSelectDonation, currentUser }) {
  const [tab, setTab] = useState('pending');

  const pendingDonations = donations.filter((d) => d.status === 'Pending Review');
  const availableDonations = donations.filter((d) => d.status === 'Available');
  const completedDonations = donations.filter((d) => d.status === 'Completed');

  return (
    <div style={{ padding: '2rem 0 4rem 0' }}>
      <div className="container">
        
        {/* Header */}
        <div style={{
          backgroundColor: 'var(--color-deep-forest)',
          color: 'white',
          borderRadius: '1rem',
          padding: '2rem',
          marginBottom: '2rem'
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: 'var(--color-amber)', fontWeight: 700, fontSize: '0.85rem', marginBottom: '0.35rem' }}>
            <Shield size={18} /> COORDINATOR & SAFETY ADMIN CONSOLE
          </div>
          <h1 style={{ fontSize: '2rem', fontWeight: 800, color: 'white', marginBottom: '0.5rem' }}>
            Platform Safety & Review Portal
          </h1>
          <p style={{ color: 'var(--color-soft-mint)', opacity: 0.9, fontSize: '0.95rem' }}>
            Audit incoming food donations, review AI screening reports, and approve safe listings for public distribution.
          </p>

          {/* Quick Metrics */}
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(160px, 1fr))', gap: '1rem', marginTop: '1.5rem' }}>
            <div style={{ backgroundColor: 'rgba(255,255,255,0.1)', padding: '1rem', borderRadius: '0.5rem' }}>
              <div style={{ fontSize: '0.75rem', color: 'var(--color-muted-sage)' }}>PENDING REVIEW</div>
              <div style={{ fontSize: '1.75rem', fontWeight: 800, color: 'var(--color-amber)' }}>{pendingDonations.length}</div>
            </div>
            <div style={{ backgroundColor: 'rgba(255,255,255,0.1)', padding: '1rem', borderRadius: '0.5rem' }}>
              <div style={{ fontSize: '0.75rem', color: 'var(--color-muted-sage)' }}>ACTIVE LISTINGS</div>
              <div style={{ fontSize: '1.75rem', fontWeight: 800, color: 'var(--color-emerald-light)' }}>{availableDonations.length}</div>
            </div>
            <div style={{ backgroundColor: 'rgba(255,255,255,0.1)', padding: '1rem', borderRadius: '0.5rem' }}>
              <div style={{ fontSize: '0.75rem', color: 'var(--color-muted-sage)' }}>TOTAL EXCHANGES</div>
              <div style={{ fontSize: '1.75rem', fontWeight: 800, color: 'white' }}>{completedDonations.length + 12}</div>
            </div>
            <div style={{ backgroundColor: 'rgba(255,255,255,0.1)', padding: '1rem', borderRadius: '0.5rem' }}>
              <div style={{ fontSize: '0.75rem', color: 'var(--color-muted-sage)' }}>TOTAL SUBMISSIONS</div>
              <div style={{ fontSize: '1.75rem', fontWeight: 800, color: 'white' }}>{donations.length}</div>
            </div>
          </div>
        </div>

        {/* Tab Filter */}
        <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '1.5rem' }}>
          <button
            className={tab === 'pending' ? 'btn-secondary' : 'btn-outline'}
            onClick={() => setTab('pending')}
            style={{ padding: '0.5rem 1rem', fontSize: '0.85rem' }}
          >
            Pending Review ({pendingDonations.length})
          </button>
          <button
            className={tab === 'all' ? 'btn-secondary' : 'btn-outline'}
            onClick={() => setTab('all')}
            style={{ padding: '0.5rem 1rem', fontSize: '0.85rem' }}
          >
            All Listings ({donations.length})
          </button>
        </div>

        {/* Pending Review Table / Cards */}
        {tab === 'pending' && (
          <div>
            {pendingDonations.length === 0 ? (
              <div className="card" style={{ padding: '3rem', textAlign: 'center' }}>
                <CheckCircle size={44} style={{ color: 'var(--color-emerald)', margin: '0 auto 1rem auto' }} />
                <h3 style={{ fontSize: '1.25rem', fontWeight: 700, marginBottom: '0.35rem' }}>Zero Pending Reviews</h3>
                <p style={{ color: 'var(--color-charcoal)', opacity: 0.75, fontSize: '0.9rem' }}>
                  All incoming surplus food listings have been verified and approved.
                </p>
              </div>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
                {pendingDonations.map((d) => (
                  <div key={d.id} className="card" style={{ padding: '1.5rem', borderLeft: '5px solid var(--color-amber)' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '1rem' }}>
                      <div style={{ display: 'flex', gap: '1.25rem' }}>
                        {d.imageUrl && (
                          <img 
                            src={d.imageUrl} 
                            alt={d.foodName} 
                            style={{ width: '100px', height: '100px', borderRadius: '0.5rem', objectFit: 'cover' }} 
                          />
                        )}
                        <div>
                          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.25rem' }}>
                            <h3 style={{ fontSize: '1.2rem', fontWeight: 800, color: 'var(--color-deep-forest)' }}>{d.foodName}</h3>
                            <span style={{ fontSize: '0.75rem', padding: '0.15rem 0.5rem', borderRadius: '4px', backgroundColor: 'var(--color-warm-ivory)', border: '1px solid var(--color-border)', fontWeight: 600 }}>
                              {d.id}
                            </span>
                          </div>

                          <div style={{ fontSize: '0.85rem', color: 'var(--color-charcoal)', opacity: 0.85, marginBottom: '0.5rem' }}>
                            Donor: <strong>{d.donorName}</strong> ({d.donorType}) • {d.location}
                          </div>

                          <div style={{ fontSize: '0.85rem', color: 'var(--color-deep-forest)', fontWeight: 600 }}>
                            {d.quantity} {d.unit} (~{d.servings} people) • Storage: {d.storageMethod} ({d.packagingCondition})
                          </div>

                          {d.aiSummary && (
                            <div style={{ marginTop: '0.5rem', backgroundColor: 'var(--color-soft-mint)', padding: '0.5rem 0.75rem', borderRadius: '6px', fontSize: '0.8rem', color: 'var(--color-deep-forest)' }}>
                              <strong>AI Visual Screening:</strong> {d.aiSummary}
                            </div>
                          )}
                        </div>
                      </div>

                      <div style={{ display: 'flex', gap: '0.5rem' }}>
                        <button
                          className="btn-outline"
                          onClick={() => onSelectDonation(d)}
                          style={{ padding: '0.5rem 0.8rem', fontSize: '0.8rem' }}
                        >
                          <Eye size={14} /> Full Details
                        </button>

                        <button
                          className="btn-primary"
                          onClick={() => {
                            onUpdateStatus(d.id, 'Available');
                            alert(`Listing '${d.foodName}' approved!`);
                          }}
                          style={{ padding: '0.5rem 1rem', fontSize: '0.8rem' }}
                        >
                          <CheckCircle size={14} /> Approve & Publish
                        </button>

                        <button
                          onClick={() => {
                            onUpdateStatus(d.id, 'Rejected');
                            alert(`Listing '${d.foodName}' rejected.`);
                          }}
                          style={{
                            backgroundColor: 'var(--color-red-bg)',
                            color: 'var(--color-red)',
                            border: '1px solid rgba(220, 38, 38, 0.3)',
                            padding: '0.5rem 0.8rem',
                            borderRadius: '0.5rem',
                            fontSize: '0.8rem',
                            fontWeight: 600,
                            display: 'flex',
                            alignItems: 'center',
                            gap: '0.3rem'
                          }}
                        >
                          <XCircle size={14} /> Reject
                        </button>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* All Listings Table */}
        {tab === 'all' && (
          <div className="card" style={{ padding: '1rem', overflowX: 'auto' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: '0.875rem' }}>
              <thead>
                <tr style={{ borderBottom: '2px solid var(--color-border)', color: 'var(--color-deep-forest)' }}>
                  <th style={{ padding: '0.75rem' }}>ID & Title</th>
                  <th style={{ padding: '0.75rem' }}>Category</th>
                  <th style={{ padding: '0.75rem' }}>Donor</th>
                  <th style={{ padding: '0.75rem' }}>Status</th>
                  <th style={{ padding: '0.75rem' }}>Quick Actions</th>
                </tr>
              </thead>
              <tbody>
                {donations.map((d) => (
                  <tr key={d.id} style={{ borderBottom: '1px solid var(--color-border)' }}>
                    <td style={{ padding: '0.75rem' }}>
                      <div style={{ fontWeight: 700, color: 'var(--color-deep-forest)' }}>{d.foodName}</div>
                      <div style={{ fontSize: '0.75rem', color: 'var(--color-charcoal)', opacity: 0.6 }}>{d.id}</div>
                    </td>
                    <td style={{ padding: '0.75rem' }}>{d.category}</td>
                    <td style={{ padding: '0.75rem' }}>{d.donorName}</td>
                    <td style={{ padding: '0.75rem' }}>
                      <span className={`badge badge-${d.status.toLowerCase().replace(' ', '-')}`}>
                        {d.status}
                      </span>
                    </td>
                    <td style={{ padding: '0.75rem' }}>
                      <div style={{ display: 'flex', gap: '0.4rem' }}>
                        {d.status !== 'Available' && (
                          <button
                            onClick={() => onUpdateStatus(d.id, 'Available')}
                            style={{ background: 'var(--color-soft-mint)', color: 'var(--color-emerald)', padding: '0.25rem 0.5rem', borderRadius: '4px', fontSize: '0.75rem', fontWeight: 600 }}
                          >
                            Set Available
                          </button>
                        )}
                        {d.status !== 'Completed' && (
                          <button
                            onClick={() => onUpdateStatus(d.id, 'Completed')}
                            style={{ background: '#E0F2FE', color: '#0284C7', padding: '0.25rem 0.5rem', borderRadius: '4px', fontSize: '0.75rem', fontWeight: 600 }}
                          >
                            Mark Completed
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

      </div>
    </div>
  );
}
