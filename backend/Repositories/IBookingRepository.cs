// ------------------------------------------------------------
// Sri Lanka Institute of Information Technology
// Module: SE4040 – Enterprise Application Development
// Project: EV Charging Station Booking System
// File: IBookingRepository.cs
// Author: <Your Name / IT Number>
// Description: Repository interface for bookings.
// Date: 2025-09-25
// ------------------------------------------------------------
using Backend.Models;

namespace Backend.Repositories
{
	public interface IBookingRepository
	{
		Task<Booking> CreateAsync(Booking booking);
		Task<Booking?> GetByIdAsync(string id);
		Task<List<Booking>> GetByOwnerAsync(string nic);
		Task<List<Booking>> GetByStationAsync(string stationId);
		Task<Booking?> UpdateAsync(string id, Booking update);
		Task<bool> HasActiveBookingsForStationAsync(string stationId);
	}
}


