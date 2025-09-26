// File: Station.cs
// Description: Charging station entity with location, type and slots.

using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;

namespace Backend.Models
{
	public class GeoLocation
	{
		[BsonElement("lat")] public double Latitude { get; set; }
		[BsonElement("lng")] public double Longitude { get; set; }
	}

	public class Station
	{
		[BsonId]
		[BsonRepresentation(BsonType.ObjectId)]
		public string? Id { get; set; }

		[BsonElement("name")] public string Name { get; set; }
		[BsonElement("location")] public GeoLocation Location { get; set; } = new GeoLocation();
		[BsonElement("type")] public string Type { get; set; }
		[BsonElement("availableSlots")] public int AvailableSlots { get; set; }
		[BsonElement("isActive")] public bool IsActive { get; set; } = true;
	}
}


