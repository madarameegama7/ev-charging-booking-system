// File: BookingService.cs
// Description: Business logic for creating and updating bookings with validation rules.

using Backend.Models;
using Backend.Repositories;

namespace Backend.Services
{
	public class BookingService : IBookingService
	{
		private readonly IBookingRepository _repo;
		private readonly IStationRepository _stationRepo;
		public Task<List<Booking>> GetAllAsync() => _repo.GetAllAsync();

		public BookingService(IBookingRepository repo, IStationRepository stationRepo)
		{
			_repo = repo;
			_stationRepo = stationRepo;
		}
		

		public async Task<Booking> CreateAsync(Booking booking)
		{
			// Validation: reservation date/time within 7 days from booking date
			var now = DateTime.UtcNow;
			if (booking.StartTimeUtc < now.AddHours(1)) throw new ArgumentException("Start time must be at least 1 hour from now.");
			if (booking.StartTimeUtc > now.AddDays(7)) throw new ArgumentException("Start time must be within 7 days.");
			if (booking.EndTimeUtc <= booking.StartTimeUtc) throw new ArgumentException("End must be after start.");

			// Ensure station exists and is active
			var station = await _stationRepo.GetByIdAsync(booking.StationId);
			if (station is null || !station.IsActive) throw new ArgumentException("Invalid or inactive station.");

			return await _repo.CreateAsync(booking);
		}


		public Task<Booking?> GetByIdAsync(string id) => _repo.GetByIdAsync(id);
		public Task<List<Booking>> GetByOwnerAsync(string nic) => _repo.GetByOwnerAsync(nic);
		public async Task<List<Booking>> GetByStationAsync(string stationId)
		{
			var station = await _stationRepo.GetByStationIdAsync(stationId);
			var stationName = station?.Name;
			return await _repo.GetByStationLooseAsync(stationId, stationName);
		}

		public async Task<Booking?> UpdateAsync(string id, Booking update)
		{
			var existing = await _repo.GetByIdAsync(id);
			if (existing is null) return null;
			// Update rules: modify/cancel at least 12 hours before reservation start
			var now = DateTime.UtcNow;
			if (existing.StartTimeUtc <= now.AddHours(12)) throw new InvalidOperationException("Updates must be at least 12 hours before start.");
			if (update.StartTimeUtc > now.AddDays(7)) throw new ArgumentException("Start time must be within 7 days.");
			if (update.EndTimeUtc <= update.StartTimeUtc) throw new ArgumentException("End must be after start.");
			update.Id = existing.Id;
			update.OwnerNIC = existing.OwnerNIC;
			return await _repo.UpdateAsync(id, update);
		}
	}
}


