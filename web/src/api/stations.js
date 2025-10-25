import { authFetch } from './http';

export async function listStations() {
	const res = await authFetch('/api/Station');
	if (!res.ok) throw new Error('Failed to load stations');
	return res.json();
}


export async function listStationsByOperator(operatorNic) {
	const stations = await listStations();
	return stations.filter(station => station.operatorNic === operatorNic);
}

export async function createStation(payload) {
	const res = await authFetch('/api/Station', { method: 'POST', body: JSON.stringify(payload) });
	if (res.status === 409) throw new Error('Conflict');
	if (!res.ok) throw new Error('Create failed');
	return res.json();
}

export async function updateStation(id, payload) {
	const res = await authFetch(`/api/Station/${id}`, { method: 'PUT', body: JSON.stringify(payload) });
	if (!res.ok) throw new Error('Update failed');
	return res.json();
}

export async function setStationActive(id, isActive) {
	const res = await authFetch(`/api/Station/${id}/status?isActive=${isActive}`, { method: 'PATCH' });
	if (res.status === 409) throw new Error('Cannot deactivate with active bookings');
	if (!res.ok) throw new Error('Status change failed');
	return true;
}


