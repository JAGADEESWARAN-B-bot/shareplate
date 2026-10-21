import React from 'react';
import { ArrowRight, PlusCircle, Search, Leaf, ShieldCheck, HeartHandshake, PackageCheck, Users2, Sparkles } from 'lucide-react';
import DonationCard from '../components/DonationCard';
import AiDisclaimer from '../components/AiDisclaimer';

export default function HomePage({ donations, setTab, onSelectDonation }) {
  const availableDonations = donations.filter((d) => d.status === 'Available');

  const totalServings = donations.reduce((sum, d) => sum + (Number(d.servings) || 0), 0);

  return (
    <div>
      {/* Hero Section */}
      <section style={{
        background: 'linear-gradient(135deg, var(--color-deep-forest) 0%, #1c4b40 100%)',
        color: 'white',
        padding: '4.5rem 0 3.5rem 0',
        position: 'relative',
        overflow: 'hidden'
      }}>
        <div className="container" style={{ position: 'relative', zIndex: 2 }}>
          <div style={{ maxWidth: '780px' }}>
            <div style={{
              display: 'inline-flex',
              alignItems: 'center',
              gap: '0.5rem',
              backgroundColor: 'rgba(33, 140, 101, 0.35)',
              border: '1px solid rgba(45, 176, 129, 0.4)',
              padding: '0.4rem 0.9rem',
              borderRadius: '9999px',
              fontSize: '0.85rem',
              color: 'var(--color-warm-ivory)',
              fontWeight: 600,
              marginBottom: '1.25rem'
            }}>
              <Leaf size={16} /> Community-Driven Surplus Food Redistribution
            </div>

            <h1 style={{
              fontSize: '3rem',
              fontWeight: 800,
              lineHeight: 1.15,
              color: 'var(--color-white)',
              marginBottom: '1.25rem'
            }}>
              Share More.<br />
              Waste Less.<br />
              <span style={{ color: 'var(--color-emerald-light)' }}>Care Together.</span>
            </h1>

            <p style={{
              fontSize: '1.15rem',
              color: 'var(--color-soft-mint)',
              lineHeight: 1.6,
              marginBottom: '2rem',
              maxWidth: '660px'
            }}>
              Connecting restaurants, grocery stores, bakeries, and neighbors with people in need.
              Featuring AI-assisted food quality screening, strict safety checklists, and direct local coordination.
            </p>

            <div style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap' }}>
              <button 
                className="btn-primary" 
                style={{ padding: '0.9rem 1.8rem', fontSize: '1rem' }}
                onClick={() => setTab('donate')}
              >
                <PlusCircle size={20} />
                Donate Food
              </button>

              <button 
                className="btn-outline" 
                style={{ padding: '0.9rem 1.8rem', fontSize: '1rem', color: 'white', borderColor: 'rgba(255,255,255,0.7)' }}
                onClick={() => setTab('browse')}
              >
                <Search size={20} />
                Find Available Food
              </button>
            </div>
          </div>
        </div>
      </section>

      {/* Impact Statistics Strip */}
      <section style={{ marginTop: '-1.5rem', position: 'relative', zIndex: 3 }}>
        <div className="container">
          <div style={{
            background: 'var(--color-white)',
            borderRadius: '1rem',
            padding: '1.5rem 2rem',
            boxShadow: '0 8px 24px rgba(23, 63, 53, 0.08)',
            border: '1px solid var(--color-border)',
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
            gap: '1.5rem',
            textAlign: 'center'
          }}>
            <div>
              <div style={{ fontSize: '2rem', fontWeight: 800, color: 'var(--color-emerald)', fontFamily: 'var(--font-heading)' }}>
                {donations.length}
              </div>
              <div style={{ fontSize: '0.875rem', color: 'var(--color-charcoal)', opacity: 0.75, fontWeight: 500 }}>
                Total Food Donations
              </div>
            </div>

            <div>
              <div style={{ fontSize: '2rem', fontWeight: 800, color: 'var(--color-deep-forest)', fontFamily: 'var(--font-heading)' }}>
                {availableDonations.length}
              </div>
              <div style={{ fontSize: '0.875rem', color: 'var(--color-charcoal)', opacity: 0.75, fontWeight: 500 }}>
                Ready for Pickup
              </div>
            </div>

            <div>
              <div style={{ fontSize: '2rem', fontWeight: 800, color: 'var(--color-emerald)', fontFamily: 'var(--font-heading)' }}>
                {totalServings + 50}+
              </div>
              <div style={{ fontSize: '0.875rem', color: 'var(--color-charcoal)', opacity: 0.75, fontWeight: 500 }}>
                Meals & Servings Shared
              </div>
            </div>

            <div>
              <div style={{ fontSize: '2rem', fontWeight: 800, color: 'var(--color-deep-forest)', fontFamily: 'var(--font-heading)' }}>
                100%
              </div>
              <div style={{ fontSize: '0.875rem', color: 'var(--color-charcoal)', opacity: 0.75, fontWeight: 500 }}>
                Verified Safety Checklist
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Mandatory Safety Notice */}
      <section style={{ padding: '1.5rem 0 0.5rem 0' }}>
        <div className="container">
          <AiDisclaimer />
        </div>
      </section>

      {/* How It Works */}
      <section style={{ padding: '3rem 0' }}>
        <div className="container">
          <div style={{ textAlign: 'center', marginBottom: '2.5rem' }}>
            <h2 style={{ fontSize: '2rem', fontWeight: 800, marginBottom: '0.5rem' }}>
              How Community Food Sharing Works
            </h2>
            <p style={{ color: 'var(--color-charcoal)', opacity: 0.75, maxWidth: '580px', margin: '0 auto', fontSize: '0.95rem' }}>
              A safe, transparent 3-step workflow designed to connect surplus fresh food with people in need before it spoils.
            </p>
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '1.5rem' }}>
            <div className="card" style={{ padding: '1.75rem' }}>
              <div style={{ width: '48px', height: '48px', borderRadius: '12px', backgroundColor: 'var(--color-soft-mint)', display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--color-emerald)', marginBottom: '1.25rem' }}>
                <PackageCheck size={26} />
              </div>
              <h3 style={{ fontSize: '1.2rem', fontWeight: 700, marginBottom: '0.5rem' }}>1. List Surplus Food</h3>
              <p style={{ fontSize: '0.9rem', color: 'var(--color-charcoal)', opacity: 0.8, lineHeight: 1.5 }}>
                Donors provide food quantity, category, packaging, temperature control history, and allergen disclosures.
              </p>
            </div>

            <div className="card" style={{ padding: '1.75rem' }}>
              <div style={{ width: '48px', height: '48px', borderRadius: '12px', backgroundColor: 'var(--color-soft-mint)', display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--color-emerald)', marginBottom: '1.25rem' }}>
                <Sparkles size={26} />
              </div>
              <h3 style={{ fontSize: '1.2rem', fontWeight: 700, marginBottom: '0.5rem' }}>2. AI Quality Screening</h3>
              <p style={{ fontSize: '0.9rem', color: 'var(--color-charcoal)', opacity: 0.8, lineHeight: 1.5 }}>
                AI visual analysis flags discoloration, mold patterns, or package damage, paired with a mandatory donor food safety review.
              </p>
            </div>

            <div className="card" style={{ padding: '1.75rem' }}>
              <div style={{ width: '48px', height: '48px', borderRadius: '12px', backgroundColor: 'var(--color-soft-mint)', display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--color-emerald)', marginBottom: '1.25rem' }}>
                <HeartHandshake size={26} />
              </div>
              <h3 style={{ fontSize: '1.2rem', fontWeight: 700, marginBottom: '0.5rem' }}>3. Direct Community Coordination</h3>
              <p style={{ fontSize: '0.9rem', color: 'var(--color-charcoal)', opacity: 0.8, lineHeight: 1.5 }}>
                Local community members, pantries, and shelters coordinate convenient pickup directly via WhatsApp or phone.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Available Food Listings */}
      <section style={{ padding: '2rem 0 4rem 0' }}>
        <div className="container">
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '1.75rem' }}>
            <div>
              <h2 style={{ fontSize: '1.75rem', fontWeight: 800, color: 'var(--color-deep-forest)' }}>
                Latest Available Donations
              </h2>
              <p style={{ color: 'var(--color-charcoal)', opacity: 0.75, fontSize: '0.9rem' }}>
                Real surplus meals and produce available right now in your area
              </p>
            </div>
            <button 
              className="btn-outline" 
              style={{ fontSize: '0.875rem', padding: '0.5rem 1rem' }}
              onClick={() => setTab('browse')}
            >
              View All ({availableDonations.length})
            </button>
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))', gap: '1.5rem' }}>
            {availableDonations.slice(0, 3).map((donation) => (
              <DonationCard 
                key={donation.id} 
                donation={donation} 
                onSelect={onSelectDonation} 
              />
            ))}
          </div>
        </div>
      </section>
    </div>
  );
}
