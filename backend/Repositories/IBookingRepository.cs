// File: IBookingRepository.cs
// Description: Repository interface for bookings
using Backend.Models;

namespace Backend.Repositories
{
	public interface IBookingRepository
	{
		Task<List<Booking>> GetAllAsync();
		Task<Booking> CreateAsync(Booking booking);
		Task<Booking?> GetByIdAsync(string id);
		Task<List<Booking>> GetByOwnerAsync(string nic);
		Task<List<Booking>> GetByStationAsync(string stationId);
		// Loose lookup: match by station id OR station name (legacy data where stationId stores name)
		Task<List<Booking>> GetByStationLooseAsync(string stationId, string? stationName);
		Task<Booking?> UpdateAsync(string id, Booking update);
		Task<bool> HasActiveBookingsForStationAsync(string stationId);
	}
}


