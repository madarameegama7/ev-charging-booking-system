// File: IStationService.cs
// Description: Station service contract.
using Backend.Models;

namespace Backend.Services
{
	public interface IStationService
	{
		Task<List<Station>> GetAllAsync();
		Task<Station?> GetByIdAsync(string id);
		Task<Station> CreateAsync(Station station);
		Task<Station?> UpdateAsync(string id, Station update);
		Task<bool> SetActiveAsync(string id, bool isActive);
		Task<Station?> GetByStationIdAsync(string stationId);
	}
}


