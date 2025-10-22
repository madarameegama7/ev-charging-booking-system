// File: User.cs
// Description: User entity representing Backoffice, Operator, or Owner

using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;

namespace Backend.Models
{

	// Represents a system user (Backoffice, Operator, or EV Owner). NIC serves as primary identifier
	public class User
	{
		[BsonId]
		[BsonRepresentation(BsonType.ObjectId)]
		public string? Id { get; set; }

		[BsonElement("nic")]
		public string NIC { get; set; }

        [BsonElement("name")]
        public string Name { get; set; }

        [BsonElement("email")]
        public string Email { get; set; }

        [BsonElement("phone")]
        public string Phone { get; set; }


        [BsonElement("role")]
		public string Role { get; set; }

		[BsonElement("isActive")]
		public bool IsActive { get; set; } = true;

		[BsonElement("passwordHash")]
		public string PasswordHash { get; set; }

		[BsonElement("forcePasswordChange")]
		public bool ForcePasswordChange { get; set; } = false;
	}
}
