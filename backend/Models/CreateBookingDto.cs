// File: Models/CreateBookingDto.cs
using System.Text.Json.Serialization;

namespace Backend.Models
{
    public class CreateBookingDto
    {
        [JsonPropertyName("stationId")]
        public string StationId { get; set; }

        [JsonPropertyName("ownerNic")]
        public string OwnerNIC { get; set; }

        [JsonPropertyName("startTime")]
        public string StartTime { get; set; }

        [JsonPropertyName("endTime")]
        public string EndTime { get; set; }
    }
}