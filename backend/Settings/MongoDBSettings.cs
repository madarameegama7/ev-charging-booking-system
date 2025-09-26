// File: MongoDBSettings.cs
// Description: Strongly typed configuration for MongoDB connection.
using Microsoft.Extensions.Configuration;

namespace Backend.Settings
{
	/// <summary>
	/// Strongly typed MongoDB settings bound from configuration.
	/// </summary>
	public class MongoDBSettings
	{
		public string ConnectionString { get; set; } = string.Empty;
		public string DatabaseName { get; set; } = string.Empty;
	}
}


