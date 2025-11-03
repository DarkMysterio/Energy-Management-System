import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Route, Routes, Link } from 'react-router-dom';
import { authService } from './authService';
import LoginPage from './pages/LoginPage';
import UsersPage from './pages/UsersPage';
import DevicesPage from './pages/DevicesPage';
import AssignDevicePage from './pages/AssignDevicePage';
import ClientDevicesPage from './pages/ClientDevicesPage';
import './App.css';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [currentPage, setCurrentPage] = useState('default');
  const [userRole, setUserRole] = useState('');

  useEffect(() => {
    const authenticated = authService.isAuthenticated();
    setIsAuthenticated(authenticated);
    if (authenticated) {
      const role = authService.getRole();
      setUserRole(role);
      setCurrentPage(role === 'ADMIN' ? 'users' : 'mydevices');
    }
  }, []);

  const handleLoginSuccess = () => {
    setIsAuthenticated(true);
    const role = authService.getRole();
    setUserRole(role);
    setCurrentPage(role === 'ADMIN' ? 'users' : 'mydevices');
  };

  const handleLogout = () => {
    authService.logout();
    setIsAuthenticated(false);
    setUserRole('');
    setCurrentPage('default');
  };

  if (!isAuthenticated) {
    return <LoginPage onLoginSuccess={handleLoginSuccess} />;
  }

  return (
    <Router>
      <div className="App">
        <nav style={styles.nav}>
          <div style={styles.navLeft}>
            {userRole === 'ADMIN' ? (
              <>
                <button
                  onClick={() => setCurrentPage('users')}
                  style={styles.navButton}
                >
                  Users
                </button>
                <button
                  onClick={() => setCurrentPage('devices')}
                  style={styles.navButton}
                >
                  Devices
                </button>
                <button
                  onClick={() => setCurrentPage('assign')}
                  style={styles.navButton}
                >
                  Assign Device
                </button>
              </>
            ) : (
              <button
                onClick={() => setCurrentPage('mydevices')}
                style={styles.navButton}
              >
                My Devices
              </button>
            )}
          </div>
          <div style={styles.navRight}>
            <span style={styles.username}>
              Welcome, {authService.getUsername()}
            </span>
            <button
              onClick={handleLogout}
              style={styles.logoutButton}
            >
              Logout
            </button>
          </div>
        </nav>

        <div style={styles.content}>
          {userRole === 'ADMIN' ? (
            <>
              {currentPage === 'users' && <UsersPage />}
              {currentPage === 'devices' && <DevicesPage />}
              {currentPage === 'assign' && <AssignDevicePage />}
            </>
          ) : (
            <>
              {currentPage === 'mydevices' && <ClientDevicesPage />}
            </>
          )}
        </div>
      </div>
    </Router>
  );
}

const styles = {
  nav: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: '15px 30px',
    backgroundColor: '#282c34',
    color: 'white',
  },
  navLeft: {
    display: 'flex',
    gap: '10px',
  },
  navRight: {
    display: 'flex',
    alignItems: 'center',
    gap: '15px',
  },
  navButton: {
    padding: '8px 16px',
    backgroundColor: '#007bff',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
  },
  username: {
    fontSize: '14px',
  },
  logoutButton: {
    padding: '8px 16px',
    backgroundColor: '#dc3545',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
  },
  content: {
    padding: '20px',
  },
};

export default App;
