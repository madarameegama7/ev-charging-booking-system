import { API_BASE } from '../config';

export async function authFetch(path, options = {}) {
	const token = localStorage.getItem('token');
	const res = await fetch(`${API_BASE}${path}`, {
		...options,
		headers: {
			'Content-Type': 'application/json',
			...(options.headers || {}),
			...(token ? { Authorization: `Bearer ${token}` } : {})
		}
	});
	return res;
}

