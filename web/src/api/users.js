import { authFetch } from './http';

export async function listUsers() {
	const res = await authFetch('/api/User');
	if (!res.ok) throw new Error('Failed to load users');
	return res.json();
}

export async function createUser({ nic, role, isActive = true }) {
	const res = await authFetch('/api/User', {
		method: 'POST',
		body: JSON.stringify({ nic, role, isActive })
	});
	if (res.status === 409) throw new Error('NIC already exists');
	if (!res.ok) throw new Error('Create failed');
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


