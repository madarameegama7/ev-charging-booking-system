import { API_BASE } from '../config';

export async function login(nic, password) {
  const res = await fetch(`${API_BASE}/api/Auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ NIC: nic, Password: password })
  });
  if (!res.ok) throw new Error('Login failed');
  const data = await res.json();
  
  // DEBUG: Log the response to see what we're getting
  console.log('Login response:', data);
  
  localStorage.setItem('token', data.token);
  localStorage.setItem('role', data.role);
  localStorage.setItem('nic', data.nic);
  
  // Store user's name - check various possible field names
  if (data.firstName) {
    localStorage.setItem('firstName', data.firstName);
  } else if (data.FirstName) {
    localStorage.setItem('firstName', data.FirstName);
  }
  
  if (data.lastName) {
    localStorage.setItem('lastName', data.lastName);
  } else if (data.LastName) {
    localStorage.setItem('lastName', data.LastName);
  }
  
  console.log('Stored in localStorage:', {
    firstName: localStorage.getItem('firstName'),
    lastName: localStorage.getItem('lastName')
  });
  
  return data;
}

export async function register(userData) {
  const payload = {
    FirstName: userData.firstName,
    LastName: userData.lastName,
    Email: userData.email,
    Phone: userData.phone || '',
    NIC: userData.nic,
    Password: userData.password,
    Role: userData.role
  };
  
  const res = await fetch(`${API_BASE}/api/Auth/register`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });
  
  if (!res.ok) {
    const errorText = await res.text();
    try {
      const errorData = JSON.parse(errorText);
      throw new Error(errorData.message || errorData.title || 'Registration failed');
    } catch (e) {
      throw new Error(errorText || 'Registration failed');
    }
  }
  
  const data = await res.json();
  
  // DEBUG: Log the response
  console.log('Register response:', data);
  
  localStorage.setItem('token', data.token);
  localStorage.setItem('role', data.role);
  localStorage.setItem('nic', data.nic);
  
  // Try to get name from response, otherwise use what user entered
  const firstName = data.firstName || data.FirstName || userData.firstName;
  const lastName = data.lastName || data.LastName || userData.lastName;
  
  localStorage.setItem('firstName', firstName);
  localStorage.setItem('lastName', lastName);
  
  console.log('Stored in localStorage:', {
    firstName: localStorage.getItem('firstName'),
    lastName: localStorage.getItem('lastName')
  });
  
  return data;
}

export function logout() {
  localStorage.removeItem('token');
  localStorage.removeItem('role');
  localStorage.removeItem('nic');
  localStorage.removeItem('firstName');
  localStorage.removeItem('lastName');
}

export function getCurrentUser() {
  const token = localStorage.getItem('token');
  if (!token) return null;
  
  const user = {
    firstName: localStorage.getItem('firstName'),
    lastName: localStorage.getItem('lastName'),
    role: localStorage.getItem('role'),
    nic: localStorage.getItem('nic')
  };
  
  // DEBUG: Log current user
  console.log('getCurrentUser:', user);
  
  return user;
}