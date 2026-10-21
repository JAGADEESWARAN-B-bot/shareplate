import React from 'react';
import { ShieldAlert } from 'lucide-react';
import { MANDATORY_AI_DISCLAIMER } from '../data/mockData';

export default function AiDisclaimer() {
  return (
    <div style={{
      backgroundColor: 'var(--color-warm-ivory)',
      border: '1.5px solid rgba(33, 140, 101, 0.35)',
      borderRadius: '0.75rem',
      padding: '0.85rem 1.1rem',
      display: 'flex',
      alignItems: 'flex-start',
      gap: '0.75rem',
      margin: '1rem 0'
    }}>
      <ShieldAlert size={22} style={{ color: 'var(--color-emerald)', flexShrink: 0, marginTop: '2px' }} />
      <div>
        <div style={{ fontWeight: 700, color: 'var(--color-deep-forest)', fontSize: '0.9rem', marginBottom: '0.2rem' }}>
          Responsible Food Safety Notice
        </div>
        <div style={{ fontSize: '0.825rem', color: 'var(--color-charcoal)', opacity: 0.9, lineHeight: 1.45 }}>
          {MANDATORY_AI_DISCLAIMER}
        </div>
      </div>
    </div>
  );
}
