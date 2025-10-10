// File: StationService.cs
// Description: Business logic for station CRUD and activation constraints.
using Backend.Models;
using Backend.Repositories;

namespace Backend.Services
{
	public class StationService : IStationService
	{
		private readonly IStationRepository _repo;
		private readonly IBookingRepository _bookingRepo;

		public StationService(IStationRepository repo, IBookingRepository bookingRepo)
		{
			_repo = repo;
			_bookingRepo = bookingRepo;
		}

		public Task<List<Station>> GetAllAsync() => _repo.GetAllAsync();
		public Task<Station?> GetByIdAsync(string id) => _repo.GetByIdAsync(id);
		
		public Task<Station> CreateAsync(Station station) => _repo.CreateAsync(station);
public async Task<Station?> GetByStationIdAsync(string stationId)
{
    return await _repo.GetByStationIdAsync(stationId);
}


		public async Task<Station?> UpdateAsync(string id, Station update)
		{
			var existing = await _repo.GetByIdAsync(id);
			if (existing is null) return null;
			update.Id = existing.Id;
			return await _repo.UpdateAsync(id, update);
		}

		public async Task<bool> SetActiveAsync(string id, bool isActive)
		{
			var existing = await _repo.GetByIdAsync(id);
			if (existing is null) return false;
			if (!isActive)
			{
				var hasActive = await _bookingRepo.HasActiveBookingsForStationAsync(id);
				if (hasActive) return false;
			}
			existing.IsActive = isActive;
			await _repo.UpdateAsync(id, existing);
			return true;
		}
	}
}


