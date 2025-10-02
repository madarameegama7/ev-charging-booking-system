using backend.Models;
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
        public async Task<IActionResult> Register([FromBody] RegisterRequest request)
        {
            // Validate input
            if (string.IsNullOrWhiteSpace(request.NIC) ||
                string.IsNullOrWhiteSpace(request.Password) ||
                string.IsNullOrWhiteSpace(request.Email) ||
                string.IsNullOrWhiteSpace(request.Role))
            {
                return BadRequest("NIC, Email, Password, and Role are required.");
            }

            // Validate role is either Backoffice or Operator
            if (request.Role != "Backoffice" && request.Role != "Operator")
            {
                return BadRequest("Role must be either 'Backoffice' or 'Operator'.");
            }

            // Check if user already exists
            var existing = await _userService.GetByNicAsync(request.NIC);
            if (existing != null)
                return Conflict("A user with this NIC already exists.");

            // Create new user with selected role
            var newUser = new User
            {
                NIC = request.NIC,
                Name = $"{request.FirstName} {request.LastName}",
                Email = request.Email,
                Phone = request.Phone ?? "",
                Role = request.Role, // Use role from request
                IsActive = true,
                PasswordHash = PasswordHelper.HashPassword(request.Password)
            };

            try
            {
                var created = await _userService.CreateAsync(newUser);

                // Generate token
                var token = _tokenService.GenerateToken(created);

                return Ok(new
                {
                    token,
                    role = created.Role,
                    nic = created.NIC,
                    message = "Account created successfully"
                });
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"An error occurred during registration: {ex.Message}");
            }
        }

        // Login with NIC + Password
        [HttpPost("login")]
        public async Task<IActionResult> Login([FromBody] LoginRequest request)
        {
            var existing = await _userService.GetByNicAsync(request.NIC);
            if (existing is null || !existing.IsActive)
                return Unauthorized("Invalid NIC or inactive account");

            // Verify password
            if (!PasswordHelper.VerifyPassword(request.Password, existing.PasswordHash))
                return Unauthorized("Invalid password");

            var token = _tokenService.GenerateToken(existing);
            return Ok(new { token, role = existing.Role, nic = existing.NIC });
        }
    }

    public class LoginRequest
    {
        public string NIC { get; set; }
        public string Password { get; set; }
    }
}