import { API_BASE } from '../config';

export async function login(nic, password) {
  const res = await fetch(`${API_BASE}/api/Auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ NIC: nic, Password: password })
  });
  if (!res.ok) throw new Error('Login failed');
  const data = await res.json();
  localStorage.setItem('token', data.token);
  localStorage.setItem('role', data.role);
  localStorage.setItem('nic', data.nic);
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
    Role: userData.role // Add role to payload
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
  localStorage.setItem('token', data.token);
  localStorage.setItem('role', data.role);
  localStorage.setItem('nic', data.nic);
  return data;
}

export function logout() {
  localStorage.removeItem('token');
  localStorage.removeItem('role');
  localStorage.removeItem('nic');
}