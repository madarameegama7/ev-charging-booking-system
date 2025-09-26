import { useEffect, useState } from 'react';
import { API_BASE } from '../config';

export default function Home() {
	const [stations, setStations] = useState([]);
	useEffect(() => {
		fetch(`${API_BASE}/api/Station`).then(r=>r.json()).then(setStations).catch(()=>{});
	},[]);
	return (
		<div className="p-6">
			<h1 className="text-xl mb-4">Stations</h1>
			<ul className="space-y-2">
				{stations.map(s => (
					<li key={s.id} className="border p-3">
						<div className="font-semibold">{s.name}</div>
						<div className="text-sm">{s.type} • slots: {s.availableSlots}</div>
					</li>
				))}
			</ul>
		</div>
	);
}
