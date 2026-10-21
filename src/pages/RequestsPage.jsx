import React, { useState } from 'react';
import { Handshake, Clock, MapPin, Plus, MessageSquare, Users } from 'lucide-react';

export default function RequestsPage({ requests, onAddRequest, currentUser }) {
  const [showModal, setShowModal] = useState(false);
  const [category, setCategory] = useState('Prepared Meals');
  const [quantity, setQuantity] = useState('25 portions');
  const [servingsNeeded, setServingsNeeded] = useState('25');
  const [neededDateTime, setNeededDateTime] = useState('Today by 6:30 PM');
  const [location, setLocation] = useState(currentUser?.location || 'Riverdale Community Hall');
  const [dietaryRequirements, setDietaryRequirements] = useState('Halal / Vegetarian options');
  const [allergies, setAllergies] = useState('Nut-free preferred');
  const [additionalNotes, setAdditionalNotes] = useState('For our community evening support program.');
  const [contactPhone, setContactPhone] = useState(currentUser?.phone || '+1 (555) 987-6543');

  const handleSubmit = (e) => {
    e.preventDefault();
    const newReq = {
      id: 'REQ-2026-' + Math.random().toString(36).substring(2, 7).toUpperCase(),
      recipientId: currentUser?.id || 'usr_hope',
      recipientName: currentUser?.fullName || 'Community Care Center',
      category,
      quantity,
      servingsNeeded: Number(servingsNeeded) || 10,
      neededDateTime,
      location,
      dietaryRequirements,
      allergies,
      additionalNotes,
      contactPhone,
      status: 'Open',
      createdAt: Date.now()
    };
    onAddRequest(newReq);
    setShowModal(false);
    alert('Food request posted successfully! Local donors can now view and fulfill it.');
  };

  const openWhatsApp = (req) => {
    const phone = (req.contactPhone || '').replace(/[^0-9+]/g, '');
    const message = encodeURIComponent(
      `Hello! I saw your community meal request on Community Food Sharing Platform: "${req.category}" (${req.quantity}, ID: ${req.id}). I have surplus food that could fulfill this request.`
    );
    window.open(`https://api.whatsapp.com/send?phone=${phone}&text=${message}`, '_blank');
  };

  return (
    <div style={{ padding: '2rem 0 4rem 0' }}>
      <div className="container">
        
        {/* Header */}
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '2rem', flexWrap: 'wrap', gap: '1rem' }}>
          <div>
            <h1 style={{ fontSize: '2.25rem', fontWeight: 800, color: 'var(--color-deep-forest)', marginBottom: '0.4rem' }}>
              Community Food Requests
            </h1>
            <p style={{ color: 'var(--color-charcoal)', opacity: 0.8, fontSize: '0.95rem' }}>
              Requests from local shelters, food pantries, and families in need of surplus food.
            </p>
          </div>

          <button 
            className="btn-primary"
            onClick={() => setShowModal(true)}
            style={{ padding: '0.75rem 1.4rem' }}
          >
            <Plus size={18} />
            Post Food Request
          </button>
        </div>

        {/* Requests List */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(360px, 1fr))', gap: '1.5rem' }}>
          {requests.map((req) => (
            <div key={req.id} className="card" style={{ padding: '1.5rem', display: 'flex', flexDirection: 'column' }}>
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '0.75rem' }}>
                <span style={{ backgroundColor: 'var(--color-soft-mint)', color: 'var(--color-emerald)', padding: '0.25rem 0.65rem', borderRadius: '6px', fontWeight: 700, fontSize: '0.8rem' }}>
                  {req.category}
                </span>
                <span className="badge badge-pending">
                  {req.status}
                </span>
              </div>

              <h3 style={{ fontSize: '1.15rem', fontWeight: 700, marginBottom: '0.35rem', color: 'var(--color-deep-forest)' }}>
                {req.quantity} (~{req.servingsNeeded} servings)
              </h3>

              <div style={{ fontSize: '0.875rem', fontWeight: 600, color: 'var(--color-charcoal)', opacity: 0.8, marginBottom: '0.75rem' }}>
                Requested by: {req.recipientName}
              </div>

              {req.additionalNotes && (
                <p style={{ fontSize: '0.85rem', color: 'var(--color-charcoal)', opacity: 0.85, marginBottom: '0.85rem', lineHeight: 1.45 }}>
                  {req.additionalNotes}
                </p>
              )}

              <div style={{ fontSize: '0.825rem', color: 'var(--color-charcoal)', opacity: 0.8, display: 'flex', flexDirection: 'column', gap: '0.35rem', marginBottom: '1rem' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
                  <Clock size={14} style={{ color: 'var(--color-emerald)' }} />
                  <span>Needed: {req.neededDateTime}</span>
                </div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
                  <MapPin size={14} style={{ color: 'var(--color-deep-forest)' }} />
                  <span>{req.location}</span>
                </div>
              </div>

              {(req.dietaryRequirements || req.allergies) && (
                <div style={{ backgroundColor: 'var(--color-warm-ivory)', padding: '0.65rem 0.85rem', borderRadius: '6px', fontSize: '0.8rem', color: 'var(--color-emerald)', marginBottom: '1.25rem', border: '1px solid var(--color-border)' }}>
                  <strong>Dietary / Allergy notes:</strong> {req.dietaryRequirements} {req.allergies ? `• ${req.allergies}` : ''}
                </div>
              )}

              <div style={{ marginTop: 'auto' }}>
                <button
                  className="btn-primary"
                  onClick={() => openWhatsApp(req)}
                  style={{ width: '100%', padding: '0.65rem', fontSize: '0.875rem' }}
                >
                  <MessageSquare size={16} />
                  Connect with Recipient via WhatsApp
                </button>
              </div>
            </div>
          ))}
        </div>

        {/* Modal Form */}
        {showModal && (
          <div style={{
            position: 'fixed',
            inset: 0,
            backgroundColor: 'rgba(0,0,0,0.5)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            zIndex: 1000,
            padding: '1rem'
          }}>
            <div className="card" style={{ maxWidth: '540px', width: '100%', maxHeight: '90vh', overflowY: 'auto', padding: '2rem' }}>
              <h2 style={{ fontSize: '1.4rem', fontWeight: 800, marginBottom: '0.35rem', color: 'var(--color-deep-forest)' }}>
                Post Community Food Request
              </h2>
              <p style={{ fontSize: '0.85rem', color: 'var(--color-charcoal)', opacity: 0.75, marginBottom: '1.25rem' }}>
                Broadcast surplus food needs to participating restaurants, markets, and donors.
              </p>

              <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                <div>
                  <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 600, marginBottom: '0.3rem' }}>Requested Category *</label>
                  <input type="text" value={category} onChange={(e) => setCategory(e.target.value)} style={{ width: '100%', padding: '0.65rem', borderRadius: '0.5rem', border: '1px solid var(--color-border)' }} required />
                </div>

                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.75rem' }}>
                  <div>
                    <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 600, marginBottom: '0.3rem' }}>Quantity *</label>
                    <input type="text" value={quantity} onChange={(e) => setQuantity(e.target.value)} style={{ width: '100%', padding: '0.65rem', borderRadius: '0.5rem', border: '1px solid var(--color-border)' }} required />
                  </div>
                  <div>
                    <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 600, marginBottom: '0.3rem' }}>People to Serve *</label>
                    <input type="number" value={servingsNeeded} onChange={(e) => setServingsNeeded(e.target.value)} style={{ width: '100%', padding: '0.65rem', borderRadius: '0.5rem', border: '1px solid var(--color-border)' }} required />
                  </div>
                </div>

                <div>
                  <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 600, marginBottom: '0.3rem' }}>Needed Date & Time *</label>
                  <input type="text" value={neededDateTime} onChange={(e) => setNeededDateTime(e.target.value)} style={{ width: '100%', padding: '0.65rem', borderRadius: '0.5rem', border: '1px solid var(--color-border)' }} required />
                </div>

                <div>
                  <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 600, marginBottom: '0.3rem' }}>Location *</label>
                  <input type="text" value={location} onChange={(e) => setLocation(e.target.value)} style={{ width: '100%', padding: '0.65rem', borderRadius: '0.5rem', border: '1px solid var(--color-border)' }} required />
                </div>

                <div>
                  <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 600, marginBottom: '0.3rem' }}>Dietary & Allergy Requirements</label>
                  <input type="text" value={dietaryRequirements} onChange={(e) => setDietaryRequirements(e.target.value)} style={{ width: '100%', padding: '0.65rem', borderRadius: '0.5rem', border: '1px solid var(--color-border)' }} />
                </div>

                <div>
                  <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 600, marginBottom: '0.3rem' }}>WhatsApp Contact Phone *</label>
                  <input type="text" value={contactPhone} onChange={(e) => setContactPhone(e.target.value)} style={{ width: '100%', padding: '0.65rem', borderRadius: '0.5rem', border: '1px solid var(--color-border)' }} required />
                </div>

                <div>
                  <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 600, marginBottom: '0.3rem' }}>Additional Context</label>
                  <textarea rows={2} value={additionalNotes} onChange={(e) => setAdditionalNotes(e.target.value)} style={{ width: '100%', padding: '0.65rem', borderRadius: '0.5rem', border: '1px solid var(--color-border)' }} />
                </div>

                <div style={{ display: 'flex', gap: '0.75rem', justifyContent: 'flex-end', marginTop: '0.5rem' }}>
                  <button type="button" className="btn-outline" onClick={() => setShowModal(false)}>Cancel</button>
                  <button type="submit" className="btn-primary">Post Request</button>
                </div>
              </form>
            </div>
          </div>
        )}

      </div>
    </div>
  );
}
