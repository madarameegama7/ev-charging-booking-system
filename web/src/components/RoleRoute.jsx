import { Navigate } from 'react-router-dom';

export default function RoleRoute({ roles = [], children }) {
	const token = localStorage.getItem('token');
	const role = localStorage.getItem('role');
	if (!token) return <Navigate to="/login" replace />;
	if (roles.length && !roles.includes(role)) return <Navigate to="/home" replace />;
	return children;
}


