// File: Booking.cs
// Description: Booking entity with start/end times and status
using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;

namespace Backend.Models
{
	public enum BookingStatus
	{
		Pending,
		Approved,
		Cancelled,
		Completed
	}

	public class Booking
	{
		[BsonId]
		[BsonRepresentation(BsonType.ObjectId)]
		public string? Id { get; set; }

		[BsonElement("stationId")]
		[BsonRepresentation(BsonType.ObjectId)]
		public string StationId { get; set; }

		[BsonElement("ownerNic")]
		public string OwnerNIC { get; set; }

		[BsonElement("start")] public DateTime StartTimeUtc { get; set; }
		[BsonElement("end")] public DateTime EndTimeUtc { get; set; }
		[BsonElement("status")] public BookingStatus Status { get; set; } = BookingStatus.Pending;
		[BsonElement("createdAt")] public DateTime CreatedAtUtc { get; set; } = DateTime.UtcNow;
	}
}
