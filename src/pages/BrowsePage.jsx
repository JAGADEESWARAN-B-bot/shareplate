import React, { useState, useMemo } from 'react';
import { Search, X, SlidersHorizontal, CheckCircle2 } from 'lucide-react';
import DonationCard from '../components/DonationCard';
import AiDisclaimer from '../components/AiDisclaimer';

export default function BrowsePage({ donations, onSelectDonation }) {
  const [search, setSearch] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('All');
  const [selectedPricing, setSelectedPricing] = useState('All');
  const [onlyAvailable, setOnlyAvailable] = useState(true);

  const categories = ['All', 'Prepared Meals', 'Bakery', 'Fresh Produce', 'Dairy', 'Pantry & Canned'];
  const pricingOptions = ['All', 'Free Donation', 'Fixed-Price'];

  const filtered = useMemo(() => {
    return donations.filter((item) => {
      const q = search.toLowerCase();
      const matchesSearch = 
        !search ||
        item.foodName?.toLowerCase().includes(q) ||
        item.description?.toLowerCase().includes(q) ||
        item.location?.toLowerCase().includes(q) ||
        item.donorName?.toLowerCase().includes(q);

      const matchesCat = selectedCategory === 'All' || item.category === selectedCategory;
      const matchesPricing = selectedPricing === 'All' || (item.pricingType && item.pricingType.includes(selectedPricing));
      const matchesStatus = onlyAvailable ? item.status === 'Available' : true;

      return matchesSearch && matchesCat && matchesPricing && matchesStatus;
    });
  }, [donations, search, selectedCategory, selectedPricing, onlyAvailable]);

  return (
    <div style={{ padding: '2rem 0 4rem 0' }}>
      <div className="container">
        
        {/* Header */}
        <div style={{ marginBottom: '2rem' }}>
          <h1 style={{ fontSize: '2.25rem', fontWeight: 800, color: 'var(--color-deep-forest)', marginBottom: '0.4rem' }}>
            Find Available Surplus Food
          </h1>
          <p style={{ color: 'var(--color-charcoal)', opacity: 0.8, fontSize: '0.95rem' }}>
            Browse verified community food donations in your neighborhood.
          </p>
        </div>

        {/* Search Bar & Primary Filters */}
        <div style={{
          background: 'var(--color-white)',
          padding: '1.25rem',
          borderRadius: '1rem',
          border: '1px solid var(--color-border)',
          marginBottom: '1.5rem',
          boxShadow: '0 2px 8px rgba(0,0,0,0.03)'
        }}>
          <div style={{ display: 'flex', gap: '0.75rem', marginBottom: '1rem' }}>
            <div style={{
              flex: 1,
              display: 'flex',
              alignItems: 'center',
              gap: '0.6rem',
              background: 'var(--color-warm-ivory)',
              border: '1px solid var(--color-border)',
              borderRadius: '0.5rem',
              padding: '0.65rem 1rem'
            }}>
              <Search size={18} style={{ color: 'var(--color-emerald)' }} />
              <input 
                type="text"
                placeholder="Search food name, description, neighborhood, or donor..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                style={{
                  width: '100%',
                  background: 'transparent',
                  border: 'none',
                  outline: 'none',
                  color: 'var(--color-charcoal)'
                }}
              />
              {search && (
                <button onClick={() => setSearch('')} style={{ background: 'transparent', color: 'var(--color-charcoal)', opacity: 0.6 }}>
                  <X size={16} />
                </button>
              )}
            </div>
          </div>

          {/* Category Filter Chips */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', flexWrap: 'wrap', marginBottom: '1rem' }}>
            <span style={{ fontSize: '0.85rem', fontWeight: 700, color: 'var(--color-deep-forest)', marginRight: '0.25rem' }}>
              Category:
            </span>
            {categories.map((cat) => {
              const active = selectedCategory === cat;
              return (
                <button
                  key={cat}
                  onClick={() => setSelectedCategory(cat)}
                  style={{
                    backgroundColor: active ? 'var(--color-emerald)' : 'var(--color-warm-ivory)',
                    color: active ? 'white' : 'var(--color-charcoal)',
                    padding: '0.35rem 0.8rem',
                    borderRadius: '9999px',
                    fontSize: '0.825rem',
                    fontWeight: active ? 600 : 500,
                    border: '1px solid',
                    borderColor: active ? 'var(--color-emerald)' : 'var(--color-border)'
                  }}
                >
                  {cat}
                </button>
              );
            })}
          </div>

          {/* Pricing & Availability Bar */}
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '1rem', paddingTop: '0.75rem', borderTop: '1px solid var(--color-border)' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <span style={{ fontSize: '0.85rem', fontWeight: 700, color: 'var(--color-deep-forest)' }}>
                Type:
              </span>
              {pricingOptions.map((type) => {
                const active = selectedPricing === type;
                return (
                  <button
                    key={type}
                    onClick={() => setSelectedPricing(type)}
                    style={{
                      backgroundColor: active ? 'var(--color-deep-forest)' : 'transparent',
                      color: active ? 'white' : 'var(--color-charcoal)',
                      padding: '0.3rem 0.7rem',
                      borderRadius: '6px',
                      fontSize: '0.8rem',
                      fontWeight: active ? 600 : 500,
                      border: active ? 'none' : '1px solid var(--color-border)'
                    }}
                  >
                    {type}
                  </button>
                );
              })}
            </div>

            <label style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', cursor: 'pointer', fontSize: '0.875rem', fontWeight: 600, color: 'var(--color-deep-forest)' }}>
              <input 
                type="checkbox" 
                checked={onlyAvailable} 
                onChange={(e) => setOnlyAvailable(e.target.checked)}
                style={{ width: '16px', height: '16px', accentColor: 'var(--color-emerald)' }}
              />
              Show Available Listings Only
            </label>
          </div>
        </div>

        {/* Mandatory Safety Notice */}
        <AiDisclaimer />

        {/* Results Header */}
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', margin: '1.5rem 0 1rem 0' }}>
          <h2 style={{ fontSize: '1.25rem', fontWeight: 700, color: 'var(--color-deep-forest)' }}>
            Showing {filtered.length} {filtered.length === 1 ? 'Donation' : 'Donations'}
          </h2>

          {(search || selectedCategory !== 'All' || selectedPricing !== 'All') && (
            <button
              onClick={() => {
                setSearch('');
                setSelectedCategory('All');
                setSelectedPricing('All');
              }}
              style={{ background: 'transparent', color: 'var(--color-emerald)', fontSize: '0.85rem', fontWeight: 600 }}
            >
              Reset All Filters
            </button>
          )}
        </div>

        {/* Grid of Listings */}
        {filtered.length === 0 ? (
          <div className="card" style={{ padding: '3.5rem 2rem', textAlign: 'center' }}>
            <h3 style={{ fontSize: '1.25rem', fontWeight: 700, marginBottom: '0.5rem' }}>No Listings Found</h3>
            <p style={{ color: 'var(--color-charcoal)', opacity: 0.75, maxWidth: '440px', margin: '0 auto', fontSize: '0.9rem' }}>
              We could not find any active food donations matching your search criteria. Try broadening your keywords or category filters.
            </p>
          </div>
        ) : (
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))', gap: '1.5rem' }}>
            {filtered.map((item) => (
              <DonationCard key={item.id} donation={item} onSelect={onSelectDonation} />
            ))}
          </div>
        )}

      </div>
    </div>
  );
}
