import React from 'react';
import { Leaf, Search, PlusCircle, Handshake, LayoutDashboard, Shield, UserCircle2, ArrowRight } from 'lucide-react';

export default function Navbar({ currentTab, setTab, currentUser, onOpenUserSwitch }) {
  return (
    <header style={{ backgroundColor: 'var(--color-deep-forest)', borderBottom: '1px solid rgba(255,255,255,0.1)' }}>
      <div className="container" style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', height: '70px' }}>
        
        {/* Brand Logo */}
        <div 
          onClick={() => setTab('home')} 
          style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', cursor: 'pointer' }}
        >
          <div style={{
            width: '40px',
            height: '40px',
            borderRadius: '50%',
            backgroundColor: 'var(--color-emerald)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: 'white'
          }}>
            <Leaf size={22} />
          </div>
          <div>
            <div style={{
              fontFamily: 'var(--font-heading)',
              fontWeight: 800,
              fontSize: '1.15rem',
              color: 'var(--color-white)',
              letterSpacing: '0.02em',
              lineHeight: 1.1
            }}>
              COMMUNITY FOOD SHARING PLATFORM
            </div>
            <div style={{ fontSize: '0.75rem', color: 'var(--color-muted-sage)' }}>
              Share More • Waste Less • Care Together
            </div>
          </div>
        </div>

        {/* Navigation Links */}
        <nav style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          {[
            { id: 'home', label: 'Home' },
            { id: 'browse', label: 'Find Food' },
            { id: 'donate', label: 'Donate' },
            { id: 'requests', label: 'Requests' },
            { id: 'dashboard', label: 'Dashboard' }
          ].map((item) => {
            const isActive = currentTab === item.id;
            return (
              <button
                key={item.id}
                onClick={() => setTab(item.id)}
                style={{
                  background: isActive ? 'var(--color-emerald)' : 'transparent',
                  color: isActive ? 'var(--color-white)' : 'var(--color-soft-mint)',
                  padding: '0.5rem 0.9rem',
                  borderRadius: '0.5rem',
                  fontWeight: isActive ? 600 : 500,
                  fontSize: '0.9rem',
                  transition: 'background 0.2s, color 0.2s'
                }}
              >
                {item.label}
              </button>
            );
          })}

          {currentUser?.isAdmin && (
            <button
              onClick={() => setTab('admin')}
              style={{
                background: currentTab === 'admin' ? 'var(--color-amber)' : 'rgba(217, 119, 6, 0.25)',
                color: 'white',
                border: '1px solid var(--color-amber)',
                padding: '0.4rem 0.75rem',
                borderRadius: '0.5rem',
                fontSize: '0.8rem',
                fontWeight: 700,
                display: 'flex',
                alignItems: 'center',
                gap: '0.3rem'
              }}
            >
              <Shield size={14} />
              ADMIN
            </button>
          )}

          {/* User profile / Switch role chip */}
          <div
            onClick={onOpenUserSwitch}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '0.45rem',
              padding: '0.4rem 0.8rem',
              borderRadius: '9999px',
              backgroundColor: 'rgba(33, 140, 101, 0.3)',
              border: '1px solid rgba(33, 140, 101, 0.5)',
              color: 'var(--color-warm-ivory)',
              fontSize: '0.85rem',
              fontWeight: 600,
              cursor: 'pointer',
              marginLeft: '0.5rem'
            }}
          >
            <UserCircle2 size={18} />
            <span>{currentUser?.fullName?.split(' ')[0] || 'User'}</span>
            <span style={{
              fontSize: '0.7rem',
              padding: '0.1rem 0.4rem',
              borderRadius: '4px',
              backgroundColor: 'var(--color-emerald)',
              color: 'white'
            }}>
              {currentUser?.accountType || 'Guest'}
            </span>
          </div>
        </nav>

      </div>
    </header>
  );
}
