// File: IBookingService.cs
// Description: Booking service contract.
using Backend.Models;

namespace Backend.Services
{
	public interface IBookingService
	{
		Task<List<Booking>> GetAllAsync();
		Task<Booking> CreateAsync(Booking booking);
		Task<Booking?> GetByIdAsync(string id);
		Task<List<Booking>> GetByOwnerAsync(string nic);
		Task<List<Booking>> GetByStationAsync(string stationId);
		Task<Booking?> UpdateAsync(string id, Booking update);
	}
}


