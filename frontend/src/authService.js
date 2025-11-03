const AUTH_API = 'http://localhost/auth';

export const authService = {
  async register(name, age, address, email, password, role = 'CLIENT') {
    const response = await fetch(`${AUTH_API}/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name, age, address, email, password, role })
    });
    
    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || 'Registration failed');
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
      throw new Error('Login failed');
    }
    
    const data = await response.json();
    localStorage.setItem('token', data.token);
    localStorage.setItem('username', data.email);
    localStorage.setItem('role', data.role);
    return data;
  },

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('role');
  },

  getToken() {
    return localStorage.getItem('token');
  },

  isAuthenticated() {
    return !!this.getToken();
  },

  getUsername() {
    return localStorage.getItem('username');
  },

  getRole() {
    return localStorage.getItem('role');
  },

  isAdmin() {
    return this.getRole() === 'ADMIN';
  },

  isClient() {
    return this.getRole() === 'CLIENT';
  }
};