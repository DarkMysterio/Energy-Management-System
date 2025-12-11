import { parseJwtPayload, getCurrentUserRole, getCurrentUserId, getCurrentUserEmail, isTokenExpired } from './jwtUtils';

const AUTH_API = 'http://localhost/auth';

export const authService = {
  async register(name, age, address, email, password, role = 'CLIENT') {
    const response = await fetch(`${AUTH_API}/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name, age, address, email, password, role })
    });
    
    if (!response.ok) {
      let errorMessage = 'Registration failed';
      try {
        const errorData = await response.json();
        errorMessage = errorData.message || errorMessage;
      } catch (e) {
      }
      throw new Error(errorMessage);
    }
    
    return response.json();
  },

  async login(username, password) {
    const response = await fetch(`${AUTH_API}/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: username, password })
    });
    
    if (!response.ok) {
      let errorMessage = 'Invalid credentials';
      try {
        const errorData = await response.json();
        errorMessage = errorData.message || errorMessage;
      } catch (e) {
      }
      throw new Error(errorMessage);
    }
    
    const data = await response.json();
    localStorage.setItem('token', data.token);
    return data;
  },

  logout() {
    localStorage.removeItem('token');
  },

  getToken() {
    return localStorage.getItem('token');
  },

  isAuthenticated() {
    const token = this.getToken();
    return token && !isTokenExpired();
  },

  getUsername() {
    return getCurrentUserEmail();
  },

  getRole() {
    return getCurrentUserRole();
  },

  getUserId() {
    return getCurrentUserId();
  },

  isAdmin() {
    return getCurrentUserRole() === 'ADMIN';
  },

  isClient() {
    return getCurrentUserRole() === 'CLIENT';
  }
};