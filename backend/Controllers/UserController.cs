// File: UserController.cs
// Description: Web API controller for managing users
// ------------------------------------------------------------
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Backend.Models;
using Backend.Services;
using MongoDB.Driver;

namespace Backend.Controllers
{
	/// <summary>
	/// Manages users across roles. Role checks will be added after JWT integration.
	/// </summary>
	[ApiController]
	[Route("api/[controller]")]
	public class UserController : ControllerBase
	{
		private readonly IUserService _userService;

		public UserController(IUserService userService)
		{
			_userService = userService;
		}

		/// <summary>Fetch all users (Backoffice only later).</summary>
		[HttpGet]
		[Authorize(Roles = "Backoffice")]
		public async Task<IActionResult> GetUsers()
		{
			var users = await _userService.GetAllAsync();
			return Ok(users);
		}


		/// <summary>
		/// Create user with NIC and role. Bootstrap rule: if no users exist yet,
		/// allow anonymous creation but only for a Backoffice user. Otherwise requires Backoffice role.
		/// </summary>
		[HttpPost]
		public async Task<IActionResult> Create([FromBody] User user)
		{
			var count = await _userService.CountAsync();
			
			// Bootstrap: if no users exist, allow creation of first Backoffice user
			if (count == 0)
			{
				if (!string.Equals(user.Role, "Backoffice", StringComparison.OrdinalIgnoreCase))
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
				var created = await _userService.CreateAsync(user);
				return CreatedAtAction(nameof(GetByNic), new { nic = created.NIC }, created);
			}
			catch (MongoWriteException ex) when (ex.WriteError?.Category == ServerErrorCategory.DuplicateKey)
			{
				return Conflict("A user with this NIC already exists.");
			}
		}

		/// <summary>Get by NIC.</summary>
		[HttpGet("{nic}")]
		[Authorize(Roles = "Backoffice,Owner,Operator")]
		public async Task<IActionResult> GetByNic(string nic)
		{
			var user = await _userService.GetByNicAsync(nic);
			return user is null ? NotFound() : Ok(user);
		}

		/// <summary>Update by NIC (Backoffice or Owner self later).</summary>
		[HttpPut("{nic}")]
		[Authorize(Roles = "Backoffice,Owner")]
		public async Task<IActionResult> Update(string nic, [FromBody] User update)
		{
			var updated = await _userService.UpdateByNicAsync(nic, update);
			return updated is null ? NotFound() : Ok(updated);
		}

		/// <summary>Activate/Deactivate by NIC.</summary>
		[HttpPatch("{nic}/status")]
		[Authorize(Roles = "Backoffice")]
		public async Task<IActionResult> SetStatus(string nic, [FromQuery] bool isActive)
		{
			var updated = await _userService.SetStatusAsync(nic, isActive);
			return updated is null ? NotFound() : Ok(updated);
		}
	}
}
