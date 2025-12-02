import { authService } from './authService';

const API_URL = 'http://localhost';

const getHeaders = () => {
  const headers = { 'Content-Type': 'application/json' };
  const token = authService.getToken();
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }
  return headers;
};

const handleResponse = async (response) => {
  if (response.status === 401) {
    // Token expired or invalid - log out
    authService.logout();
    window.location.reload();
    throw new Error('Session expired. Please login again.');
  }
  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(errorText || `HTTP ${response.status}: ${response.statusText}`);
  }
  
  // Handle responses with no content (204, 201, etc.)
  if (response.status === 204 || response.status === 205) {
    return null;
  }
  
  // Check if response has content
  const contentType = response.headers.get('content-type');
  if (contentType && contentType.includes('application/json')) {
    const text = await response.text();
    return text ? JSON.parse(text) : null;
  }
  

  if (response.status === 201) {
    return null;
  }
  
  // Try to parse as JSON for other successful responses
  const text = await response.text();
  return text ? JSON.parse(text) : null;
};

// Users API
export const fetchUsers = async () => {
  const response = await fetch(`${API_URL}/api/users`, {
    headers: getHeaders()
  });
  return handleResponse(response);
};

export const createUser = async (user) => {
  const response = await fetch(`${API_URL}/api/users`, {
    method: 'POST',
    headers: getHeaders(),
    body: JSON.stringify(user)
  });
  return handleResponse(response);
};

export const updateUser = async (id, user) => {
  const response = await fetch(`${API_URL}/api/users/${id}`, {
    method: 'PUT',
    headers: getHeaders(),
    body: JSON.stringify(user)
  });
  return handleResponse(response);
};

export const deleteUser = async (id) => {
  const response = await fetch(`${API_URL}/api/users/${id}`, {
    method: 'DELETE',
    headers: getHeaders()
  });
  return handleResponse(response);
};

export const deleteAllUsers = async () => {
  const response = await fetch(`${API_URL}/api/users`, {
    method: 'DELETE',
    headers: getHeaders()
  });
  return handleResponse(response);
};

// Devices API
export const fetchDevices = async () => {
  const response = await fetch(`${API_URL}/api/devices`, {
    headers: getHeaders()
  });
  return handleResponse(response);
};

export const createDevice = async (device) => {
  const response = await fetch(`${API_URL}/api/devices`, {
    method: 'POST',
    headers: getHeaders(),
    body: JSON.stringify(device)
  });
  return handleResponse(response);
};

export const updateDevice = async (id, device) => {
  const response = await fetch(`${API_URL}/api/devices/${id}`, {
    method: 'PUT',
    headers: getHeaders(),
    body: JSON.stringify(device)
  });
  return handleResponse(response);
};

export const updateDeviceName = async (id, name) => {
  const response = await fetch(`${API_URL}/api/devices/name/${id}`, {
    method: 'PATCH',
    headers: getHeaders(),
    body: JSON.stringify({ name })
  });
  return handleResponse(response);
};

export const updateDeviceConsumption = async (id, consumption) => {
  const response = await fetch(`${API_URL}/api/devices/consumption/${id}`, {
    method: 'PATCH',
    headers: getHeaders(),
    body: JSON.stringify({ consumption })
  });
  return handleResponse(response);
};

export const deleteAllDevices = async () => {
  const response = await fetch(`${API_URL}/api/devices`, {
    method: 'DELETE',
    headers: getHeaders()
  });
  return handleResponse(response);
};

export const deleteDevice = async (id) => {
  const response = await fetch(`${API_URL}/api/devices/${id}`, {
    method: 'DELETE',
    headers: getHeaders()
  });
  return handleResponse(response);
};

// Assignments API
export const fetchAssignments = async () => {
  const response = await fetch(`${API_URL}/api/devices/assign`, {
    headers: getHeaders()
  });
  return handleResponse(response);
};

export const assignDeviceToUser = async (userId, deviceId) => {
  const response = await fetch(`${API_URL}/api/devices/assign`, {
    method: 'POST',
    headers: getHeaders(),
    body: JSON.stringify({ userID: userId, deviceID: deviceId })
  });
  return handleResponse(response);
};

export const deleteAllAssignments = async () => {
  const response = await fetch(`${API_URL}/api/devices/assign`, {
    method: 'DELETE',
    headers: getHeaders()
  });
  return handleResponse(response);
};

export const deleteAssignment = async (userId, deviceId) => {
  const response = await fetch(`${API_URL}/api/devices/assign/assignment/${userId}/${deviceId}`, {
    method: 'DELETE',
    headers: getHeaders()
  });
  return handleResponse(response);
};

// Monitoring API - Energy Consumption
export const fetchDailyConsumption = async (deviceId, date) => {
  const response = await fetch(`${API_URL}/api/monitoring/consumption/${deviceId}?date=${date}`, {
    headers: getHeaders()
  });
  return handleResponse(response);
};

// Fetch total consumption for multiple devices
export const fetchTotalConsumption = async (devices, date) => {
  const response = await fetch(`${API_URL}/api/monitoring/consumption/total?date=${date}`, {
    method: 'POST',
    headers: getHeaders(),
    body: JSON.stringify({ devices: devices.map(d => ({ deviceId: d.id, name: d.name })) })
  });
  return handleResponse(response);
};

// Keep the api object for backward compatibility
export const api = {
  getUsers: fetchUsers,
  createUser,
  updateUser,
  deleteUser,
  deleteAllUsers,
  getDevices: fetchDevices,
  createDevice,
  updateDevice,
  updateDeviceName,
  updateDeviceConsumption,
  deleteAllDevices,
  fetchAssignments,
  assignDevice: assignDeviceToUser,
  deleteAllAssignments
};
