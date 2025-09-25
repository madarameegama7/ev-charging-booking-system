
// File: MongoContext.cs
// Description: MongoDB context exposing client and database handles
// ------------------------------------------------------------
using Backend.Settings;
using Microsoft.Extensions.Options;
using MongoDB.Driver;

namespace Backend.Data
{
	/// <summary>
	/// Provides MongoDB database handle and common helpers.
	/// </summary>
	public class MongoContext
	{
		public IMongoDatabase Database { get; }
		public IMongoClient Client { get; }

		public MongoContext(IOptions<MongoDBSettings> options)
		{
			Client = new MongoClient(options.Value.ConnectionString);
			Database = Client.GetDatabase(options.Value.DatabaseName);
		}

		public IMongoCollection<T> GetCollection<T>(string name) => Database.GetCollection<T>(name);
	}
}


