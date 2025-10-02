import { authFetch } from './http';

export async function listAllBookings() {
	const res = await authFetch('/api/Booking');
	if (!res.ok) throw new Error('Failed to load bookings');
	return res.json();
}

export async function listBookingsByStation(stationId) {
	const res = await authFetch(`/api/Booking/station/${encodeURIComponent(stationId)}`);
	if (!res.ok) throw new Error('Failed to load station bookings');
	return res.json();
}

export async function updateBooking(id, payload) {
	const res = await authFetch(`/api/Booking/${id}`, { method: 'PUT', body: JSON.stringify(payload) });
	if (!res.ok) throw new Error('Update failed');
	return res.json();
}

export async function cancelBooking(id) {
	// Convention: set status to Cancelled (2) and keep times as-is
	// Caller should pass the current booking to preserve fields if needed
	const current = await authFetch(`/api/Booking/${id}`);
	if (!current.ok) throw new Error('Load failed');
	const b = await current.json();
	const res = await authFetch(`/api/Booking/${id}`, {
		method: 'PUT',
		body: JSON.stringify({
			...b,
			status: 2
		})
	});
	if (!res.ok) throw new Error('Cancel failed');
	return res.json();
}


