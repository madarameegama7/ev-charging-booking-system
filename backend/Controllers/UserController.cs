// File: UserController.cs
// Description: Web API controller for managing users

using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Backend.Models;
using Backend.Services;
using MongoDB.Driver;
using Backend.Utils;

namespace Backend.Controllers
{
	
	//Manages users across roles. Role checks will be added after JWT integration
	[ApiController]
	[Route("api/[controller]")]
	public class UserController : ControllerBase
	{
		private readonly IUserService _userService;

		public UserController(IUserService userService)
		{
			_userService = userService;
		}

		//Fetch all users (Backoffice only)
		[HttpGet]
		[Authorize(Roles = "Backoffice")]
		public async Task<IActionResult> GetUsers()
		{
			var users = await _userService.GetAllAsync();
			return Ok(users);
		}


		//Create user with NIC and role. Bootstrap rule: if no users exist yet,
		//allow anonymous creation but only for a Backoffice user. Otherwise requires Backoffice role
		public class CreateUserRequest
		{
			public string? NIC { get; set; }
			public string? Role { get; set; }
			public bool IsActive { get; set; } = true;
			public string? Password { get; set; }
			public string? FirstName { get; set; }
			public string? LastName { get; set; }
			public string? Email { get; set; }
			public string? Phone { get; set; }
		}

		[HttpPost]
		public async Task<IActionResult> Create([FromBody] CreateUserRequest req)
		{
			var count = await _userService.CountAsync();
			
			// Bootstrap: if no users exist, allow creation of first Backoffice user
			if (count == 0)
			{
				if (!string.Equals(req.Role ?? string.Empty, "Backoffice", StringComparison.OrdinalIgnoreCase))
				{
					return BadRequest("First account must be a Backoffice user.");
				}
			}
			else
			{
				// After bootstrap, require Backoffice role
				var isAuth = User.Identity != null && User.Identity.IsAuthenticated;
				if (!isAuth || !User.IsInRole("Backoffice"))
				{
					return StatusCode(403, "Only Backoffice users can create new users.");
				}
			}

			try
			{
				// Validate minimal fields
				if (string.IsNullOrWhiteSpace(req.NIC) || string.IsNullOrWhiteSpace(req.Role))
					return BadRequest("NIC and Role are required.");

				// Map to User model
				var newUser = new User
				{
					NIC = req.NIC,
					Name = string.IsNullOrWhiteSpace(req.FirstName) && string.IsNullOrWhiteSpace(req.LastName)
						? ""
						: $"{req.FirstName ?? ""} {req.LastName ?? ""}".Trim(),
					Email = req.Email ?? "",
					Phone = req.Phone ?? "",
					Role = req.Role,
					IsActive = req.IsActive
				};

				// Handle password: if missing, generate a temporary one
				string? tempPassword = null;
				if (string.IsNullOrWhiteSpace(req.Password))
				{
					tempPassword = System.Guid.NewGuid().ToString("N").Substring(0, 10);
					newUser.PasswordHash = PasswordHelper.HashPassword(tempPassword);
					newUser.ForcePasswordChange = true;
				}
				else
				{
					newUser.PasswordHash = PasswordHelper.HashPassword(req.Password);
				}

				var created = await _userService.CreateAsync(newUser);

				// Return created user and expose temp password if generated
				return CreatedAtAction(nameof(GetByNic), new { nic = created.NIC }, new { created, tempPassword });
			}
			catch (MongoWriteException ex) when (ex.WriteError?.Category == ServerErrorCategory.DuplicateKey)
			{
				return Conflict("A user with this NIC already exists.");
			}
		}

		//Get by NIC
		[HttpGet("{nic}")]
		[Authorize(Roles = "Backoffice,Owner,Operator")]
		public async Task<IActionResult> GetByNic(string nic)
		{
			var user = await _userService.GetByNicAsync(nic);
			return user is null ? NotFound() : Ok(user);
		}

