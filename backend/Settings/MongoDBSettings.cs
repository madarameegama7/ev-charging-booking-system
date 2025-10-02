// File: MongoDBSettings.cs
// Description: Strongly typed configuration for MongoDB connection.
using Microsoft.Extensions.Configuration;

namespace Backend.Settings
{

	public class MongoDBSettings
	{
		public string ConnectionString { get; set; } = string.Empty;
		public string DatabaseName { get; set; } = string.Empty;
	}
}


