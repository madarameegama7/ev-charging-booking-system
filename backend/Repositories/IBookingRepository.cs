// File: IBookingRepository.cs
// Description: Repository interface for bookings
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


