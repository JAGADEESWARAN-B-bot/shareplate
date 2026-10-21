import React from 'react';
import { Leaf, Shield } from 'lucide-react';

export default function Footer({ setTab }) {
  return (
    <footer style={{ backgroundColor: 'var(--color-deep-forest)', color: 'white', padding: '3.5rem 0 2rem 0', borderTop: '1px solid rgba(255,255,255,0.1)' }}>
      <div className="container">
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '2.5rem', marginBottom: '2.5rem' }}>
          
          {/* Brand info */}
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem', marginBottom: '1rem' }}>
              <div style={{ width: '32px', height: '32px', borderRadius: '50%', backgroundColor: 'var(--color-emerald)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <Leaf size={18} />
              </div>
              <span style={{ fontWeight: 800, fontSize: '1.05rem', letterSpacing: '0.02em', color: 'white' }}>
                COMMUNITY FOOD SHARING PLATFORM
              </span>
            </div>
            <p style={{ fontSize: '0.85rem', color: 'var(--color-soft-mint)', opacity: 0.85, lineHeight: 1.6, maxWidth: '320px' }}>
              Empowering communities, food businesses, and volunteers to reduce hunger and minimize waste through verified, safe surplus food sharing.
            </p>
          </div>

          {/* Quick links */}
          <div>
            <h4 style={{ color: 'white', fontSize: '0.95rem', fontWeight: 700, marginBottom: '1rem' }}>Platform Navigation</h4>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem', fontSize: '0.85rem' }}>
              <button onClick={() => setTab('home')} style={{ background: 'transparent', color: 'var(--color-muted-sage)', textAlign: 'left' }}>Home</button>
              <button onClick={() => setTab('browse')} style={{ background: 'transparent', color: 'var(--color-muted-sage)', textAlign: 'left' }}>Find Surplus Food</button>
              <button onClick={() => setTab('donate')} style={{ background: 'transparent', color: 'var(--color-muted-sage)', textAlign: 'left' }}>Donate Food</button>
              <button onClick={() => setTab('requests')} style={{ background: 'transparent', color: 'var(--color-muted-sage)', textAlign: 'left' }}>Community Requests</button>
              <button onClick={() => setTab('dashboard')} style={{ background: 'transparent', color: 'var(--color-muted-sage)', textAlign: 'left' }}>Member Dashboard</button>
            </div>
          </div>

          {/* Safety & Protocol */}
          <div>
            <h4 style={{ color: 'white', fontSize: '0.95rem', fontWeight: 700, marginBottom: '1rem' }}>Food Safety Standards</h4>
            <p style={{ fontSize: '0.85rem', color: 'var(--color-soft-mint)', opacity: 0.85, lineHeight: 1.6 }}>
              All donors adhere to temperature control guidelines (4°C - 60°C Danger Zone prevention), clear allergen labeling, and AI-assisted visual quality screening before public listing.
            </p>
          </div>

        </div>

        <div style={{
          borderTop: '1px solid rgba(255,255,255,0.1)',
          paddingTop: '1.5rem',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          flexWrap: 'wrap',
          gap: '1rem',
          fontSize: '0.8rem',
          color: 'var(--color-muted-sage)'
        }}>
          <div>
            © {new Date().getFullYear()} COMMUNITY FOOD SHARING PLATFORM. All rights reserved.
          </div>
          <div>
            Share More • Waste Less • Care Together
          </div>
        </div>
      </div>
    </footer>
  );
}
