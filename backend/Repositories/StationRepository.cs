// File: StationRepository.cs
// Description: MongoDB data access for stations.

using Backend.Data;
using Backend.Models;
using MongoDB.Bson;
using MongoDB.Driver;

namespace Backend.Repositories
{
	public class StationRepository : IStationRepository
	{
		private const string CollectionName = "stations";
		private readonly IMongoCollection<Station> _collection;

		public StationRepository(MongoContext context)
		{
			_collection = context.GetCollection<Station>(CollectionName);
		}

		public Task<List<Station>> GetAllAsync() => _collection.Find(_ => true).ToListAsync();

		public Task<Station?> GetByIdAsync(string id) => _collection.Find(s => s.Id == id).FirstOrDefaultAsync();

		public async Task<Station> CreateAsync(Station station)
		{
			await _collection.InsertOneAsync(station);
			return station;
		}

		public async Task<Station?> UpdateAsync(string id, Station update)
		{
			await _collection.ReplaceOneAsync(s => s.Id == id, update, new ReplaceOptions { IsUpsert = false });
			return await _collection.Find(s => s.Id == id).FirstOrDefaultAsync();
		}
	}
}


