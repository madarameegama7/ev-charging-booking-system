import { authFetch } from './http';

export async function listUsers() {
	const res = await authFetch('/api/User');
	if (!res.ok) throw new Error('Failed to load users');
	return res.json();
}

export async function createUser({ nic, role, isActive = true, password, firstName, lastName, email, phone }) {
	const payload = {
		NIC: nic,
		Role: role,
		IsActive: isActive,
		Password: password,
		FirstName: firstName,
		LastName: lastName,
		Email: email,
		Phone: phone,
		// Also include Name and PasswordHash for compatibility with server expectations
		Name: `${firstName || ''}${firstName && lastName ? ' ' : ''}${lastName || ''}`.trim(),
		PasswordHash: password // server will re-hash or ignore if it generates its own
	};

	const res = await authFetch('/api/User', {
		method: 'POST',
		body: JSON.stringify(payload)
	});
	if (res.status === 409) throw new Error('NIC already exists');
	if (!res.ok) throw new Error('Create failed');
	return res.json();
}
export async function register({ nic, role, password }) {
  const res = await fetch(`${API_BASE}/api/Auth/register`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ nic, role, passwordHash: password })
  });
  if (!res.ok) throw new Error('Registration failed');
  return res.json();
}


export async function updateUser(nic, data) {
	const res = await authFetch(`/api/User/${encodeURIComponent(nic)}`, {
		method: 'PUT',
		body: JSON.stringify(data)
	});
	if (!res.ok) throw new Error('Update failed');
	return res.json();
}

export async function changePassword(nic, newPassword) {
  const res = await authFetch(`/api/User/${encodeURIComponent(nic)}/password`, {
    method: 'POST',
    body: JSON.stringify({ NewPassword: newPassword })
  });
  if (!res.ok) throw new Error('Password change failed');
  return res.json();
}

export async function setUserStatus(nic, isActive) {
	const res = await authFetch(`/api/User/${encodeURIComponent(nic)}/status?isActive=${isActive}`, {
		method: 'PATCH'
	});
	if (!res.ok) throw new Error('Status update failed');
	return res.json();
}

export async function deleteUser(nic) {
	const res = await authFetch(`/api/User/${encodeURIComponent(nic)}`, { method: 'DELETE' });
	if (res.status === 204) return true;
	if (res.status === 404) throw new Error('User not found');
	throw new Error('Delete failed');
}