		//Update by NIC (Backoffice or Owner)
		[HttpPut("{nic}")]
		[Authorize(Roles = "Backoffice,Owner")]
		public async Task<IActionResult> Update(string nic, [FromBody] User update)
		{
			var updated = await _userService.UpdateByNicAsync(nic, update);
			return updated is null ? NotFound() : Ok(updated);
		}

		// Change password (Owner/Operator/Backoffice). Owner/Operator can change their own password; Backoffice can change any.
		[HttpPost("{nic}/password")]
		[Authorize(Roles = "Owner,Operator,Backoffice")]
		public async Task<IActionResult> ChangePassword(string nic, [FromBody] ChangePasswordRequest req)
		{
			// Only allow if the caller is Backoffice or the same nic
			var isBackoffice = User.IsInRole("Backoffice");
			var callerNic = User.FindFirst("nic")?.Value;
			if (!isBackoffice && !string.Equals(callerNic, nic, StringComparison.OrdinalIgnoreCase))
				return Forbid();

			if (string.IsNullOrWhiteSpace(req.NewPassword)) return BadRequest("NewPassword is required");

			var existing = await _userService.GetByNicAsync(nic);
			if (existing is null) return NotFound();

			existing.PasswordHash = PasswordHelper.HashPassword(req.NewPassword);
			existing.ForcePasswordChange = false;

			var updated = await _userService.UpdateByNicAsync(nic, existing);
			return Ok(new { message = "Password updated successfully" });
		}

		public class ChangePasswordRequest { public string? NewPassword { get; set; } }

		//Activate/Deactivate by NIC
		[HttpPatch("{nic}/status")]
		[Authorize(Roles = "Backoffice")]
		public async Task<IActionResult> SetStatus(string nic, [FromQuery] bool isActive)
		{
			var updated = await _userService.SetStatusAsync(nic, isActive);
			return updated is null ? NotFound() : Ok(updated);
		}

		//Delete user by NIC
		[HttpDelete("{nic}")]
		[Authorize(Roles = "Backoffice")]
		public async Task<IActionResult> Delete(string nic)
		{
			var ok = await _userService.DeleteByNicAsync(nic);
			return ok ? NoContent() : NotFound();
		}

		// Get user profile by NIC
        [HttpGet("profile/{nic}")]
        [Authorize(Roles = "Owner,Operator,Backoffice")]
        public async Task<IActionResult> GetProfile(string nic)
        {
            var user = await _userService.GetByNicAsync(nic);
            if (user == null) return NotFound("User not found.");

            // Return minimal fields for profile display
            return Ok(new
            {
                nic = user.NIC,
                name = user.Name,
                email = user.Email,
                phone = user.Phone,
                isActive = user.IsActive
            });
        }

		// Update user profile
        [HttpPut("profile/{nic}")]
        [Authorize(Roles = "Owner")]
        public async Task<IActionResult> UpdateProfile(string nic, [FromBody] User update)
        {
            var existing = await _userService.GetByNicAsync(nic);
            if (existing == null) return NotFound("User not found.");

            // Only allow updating name, email, and phone
            existing.Name = update.Name ?? existing.Name;
            existing.Email = update.Email ?? existing.Email;
            existing.Phone = update.Phone ?? existing.Phone;

			var updatedUser = await _userService.UpdateByNicAsync(nic, existing);
			return Ok(new { message = "Profile updated successfully", updated = updatedUser });
        }

		// Deactivate user account
        [HttpPut("profile/{nic}/deactivate")]
        [Authorize(Roles = "Owner")]
        public async Task<IActionResult> DeactivateAccount(string nic)
        {
            var existing = await _userService.GetByNicAsync(nic);
            if (existing == null) return NotFound("User not found.");

            existing.IsActive = false;
			await _userService.UpdateByNicAsync(nic, existing);

			return Ok(new { message = "Account deactivated successfully" });
        }
	}
}
