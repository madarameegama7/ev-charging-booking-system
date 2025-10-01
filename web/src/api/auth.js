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



export function logout() {
	localStorage.removeItem('token');
	localStorage.removeItem('role');
	localStorage.removeItem('nic');
}

