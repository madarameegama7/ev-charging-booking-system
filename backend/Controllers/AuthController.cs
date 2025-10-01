// File: AuthController.cs
// Description: Issues JWT tokens for authenticated users
using Backend.Models;
using Backend.Services;
using Backend.Utils;
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

        // Register new user
        [HttpPost("register")]
        public async Task<IActionResult> Register([FromBody] User user)
        {
            var existing = await _userService.GetByNicAsync(user.NIC);
            if (existing != null) return Conflict("User already exists");

            var created = await _userService.CreateAsync(user);
            return Ok(new { message = "User registered successfully", nic = created.NIC });
        }

        // Login with NIC + Password
        [HttpPost("login")]
        public async Task<IActionResult> Login([FromBody] LoginRequest request)
        {
            var existing = await _userService.GetByNicAsync(request.NIC);
            if (existing is null || !existing.IsActive) return Unauthorized("Invalid NIC or inactive account");

            // Verify password
            if (!PasswordHelper.VerifyPassword(request.Password, existing.PasswordHash))
                return Unauthorized("Invalid password");

            var token = _tokenService.GenerateToken(existing);
            return Ok(new { token, role = existing.Role, nic = existing.NIC });
        }
    }

    // DTO for login
    public class LoginRequest
    {
        public string NIC { get; set; }
        public string Password { get; set; }
    }
}
