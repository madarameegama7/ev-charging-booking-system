// File: User.cs
// Description: User entity representing Backoffice, Operator, or Owner
// ------------------------------------------------------------
using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;

namespace Backend.Models
{
	
	/// Represents a system user (Backoffice, Operator, or EV Owner). NIC serves as domain identifier
	public class User
	{
		[BsonId]
		[BsonRepresentation(BsonType.ObjectId)]
		public string? Id { get; set; }

		/// <summary>National Identity Card, acts as natural unique key for owners.</summary>
		[BsonElement("nic")]
		public string NIC { get; set; }

		/// <summary>User role: Backoffice | Operator | Owner</summary>
		[BsonElement("role")]
		public string Role { get; set; }

		/// <summary>Account activation status.</summary>
		[BsonElement("isActive")]
		public bool IsActive { get; set; } = true;
	}
}
