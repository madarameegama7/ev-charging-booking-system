// File: AuthController.cs
// Description: Issues JWT tokens for authenticated users
// ------------------------------------------------------------
using Backend.Models;
using Backend.Services;
using Microsoft.AspNetCore.Mvc;

namespace Backend.Controllers
{
	[ApiController]
	[Route("api/[controller]")]
	public class AuthController : ControllerBase
	{
		private readonly IUserService _userService;
		private readonly ITokenService _tokenService;

		public AuthController(IUserService userService, ITokenService tokenService)
		{
			_userService = userService;
			_tokenService = tokenService;
		}

		/// <summary>Simple login by NIC and role for demo; replace with password later if needed.</summary>
		[HttpPost("login")]
		public async Task<IActionResult> Login([FromBody] User user)
		{
			var existing = await _userService.GetByNicAsync(user.NIC);
			if (existing is null || !existing.IsActive) return Unauthorized();
			// Optional: check role matches
			if (!string.IsNullOrWhiteSpace(user.Role) && !string.Equals(existing.Role, user.Role, StringComparison.OrdinalIgnoreCase))
			{
				return Unauthorized();
			}
			var token = _tokenService.GenerateToken(existing);
			return Ok(new { token, role = existing.Role, nic = existing.NIC });
		}
	}
}


