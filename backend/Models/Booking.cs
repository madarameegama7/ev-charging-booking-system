// File: Booking.cs
using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;
using System.Text.Json.Serialization;

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

        [BsonElement("bookingId")]
        public string BookingId { get; set; } = GenerateBookingId();

        [BsonElement("stationId")]
        public string StationId { get; set; }

        [BsonElement("ownerNic")]
        public string OwnerNIC { get; set; }

        // UTC storage fields
        [BsonElement("start")] 
        public DateTime StartTimeUtc { get; set; }
        
        [BsonElement("end")] 
        public DateTime EndTimeUtc { get; set; }
        
        // Local time properties for API - ADD THESE
        [BsonIgnore]
        [JsonPropertyName("startTime")]
        public string StartTime 
        { 
            get => StartTimeUtc.ToString("yyyy-MM-ddTHH:mm:ss");
            set 
            { 
                if (DateTime.TryParse(value, out DateTime localTime))
                {
                    StartTimeUtc = localTime.ToUniversalTime();
                }
                else
                {
                    throw new ArgumentException($"Invalid start time format: {value}");
                }
            }
        }
        
        [BsonIgnore]
        [JsonPropertyName("endTime")]
        public string EndTime 
        { 
            get => EndTimeUtc.ToString("yyyy-MM-ddTHH:mm:ss");
            set 
            { 
                if (DateTime.TryParse(value, out DateTime localTime))
                {
                    EndTimeUtc = localTime.ToUniversalTime();
                }
                else
                {
                    throw new ArgumentException($"Invalid end time format: {value}");
                }
            }
        }

        [BsonElement("status")] 
        public BookingStatus Status { get; set; } = BookingStatus.Pending;
        
        [BsonElement("createdAt")] 
        public DateTime CreatedAtUtc { get; set; } = DateTime.UtcNow;

        private static string GenerateBookingId()
        {
            string randomPart = Guid.NewGuid().ToString("N").Substring(0, 5).ToUpper();
            string datePart = DateTime.UtcNow.ToString("yyyyMMdd");
            return $"BK-{datePart}-{randomPart}";
        }
    }
}