import React from 'react';
import { Clock, MapPin, Users, MessageSquare, CheckCircle, AlertTriangle, HelpCircle, Utensils } from 'lucide-react';

export default function DonationCard({ donation, onSelect }) {
  const getAiBadge = (status) => {
    switch (status) {
      case 'NO OBVIOUS VISUAL WARNING SIGNS':
        return (
          <span className="badge" style={{ backgroundColor: 'var(--color-soft-mint)', color: 'var(--color-emerald)' }}>
            <CheckCircle size={12} /> AI: Clean Visual
          </span>
        );
      case 'POTENTIAL QUALITY CONCERN':
        return (
          <span className="badge" style={{ backgroundColor: 'var(--color-amber-bg)', color: 'var(--color-amber)' }}>
            <AlertTriangle size={12} /> AI: Quality Note
          </span>
        );
      case 'UNABLE TO ASSESS':
      default:
        return (
          <span className="badge" style={{ backgroundColor: '#F3F4F6', color: '#4B5563' }}>
            <HelpCircle size={12} /> AI: Unassessed
          </span>
        );
    }
  };

  const openWhatsApp = (e) => {
    e.stopPropagation();
    const phone = (donation.contactPhone || '').replace(/[^0-9+]/g, '');
    const message = encodeURIComponent(
      `Hello! I saw your surplus food donation listing on Community Food Sharing Platform: "${donation.foodName}" (ID: ${donation.id}). Is it still available for pickup?`
    );
    window.open(`https://api.whatsapp.com/send?phone=${phone}&text=${message}`, '_blank');
  };

  return (
    <div className="card" onClick={() => onSelect(donation)} style={{ cursor: 'pointer', display: 'flex', flexDirection: 'column' }}>
      {/* Image Container with Badges */}
      <div style={{ position: 'relative', width: '100%', height: '180px', backgroundColor: '#E2EBE4', overflow: 'hidden' }}>
        {donation.imageUrl ? (
          <img 
            src={donation.imageUrl} 
            alt={donation.foodName}
            style={{ width: '100%', height: '100%', objectFit: 'cover' }}
            onError={(e) => {
              e.target.style.display = 'none';
            }}
          />
        ) : (
          <div style={{ width: '100%', height: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--color-muted-sage)' }}>
            <Utensils size={48} />
          </div>
        )}

        {/* Category Badge */}
        <div style={{ position: 'absolute', top: '12px', left: '12px' }}>
          <span style={{
            backgroundColor: 'rgba(23, 63, 53, 0.88)',
            color: 'white',
            padding: '0.25rem 0.6rem',
            borderRadius: '6px',
            fontSize: '0.75rem',
            fontWeight: 700
          }}>
            {donation.category}
          </span>
        </div>

        {/* Status Badge */}
        <div style={{ position: 'absolute', top: '12px', right: '12px' }}>
          <span className={`badge badge-${donation.status.toLowerCase().replace(' ', '-')}`}>
            {donation.status}
          </span>
        </div>

        {/* Free / Fixed Price Tag */}
        <div style={{
          position: 'absolute',
          bottom: '0',
          right: '0',
          backgroundColor: donation.pricingType === 'Free Donation' ? 'var(--color-emerald)' : 'var(--color-deep-forest)',
          color: 'white',
          padding: '0.25rem 0.75rem',
          borderTopLeftRadius: '8px',
          fontWeight: 700,
          fontSize: '0.8rem'
        }}>
          {donation.pricingType === 'Free Donation' ? 'FREE' : `$${Number(donation.priceAmount).toFixed(2)}`}
        </div>
      </div>

      {/* Card Content */}
      <div style={{ padding: '1.1rem', flex: 1, display: 'flex', flexDirection: 'column' }}>
        <h3 style={{ fontSize: '1.1rem', fontWeight: 700, marginBottom: '0.35rem', color: 'var(--color-deep-forest)' }}>
          {donation.foodName}
        </h3>
        
        <p style={{
          fontSize: '0.85rem',
          color: 'var(--color-charcoal)',
          opacity: 0.85,
          marginBottom: '0.85rem',
          display: '-webkit-box',
          WebkitLineClamp: 2,
          WebkitBoxOrient: 'vertical',
          overflow: 'hidden'
        }}>
          {donation.description}
        </p>

        {/* Info Rows */}
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', fontSize: '0.825rem', marginBottom: '0.5rem' }}>
          <span style={{ display: 'flex', alignItems: 'center', gap: '0.3rem', fontWeight: 600, color: 'var(--color-deep-forest)' }}>
            <Users size={15} style={{ color: 'var(--color-emerald)' }} />
            {donation.quantity} {donation.unit} (~{donation.servings} servings)
          </span>
          <span style={{ display: 'flex', alignItems: 'center', gap: '0.3rem', color: 'var(--color-charcoal)', opacity: 0.75 }}>
            <Clock size={14} />
            {donation.deadline}
          </span>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '0.35rem', fontSize: '0.8rem', color: 'var(--color-charcoal)', opacity: 0.8, marginBottom: '0.75rem' }}>
          <MapPin size={14} style={{ color: 'var(--color-emerald)', flexShrink: 0 }} />
          <span style={{ whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
            {donation.location} • {donation.donorName}
          </span>
        </div>

        {/* AI Screening status & Allergens */}
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: '0.5rem', marginBottom: '1rem', marginTop: 'auto' }}>
          {getAiBadge(donation.aiScreeningStatus)}

          {donation.allergenInfo && donation.allergenInfo !== 'None' && (
            <span style={{ fontSize: '0.7rem', color: 'var(--color-amber)', backgroundColor: 'var(--color-amber-bg)', padding: '0.15rem 0.45rem', borderRadius: '4px', fontWeight: 600 }}>
              Allergens: {donation.allergenInfo.slice(0, 18)}...
            </span>
          )}
        </div>

        {/* Actions */}
        <div style={{ display: 'flex', gap: '0.5rem' }}>
          <button 
            className="btn-outline" 
            style={{ flex: 1, padding: '0.5rem', fontSize: '0.85rem' }}
            onClick={(e) => {
              e.stopPropagation();
              onSelect(donation);
            }}
          >
            View Details
          </button>
          
          <button 
            className="btn-primary" 
            style={{ flex: 1, padding: '0.5rem', fontSize: '0.85rem' }}
            onClick={openWhatsApp}
          >
            <MessageSquare size={15} />
            WhatsApp
          </button>
        </div>
      </div>
    </div>
  );
}
