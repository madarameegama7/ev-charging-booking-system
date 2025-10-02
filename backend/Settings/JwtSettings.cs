// File: JwtSettings.cs
// Description: Strongly typed configuration for JWT authentication.

namespace Backend.Settings
{
	public class JwtSettings
	{
		public string Issuer { get; set; } = string.Empty;
		public string Audience { get; set; } = string.Empty;
		public string Key { get; set; } = string.Empty;
		public int ExpiresMinutes { get; set; } = 120;
	}
}


