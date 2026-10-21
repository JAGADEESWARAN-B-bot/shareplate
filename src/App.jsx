import React, { useState, useEffect } from 'react';
import Navbar from './components/Navbar';
import Footer from './components/Footer';
import HomePage from './pages/HomePage';
import BrowsePage from './pages/BrowsePage';
import DonatePage from './pages/DonatePage';
import RequestsPage from './pages/RequestsPage';
import DashboardPage from './pages/DashboardPage';
import AdminPage from './pages/AdminPage';
import DetailModal from './components/DetailModal';
import RoleSwitchModal from './components/RoleSwitchModal';
import { INITIAL_USERS, INITIAL_DONATIONS, INITIAL_REQUESTS } from './data/mockData';
import { supabase, isSupabaseConfigured } from './supabase';

export default function App() {
  const [currentTab, setTab] = useState('home');
  const [currentUser, setCurrentUser] = useState(() => {
    const saved = localStorage.getItem('cfsp_user');
    return saved ? JSON.parse(saved) : INITIAL_USERS[1]; // Default to Green Garden Bistro
  });

  const [donations, setDonations] = useState(() => {
    const saved = localStorage.getItem('cfsp_donations');
    return saved ? JSON.parse(saved) : INITIAL_DONATIONS;
  });

  const [requests, setRequests] = useState(() => {
    const saved = localStorage.getItem('cfsp_requests');
    return saved ? JSON.parse(saved) : INITIAL_REQUESTS;
  });

  const [users, setUsers] = useState(() => {
    const saved = localStorage.getItem('cfsp_all_users');
    return saved ? JSON.parse(saved) : INITIAL_USERS;
  });

  const [selectedDonation, setSelectedDonation] = useState(null);
  const [showRoleSwitch, setShowRoleSwitch] = useState(false);

  // Sync to localStorage
  useEffect(() => {
    localStorage.setItem('cfsp_user', JSON.stringify(currentUser));
  }, [currentUser]);

  useEffect(() => {
    localStorage.setItem('cfsp_donations', JSON.stringify(donations));
  }, [donations]);

  useEffect(() => {
    localStorage.setItem('cfsp_requests', JSON.stringify(requests));
  }, [requests]);

  useEffect(() => {
    localStorage.setItem('cfsp_all_users', JSON.stringify(users));
  }, [users]);

  // Handlers
  const handleAddDonation = (newDonation) => {
    setDonations((prev) => [newDonation, ...prev]);
  };

  const handleAddRequest = (newRequest) => {
    setRequests((prev) => [newRequest, ...prev]);
  };

  const handleAddUser = (newUser) => {
    setUsers((prev) => [...prev, newUser]);
  };

  const handleUpdateStatus = (donationId, newStatus) => {
    setDonations((prev) =>
      prev.map((d) => (d.id === donationId ? { ...d, status: newStatus } : d))
    );
    if (selectedDonation && selectedDonation.id === donationId) {
      setSelectedDonation((prev) => ({ ...prev, status: newStatus }));
    }
  };

  return (
    <div style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
      <Navbar 
        currentTab={currentTab} 
        setTab={setTab} 
        currentUser={currentUser} 
        onOpenUserSwitch={() => setShowRoleSwitch(true)} 
      />

      <main style={{ flex: 1 }}>
        {currentTab === 'home' && (
          <HomePage 
            donations={donations} 
            setTab={setTab} 
            onSelectDonation={(d) => setSelectedDonation(d)} 
          />
        )}

        {currentTab === 'browse' && (
          <BrowsePage 
            donations={donations} 
            onSelectDonation={(d) => setSelectedDonation(d)} 
          />
        )}

        {currentTab === 'donate' && (
          <DonatePage 
            currentUser={currentUser} 
            onAddDonation={handleAddDonation} 
            setTab={setTab} 
          />
        )}

        {currentTab === 'requests' && (
          <RequestsPage 
            requests={requests} 
            onAddRequest={handleAddRequest} 
            currentUser={currentUser} 
          />
        )}

        {currentTab === 'dashboard' && (
          <DashboardPage 
            currentUser={currentUser} 
            donations={donations} 
            requests={requests} 
            setTab={setTab} 
            onSelectDonation={(d) => setSelectedDonation(d)}
            onOpenUserSwitch={() => setShowRoleSwitch(true)}
          />
        )}

        {currentTab === 'admin' && (
          <AdminPage 
            donations={donations} 
            onUpdateStatus={handleUpdateStatus} 
            onSelectDonation={(d) => setSelectedDonation(d)}
            currentUser={currentUser}
          />
        )}
      </main>

      {/* Detail Modal */}
      {selectedDonation && (
        <DetailModal 
          donation={selectedDonation} 
          onClose={() => setSelectedDonation(null)} 
          onUpdateStatus={handleUpdateStatus} 
        />
      )}

      {/* Role Switcher Modal */}
      {showRoleSwitch && (
        <RoleSwitchModal 
          currentUser={currentUser} 
          allUsers={users} 
          onSelectUser={(u) => setCurrentUser(u)} 
          onAddUser={handleAddUser}
          onClose={() => setShowRoleSwitch(false)} 
        />
      )}

      <Footer setTab={setTab} />
    </div>
  );
}
