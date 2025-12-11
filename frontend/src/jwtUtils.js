export const parseJwtPayload = (token) => {
  if (!token || typeof token !== 'string') return null;
  
  try {
    const parts = token.split('.');
    if (parts.length !== 3) return null;
    
    const payload = parts[1];
    const decoded = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
    return JSON.parse(decoded);
  } catch (err) {
    console.error('Failed to parse JWT:', err);
    return null;
  }
};

export const getCurrentUserRole = () => {
  const token = localStorage.getItem('token');
  const payload = parseJwtPayload(token);
  return payload?.role || null;
};

export const getCurrentUserId = () => {
  const token = localStorage.getItem('token');
  const payload = parseJwtPayload(token);
  return payload?.userId || null;
};

export const getCurrentUserEmail = () => {
  const token = localStorage.getItem('token');
  const payload = parseJwtPayload(token);
  return payload?.sub || null;
};

export const isTokenExpired = () => {
  const token = localStorage.getItem('token');
  const payload = parseJwtPayload(token);
  
  if (!payload?.exp) return true;
  
  return Date.now() >= payload.exp * 1000;
};
