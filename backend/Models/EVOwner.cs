// File: EVOwner.cs
// Description: EV owner profile entity for mobile-side account management.
using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;

namespace Backend.Models
{

	public class EVOwner
	{
		[BsonId]
		[BsonRepresentation(BsonType.ObjectId)]
		public string? Id { get; set; }

		[BsonElement("nic")]
		public string NIC { get; set; }

		[BsonElement("name")]
		public string Name { get; set; }

		[BsonElement("phone")]
		public string Phone { get; set; }

		[BsonElement("email")]
		public string Email { get; set; }

		[BsonElement("isActive")]
		public bool IsActive { get; set; } = true;
	}
}
