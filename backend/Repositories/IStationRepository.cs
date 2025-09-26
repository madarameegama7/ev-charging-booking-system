
// File: IStationRepository.cs
// Description: Repository interface for stations.

using Backend.Models;

namespace Backend.Repositories
{
	public interface IStationRepository
	{
		Task<List<Station>> GetAllAsync();
		Task<Station?> GetByIdAsync(string id);
		Task<Station> CreateAsync(Station station);
		Task<Station?> UpdateAsync(string id, Station update);
	}
}


