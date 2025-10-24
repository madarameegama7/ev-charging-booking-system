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
		Completed,
		Active
	}

	public class Booking
	{
		[BsonId]
		[BsonRepresentation(BsonType.ObjectId)]
		public string? Id { get; set; }

		[BsonElement("bookingId")]
		public string BookingId { get; set; } = GenerateBookingId();

		[BsonElement("stationId")]
		public string StationId { get; set; }

		[BsonElement("ownerNic")]
		public string OwnerNIC { get; set; }

		[BsonElement("start")] public DateTime StartTimeUtc { get; set; }
		[BsonElement("end")] public DateTime EndTimeUtc { get; set; }
		[BsonElement("status")] public BookingStatus Status { get; set; } = BookingStatus.Pending;
		[BsonElement("createdAt")] public DateTime CreatedAtUtc { get; set; } = DateTime.UtcNow;

		private static string GenerateBookingId()
		{
			// format: BK-20251010-XY9Z8 (Date + Random)
			string randomPart = Guid.NewGuid().ToString("N").Substring(0, 5).ToUpper();
			string datePart = DateTime.UtcNow.ToString("yyyyMMdd");
			return $"BK-{datePart}-{randomPart}";
		}
	}
}
