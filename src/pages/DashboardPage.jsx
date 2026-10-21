import React, { useState } from 'react';
import { Package, Handshake, Bell, User, PlusCircle, ExternalLink } from 'lucide-react';
import DonationCard from '../components/DonationCard';

export default function DashboardPage({ currentUser, donations, requests, setTab, onSelectDonation, onOpenUserSwitch }) {
  const [activeTab, setActiveTab] = useState('donations');

  const myDonations = donations.filter((d) => d.donorId === currentUser?.id || d.donorName === currentUser?.fullName);
  const myRequests = requests.filter((r) => r.recipientId === currentUser?.id || r.recipientName === currentUser?.fullName);

  const totalServings = myDonations.reduce((sum, d) => sum + (Number(d.servings) || 0), 0);

  return (
    <div style={{ padding: '2rem 0 4rem 0' }}>
      <div className="container">
        
        {/* Welcome Section */}
        <div style={{
          backgroundColor: 'var(--color-deep-forest)',
          color: 'white',
          borderRadius: '1rem',
          padding: '2rem',
          marginBottom: '2rem',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          flexWrap: 'wrap',
          gap: '1.5rem'
        }}>
          <div>
            <div style={{ fontSize: '0.85rem', color: 'var(--color-soft-mint)', marginBottom: '0.25rem' }}>
              Welcome back to Community Food Share,
            </div>
            <h1 style={{ fontSize: '2rem', fontWeight: 800, color: 'white', marginBottom: '0.5rem' }}>
              {currentUser?.fullName || 'Community Member'}
            </h1>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', fontSize: '0.85rem', color: 'var(--color-muted-sage)' }}>
              <span>{currentUser?.accountType}</span>
              <span>•</span>
              <span>{currentUser?.location}</span>
            </div>
          </div>

          <div style={{ display: 'flex', gap: '0.75rem' }}>
            <button
              className="btn-outline"
              onClick={onOpenUserSwitch}
              style={{ color: 'white', borderColor: 'rgba(255,255,255,0.6)', fontSize: '0.85rem', padding: '0.6rem 1rem' }}
            >
              Switch Profile
            </button>
            <button
              className="btn-primary"
              onClick={() => setTab('donate')}
              style={{ fontSize: '0.85rem', padding: '0.6rem 1.2rem' }}
            >
              <PlusCircle size={16} />
              New Donation
            </button>
          </div>
        </div>

        {/* Real Metrics Cards */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1.25rem', marginBottom: '2rem' }}>
          <div className="card" style={{ padding: '1.25rem' }}>
            <div style={{ fontSize: '0.8rem', color: 'var(--color-charcoal)', opacity: 0.75, fontWeight: 600 }}>MY ACTIVE DONATIONS</div>
            <div style={{ fontSize: '2rem', fontWeight: 800, color: 'var(--color-emerald)', marginTop: '0.25rem' }}>{myDonations.length}</div>
          </div>
          <div className="card" style={{ padding: '1.25rem' }}>
            <div style={{ fontSize: '0.8rem', color: 'var(--color-charcoal)', opacity: 0.75, fontWeight: 600 }}>MY FOOD REQUESTS</div>
            <div style={{ fontSize: '2rem', fontWeight: 800, color: 'var(--color-deep-forest)', marginTop: '0.25rem' }}>{myRequests.length}</div>
          </div>
          <div className="card" style={{ padding: '1.25rem' }}>
            <div style={{ fontSize: '0.8rem', color: 'var(--color-charcoal)', opacity: 0.75, fontWeight: 600 }}>ESTIMATED PEOPLE SERVED</div>
            <div style={{ fontSize: '2rem', fontWeight: 800, color: 'var(--color-emerald)', marginTop: '0.25rem' }}>{totalServings}</div>
          </div>
        </div>

        {/* Dashboard Tabs */}
        <div style={{ display: 'flex', gap: '0.5rem', borderBottom: '1px solid var(--color-border)', marginBottom: '1.5rem' }}>
          {[
            { id: 'donations', label: `My Donations (${myDonations.length})`, icon: Package },
            { id: 'requests', label: `My Requests (${myRequests.length})`, icon: Handshake },
            { id: 'profile', label: 'Profile Settings', icon: User }
          ].map((t) => {
            const active = activeTab === t.id;
            const Icon = t.icon;
            return (
              <button
                key={t.id}
                onClick={() => setActiveTab(t.id)}
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '0.4rem',
                  padding: '0.75rem 1.25rem',
                  borderBottom: active ? '3px solid var(--color-emerald)' : '3px solid transparent',
                  color: active ? 'var(--color-deep-forest)' : 'var(--color-charcoal)',
                  fontWeight: active ? 700 : 500,
                  background: 'transparent',
                  fontSize: '0.95rem'
                }}
              >
                <Icon size={16} style={{ color: active ? 'var(--color-emerald)' : 'inherit' }} />
                {t.label}
              </button>
            );
          })}
        </div>

        {/* Tab 1: My Donations */}
        {activeTab === 'donations' && (
          <div>
            {myDonations.length === 0 ? (
              <div className="card" style={{ padding: '3.5rem 2rem', textAlign: 'center' }}>
                <Package size={44} style={{ color: 'var(--color-muted-sage)', margin: '0 auto 1rem auto' }} />
                <h3 style={{ fontSize: '1.2rem', fontWeight: 700, marginBottom: '0.35rem' }}>No donations logged yet</h3>
                <p style={{ color: 'var(--color-charcoal)', opacity: 0.75, maxWidth: '420px', margin: '0 auto 1.5rem auto', fontSize: '0.9rem' }}>
                  Have surplus food from today's service or fresh pantry items? Share with your community in just 2 minutes.
                </p>
                <button className="btn-primary" onClick={() => setTab('donate')}>
                  <PlusCircle size={18} />
                  Create First Donation
                </button>
              </div>
            ) : (
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))', gap: '1.5rem' }}>
                {myDonations.map((d) => (
                  <DonationCard key={d.id} donation={d} onSelect={onSelectDonation} />
                ))}
              </div>
            )}
          </div>
        )}

        {/* Tab 2: My Requests */}
        {activeTab === 'requests' && (
          <div>
            {myRequests.length === 0 ? (
              <div className="card" style={{ padding: '3.5rem 2rem', textAlign: 'center' }}>
                <Handshake size={44} style={{ color: 'var(--color-muted-sage)', margin: '0 auto 1rem auto' }} />
                <h3 style={{ fontSize: '1.2rem', fontWeight: 700, marginBottom: '0.35rem' }}>No food requests posted</h3>
                <p style={{ color: 'var(--color-charcoal)', opacity: 0.75, maxWidth: '420px', margin: '0 auto 1.5rem auto', fontSize: '0.9rem' }}>
                  If your organization or household is experiencing food insecurity, post a request so local food partners can respond.
                </p>
                <button className="btn-primary" onClick={() => setTab('requests')}>
                  Post Food Request
                </button>
              </div>
            ) : (
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(340px, 1fr))', gap: '1.25rem' }}>
                {myRequests.map((r) => (
                  <div key={r.id} className="card" style={{ padding: '1.25rem' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
                      <span style={{ fontWeight: 700, color: 'var(--color-deep-forest)' }}>{r.category}</span>
                      <span className="badge badge-pending">{r.status}</span>
                    </div>
                    <div style={{ fontSize: '0.9rem', color: 'var(--color-charcoal)', marginBottom: '0.5rem' }}>
                      {r.quantity} ({r.servingsNeeded} servings needed)
                    </div>
                    <div style={{ fontSize: '0.8rem', color: 'var(--color-charcoal)', opacity: 0.7 }}>
                      Needed: {r.neededDateTime} at {r.location}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* Tab 3: Profile Settings */}
        {activeTab === 'profile' && (
          <div className="card" style={{ padding: '2rem', maxWidth: '600px' }}>
            <h3 style={{ fontSize: '1.25rem', fontWeight: 800, marginBottom: '1.25rem', color: 'var(--color-deep-forest)' }}>
              Account Profile
            </h3>
            
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.85rem', fontSize: '0.9rem' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', paddingBottom: '0.5rem', borderBottom: '1px solid var(--color-border)' }}>
                <span style={{ color: 'var(--color-charcoal)', opacity: 0.7 }}>Full Name</span>
                <strong style={{ color: 'var(--color-deep-forest)' }}>{currentUser?.fullName}</strong>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', paddingBottom: '0.5rem', borderBottom: '1px solid var(--color-border)' }}>
                <span style={{ color: 'var(--color-charcoal)', opacity: 0.7 }}>Email</span>
                <strong style={{ color: 'var(--color-deep-forest)' }}>{currentUser?.email}</strong>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', paddingBottom: '0.5rem', borderBottom: '1px solid var(--color-border)' }}>
                <span style={{ color: 'var(--color-charcoal)', opacity: 0.7 }}>Phone (WhatsApp)</span>
                <strong style={{ color: 'var(--color-deep-forest)' }}>{currentUser?.phone}</strong>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', paddingBottom: '0.5rem', borderBottom: '1px solid var(--color-border)' }}>
                <span style={{ color: 'var(--color-charcoal)', opacity: 0.7 }}>Account Type</span>
                <strong style={{ color: 'var(--color-emerald)' }}>{currentUser?.accountType}</strong>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', paddingBottom: '0.5rem', borderBottom: '1px solid var(--color-border)' }}>
                <span style={{ color: 'var(--color-charcoal)', opacity: 0.7 }}>Neighborhood</span>
                <strong style={{ color: 'var(--color-deep-forest)' }}>{currentUser?.location}</strong>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ color: 'var(--color-charcoal)', opacity: 0.7 }}>Platform Role</span>
                <strong style={{ color: currentUser?.isAdmin ? 'var(--color-amber)' : 'var(--color-charcoal)' }}>
                  {currentUser?.isAdmin ? 'Safety Administrator' : 'Standard Member'}
                </strong>
              </div>
            </div>
          </div>
        )}

      </div>
    </div>
  );
}
