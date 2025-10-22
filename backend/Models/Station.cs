// File: Station.cs
// Description: Charging station entity with location, type and slots.

using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;
using System;

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

        [BsonElement("stationId")]
        public string StationId { get; set; } = GenerateStationId();

        [BsonElement("name")] public string? Name { get; set; }
        [BsonElement("location")] public GeoLocation Location { get; set; } = new GeoLocation();
        [BsonElement("type")] public string? Type { get; set; }
        [BsonElement("availableSlots")] public int AvailableSlots { get; set; }
        [BsonElement("isActive")] public bool IsActive { get; set; } = true;

        [BsonElement("operatorNic")] public string? OperatorNic { get; set; }

        // Static method to generate a unique StationId
        private static string GenerateStationId()
        {
            // format: ST-20251010-AB12F3 (Date + Random)
            string randomPart = Guid.NewGuid().ToString("N").Substring(0, 5).ToUpper();
            string datePart = DateTime.UtcNow.ToString("yyyyMMdd");
            return $"ST-{datePart}-{randomPart}";
        }
    }
}
