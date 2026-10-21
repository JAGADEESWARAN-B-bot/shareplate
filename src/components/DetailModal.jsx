import React from 'react';
import { X, MessageSquare, Clock, MapPin, Users, AlertTriangle, ShieldCheck, CheckCircle } from 'lucide-react';
import AiDisclaimer from './AiDisclaimer';

export default function DetailModal({ donation, onClose, onUpdateStatus }) {
  if (!donation) return null;

  const openWhatsApp = () => {
    const phone = (donation.contactPhone || '').replace(/[^0-9+]/g, '');
    const message = encodeURIComponent(
      `Hello! I saw your surplus food donation listing on Community Food Sharing Platform: "${donation.foodName}" (ID: ${donation.id}). Is it still available for pickup?`
    );
    window.open(`https://api.whatsapp.com/send?phone=${phone}&text=${message}`, '_blank');
  };

  return (
    <div style={{
      position: 'fixed',
      inset: 0,
      backgroundColor: 'rgba(0,0,0,0.65)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      zIndex: 1100,
      padding: '1rem'
    }}>
      <div className="card" style={{ maxWidth: '640px', width: '100%', maxHeight: '90vh', overflowY: 'auto', padding: '0', position: 'relative' }}>
        
        {/* Close Button */}
        <button
          onClick={onClose}
          style={{
            position: 'absolute',
            top: '12px',
            right: '12px',
            backgroundColor: 'rgba(0,0,0,0.5)',
            color: 'white',
            borderRadius: '50%',
            width: '32px',
            height: '32px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            zIndex: 10
          }}
        >
          <X size={18} />
        </button>

        {/* Hero Image */}
        {donation.imageUrl && (
          <div style={{ width: '100%', height: '220px', backgroundColor: '#E2EBE4', position: 'relative' }}>
            <img 
              src={donation.imageUrl} 
              alt={donation.foodName} 
              style={{ width: '100%', height: '100%', objectFit: 'cover' }} 
            />
            <div style={{ position: 'absolute', bottom: '12px', left: '12px', display: 'flex', gap: '0.5rem' }}>
              <span className={`badge badge-${donation.status.toLowerCase().replace(' ', '-')}`}>
                {donation.status}
              </span>
              <span style={{ backgroundColor: 'rgba(23,63,53,0.85)', color: 'white', padding: '0.2rem 0.5rem', borderRadius: '4px', fontSize: '0.75rem', fontWeight: 700 }}>
                {donation.category}
              </span>
            </div>
          </div>
        )}

        <div style={{ padding: '1.5rem' }}>
          <h2 style={{ fontSize: '1.5rem', fontWeight: 800, color: 'var(--color-deep-forest)', marginBottom: '0.5rem' }}>
            {donation.foodName}
          </h2>

          <p style={{ fontSize: '0.95rem', color: 'var(--color-charcoal)', opacity: 0.85, lineHeight: 1.5, marginBottom: '1.25rem' }}>
            {donation.description}
          </p>

          {/* Allergens Box */}
          {donation.allergenInfo && donation.allergenInfo !== 'None' && (
            <div style={{
              backgroundColor: 'var(--color-amber-bg)',
              border: '1px solid rgba(217,119,6,0.3)',
              borderRadius: '0.5rem',
              padding: '0.75rem 1rem',
              display: 'flex',
              alignItems: 'center',
              gap: '0.5rem',
              color: 'var(--color-amber)',
              marginBottom: '1rem',
              fontSize: '0.85rem'
            }}>
              <AlertTriangle size={18} style={{ flexShrink: 0 }} />
              <div>
                <strong>Allergen Notice:</strong> Contains {donation.allergenInfo}
              </div>
            </div>
          )}

          {/* Specifications Table */}
          <div style={{
            backgroundColor: 'var(--color-warm-ivory)',
            borderRadius: '0.75rem',
            padding: '1rem',
            border: '1px solid var(--color-border)',
            display: 'grid',
            gridTemplateColumns: '1fr 1fr',
            gap: '0.85rem',
            fontSize: '0.85rem',
            marginBottom: '1.25rem'
          }}>
            <div>
              <div style={{ color: 'var(--color-charcoal)', opacity: 0.7 }}>Quantity & Servings</div>
              <div style={{ fontWeight: 700, color: 'var(--color-deep-forest)' }}>{donation.quantity} {donation.unit} (~{donation.servings} people)</div>
            </div>
            <div>
              <div style={{ color: 'var(--color-charcoal)', opacity: 0.7 }}>Temperature Storage</div>
              <div style={{ fontWeight: 700, color: 'var(--color-deep-forest)' }}>{donation.storageMethod}</div>
            </div>
            <div>
              <div style={{ color: 'var(--color-charcoal)', opacity: 0.7 }}>Packaging Condition</div>
              <div style={{ fontWeight: 700, color: 'var(--color-deep-forest)' }}>{donation.packagingCondition}</div>
            </div>
            <div>
              <div style={{ color: 'var(--color-charcoal)', opacity: 0.7 }}>Collection Deadline</div>
              <div style={{ fontWeight: 700, color: 'var(--color-deep-forest)' }}>{donation.deadline}</div>
            </div>
            <div>
              <div style={{ color: 'var(--color-charcoal)', opacity: 0.7 }}>Donor Entity</div>
              <div style={{ fontWeight: 700, color: 'var(--color-deep-forest)' }}>{donation.donorName} ({donation.donorType})</div>
            </div>
            <div>
              <div style={{ color: 'var(--color-charcoal)', opacity: 0.7 }}>Location</div>
              <div style={{ fontWeight: 700, color: 'var(--color-deep-forest)' }}>{donation.location}</div>
            </div>
          </div>

          {/* AI Screening Findings */}
          <div style={{
            border: '1px solid var(--color-border)',
            borderRadius: '0.75rem',
            padding: '1rem',
            marginBottom: '1rem'
          }}>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
              <span style={{ fontWeight: 700, fontSize: '0.9rem', color: 'var(--color-deep-forest)' }}>AI Quality Screening:</span>
              <span className={`badge ${donation.aiScreeningStatus === 'NO OBVIOUS VISUAL WARNING SIGNS' ? 'badge-available' : 'badge-pending'}`}>
                {donation.aiScreeningStatus}
              </span>
            </div>
            <p style={{ fontSize: '0.85rem', color: 'var(--color-charcoal)', opacity: 0.85, marginBottom: '0.5rem' }}>
              {donation.aiSummary || 'Surface appearance inspected for typical discoloration and damage.'}
            </p>
            <div style={{ fontSize: '0.8rem', color: 'var(--color-emerald)', fontWeight: 600 }}>
              {donation.aiNextSteps || 'Follow safe cold-chain protocol during transport.'}
            </div>
          </div>

          <AiDisclaimer />

          {/* Action Buttons */}
          <div style={{ display: 'flex', gap: '0.75rem', marginTop: '1.25rem' }}>
            <button
              className="btn-primary"
              onClick={openWhatsApp}
              style={{ flex: 1 }}
            >
              <MessageSquare size={16} />
              WhatsApp Donor ({donation.contactPhone})
            </button>

            {donation.status === 'Available' && (
              <button
                className="btn-secondary"
                onClick={() => {
                  onUpdateStatus(donation.id, 'Reserved');
                  onClose();
                }}
              >
                Reserve
              </button>
            )}
          </div>
        </div>

      </div>
    </div>
  );
}
