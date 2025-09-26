// File: BookingRepository.cs
// Description: MongoDB data access for bookings

using Backend.Data;
using Backend.Models;
using MongoDB.Driver;

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

		public async Task<Booking> CreateAsync(Booking booking)
		{
			await _collection.InsertOneAsync(booking);
			return booking;
		}

		public Task<Booking?> GetByIdAsync(string id) => _collection.Find(b => b.Id == id).FirstOrDefaultAsync();

		public Task<List<Booking>> GetByOwnerAsync(string nic) => _collection.Find(b => b.OwnerNIC == nic).ToListAsync();

		public Task<List<Booking>> GetByStationAsync(string stationId) => _collection.Find(b => b.StationId == stationId).ToListAsync();

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


