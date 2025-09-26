import 'leaflet/dist/leaflet.css';
import { MapContainer, TileLayer, Marker, useMapEvents } from 'react-leaflet';
import L from 'leaflet';
import { useState } from 'react';

// Ensure default marker icons load in bundlers
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
	iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
	iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
	shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png'
});

function ClickHandler({ onClick }) {
	useMapEvents({
		click(e) {
			onClick(e.latlng);
		}
	});
	return null;
}

export default function MapPicker({ value, onChange, height = 300, zoom = 12 }) {
	const [position, setPosition] = useState(value || { lat: 6.9271, lng: 79.8612 });

	const handleClick = (latlng) => {
		setPosition(latlng);
		onChange && onChange({ lat: latlng.lat, lng: latlng.lng });
	};

	return (
		<div>
			<MapContainer center={[position.lat, position.lng]} zoom={zoom} style={{ height }}>
				<TileLayer
					url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
					attribution="&copy; OpenStreetMap contributors"
				/>
				<ClickHandler onClick={handleClick} />
				<Marker
					position={[position.lat, position.lng]}
					draggable
					eventHandlers={{ dragend: (e) => {
						const m = e.target;
						const ll = m.getLatLng();
						setPosition(ll);
						onChange && onChange({ lat: ll.lat, lng: ll.lng });
					}}}
				/>
			</MapContainer>
		</div>
	);
}


