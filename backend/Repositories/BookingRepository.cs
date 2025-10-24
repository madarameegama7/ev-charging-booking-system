// File: BookingRepository.cs
// Description: MongoDB data access for bookings

using Backend.Data;
using Backend.Models;
using MongoDB.Driver;
using System.Text.RegularExpressions;

namespace Backend.Repositories
{
	public class BookingRepository : IBookingRepository
	{
		private const string CollectionName = "bookings";
		private readonly IMongoCollection<Booking> _collection;

		public BookingRepository(MongoContext context)
		{
			_collection = context.GetCollection<Booking>(CollectionName);
		}

		public Task<List<Booking>> GetByStationsAsync(List<string> stationIds)
		{
			var filter = Builders<Booking>.Filter.In(b => b.StationId, stationIds);
			return _collection.Find(filter).ToListAsync();
		}

		public async Task<List<Booking>> GetAllAsync()
		{
			return await _collection.Find(_ => true).ToListAsync();
		}

		public async Task<Booking> CreateAsync(Booking booking)
		{
			await _collection.InsertOneAsync(booking);
			return booking;
		}

		public async Task<Booking?> GetByIdAsync(string id)
		{
			return await _collection.Find(b => b.Id == id).FirstOrDefaultAsync();
		}

		public Task<List<Booking>> GetByOwnerAsync(string nic) => _collection.Find(b => b.OwnerNIC == nic).ToListAsync();

		public Task<List<Booking>> GetByStationAsync(string stationId) => _collection.Find(b => b.StationId == stationId).ToListAsync();

		public Task<List<Booking>> GetByStationLooseAsync(string stationId, string? stationName)
		{
			// Match bookings where StationId equals the id, or equals the station name (legacy), or contains it.
			var filters = new List<FilterDefinition<Booking>>();
			filters.Add(Builders<Booking>.Filter.Eq(b => b.StationId, stationId));
			if (!string.IsNullOrWhiteSpace(stationName))
			{
				// case-insensitive match on station name
				var esc = Regex.Escape(stationName);
				filters.Add(Builders<Booking>.Filter.Regex("stationId", new MongoDB.Bson.BsonRegularExpression($"^{esc}$", "i")));
				filters.Add(Builders<Booking>.Filter.Regex("stationId", new MongoDB.Bson.BsonRegularExpression(esc, "i")));
			}
			var combined = Builders<Booking>.Filter.Or(filters);
			return _collection.Find(combined).ToListAsync();
		}

		public async Task<Booking?> UpdateAsync(string id, Booking update)
		{
			await _collection.ReplaceOneAsync(b => b.Id == id, update, new ReplaceOptions { IsUpsert = false });
			return await _collection.Find(b => b.Id == id).FirstOrDefaultAsync();
		}

		public async Task<bool> HasActiveBookingsForStationAsync(string stationId)
		{
			var filter = Builders<Booking>.Filter.And(
				Builders<Booking>.Filter.Eq(b => b.StationId, stationId),
				Builders<Booking>.Filter.In(b => b.Status, new[] { BookingStatus.Pending, BookingStatus.Approved })
			);
			var count = await _collection.CountDocumentsAsync(filter);
			return count > 0;
		}
	}
}