// File: UserRepository.cs
// Description: MongoDB data access for users with NIC unique index.
using Backend.Data;
using Backend.Models;
using MongoDB.Driver;

namespace Backend.Repositories
{
	public class UserRepository : IUserRepository
	{
		private const string CollectionName = "users";
		private readonly IMongoCollection<User> _collection;

		public UserRepository(MongoContext context)
		{
			_collection = context.GetCollection<User>(CollectionName);
			var indexKeys = Builders<User>.IndexKeys.Ascending(u => u.NIC);
			var indexOptions = new CreateIndexOptions { Unique = true, Name = "idx_unique_nic" };
			_collection.Indexes.CreateOne(new CreateIndexModel<User>(indexKeys, indexOptions));
		}

		public async Task<List<User>> GetAllAsync()
		{
			return await _collection.Find(_ => true).ToListAsync();
		}

		public async Task<User?> GetByNicAsync(string nic)
		{
			return await _collection.Find(x => x.NIC == nic).FirstOrDefaultAsync();
		}

		public async Task<User> CreateAsync(User user)
		{
			await _collection.InsertOneAsync(user);
			return user;
		}

		public async Task<long> CountAsync()
		{
			return await _collection.CountDocumentsAsync(_ => true);
		}

		public async Task<bool> DeleteByNicAsync(string nic)
		{
			var result = await _collection.DeleteOneAsync(x => x.NIC == nic);
			return result.DeletedCount > 0;
		}

		public async Task<User?> UpdateByNicAsync(string nic, User update)
		{
			await _collection.ReplaceOneAsync(x => x.NIC == nic, update, new ReplaceOptions { IsUpsert = false });
			return await _collection.Find(x => x.NIC == nic).FirstOrDefaultAsync();
		}
	}
}


