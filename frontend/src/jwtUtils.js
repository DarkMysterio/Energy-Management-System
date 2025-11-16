/**
 * Decodes a JWT token payload
 * @param {string} token - The JWT token
 * @returns {object|null} The decoded payload or null if invalid
 */
export const parseJwtPayload = (token) => {
  if (!token || typeof token !== 'string') return null;
  
  try {
    const parts = token.split('.');
    if (parts.length !== 3) return null;
    
    // Decode the payload (second part)
    const payload = parts[1];
    const decoded = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
    return JSON.parse(decoded);
  } catch (err) {
    console.error('Failed to parse JWT:', err);
    return null;
  }
};

/**
 * Gets the current user's role from the stored token
 * @returns {string|null} The user role or null
 */
export const getCurrentUserRole = () => {
  const token = localStorage.getItem('token');
  const payload = parseJwtPayload(token);
  return payload?.role || null;
};

/**
 * Gets the current user's ID from the stored token
 * @returns {string|null} The user ID or null
 */
export const getCurrentUserId = () => {
  const token = localStorage.getItem('token');
  const payload = parseJwtPayload(token);
  return payload?.userId || null;
};

/**
 * Gets the current user's email from the stored token
 * @returns {string|null} The user email or null
 */
export const getCurrentUserEmail = () => {
  const token = localStorage.getItem('token');
  const payload = parseJwtPayload(token);
  return payload?.sub || null;
};

/**
 * Checks if the token is expired
 * @returns {boolean} True if expired or invalid
 */
export const isTokenExpired = () => {
  const token = localStorage.getItem('token');
  const payload = parseJwtPayload(token);
  
  if (!payload?.exp) return true;
  
  return Date.now() >= payload.exp * 1000;
};
