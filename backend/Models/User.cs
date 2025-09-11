using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;

namespace Backend.Models
{
    public class User
    {
        [BsonId]
        [BsonRepresentation(BsonType.ObjectId)]
        public string? Id { get; set; }

        public string NIC { get; set; }
        public string Role { get; set; } // Backoffice, Operator, Owner
        public bool IsActive { get; set; }
    }
}
