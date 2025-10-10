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
            if (request.Role != "Backoffice" && request.Role != "Operator" && request.Role != "Owner")
            {
                return BadRequest("Role must be either 'Backoffice', 'Operator', or 'Owner'.");
            }

            // Check if user already exists
            var existing = await _userService.GetByNicAsync(request.NIC);
            if (existing != null)
                return Conflict("A user with this NIC already exists.");

            // Create new user with selected role


            // Debug: Log password and hash, check for null/empty
            var password = request.Password;
            if (string.IsNullOrEmpty(password))
            {
                Console.WriteLine("[DEBUG][Register] WARNING: Password is null or empty!");
            }
            else
            {
                Console.WriteLine($"[DEBUG][Register] Password (raw): '{password}' (Length: {password.Length})");
            }
            var passwordHash = PasswordHelper.HashPassword(password);
            Console.WriteLine($"[DEBUG][Register] PasswordHash: {passwordHash}");

            var newUser = new User
            {
                NIC = request.NIC,
                Name = $"{request.FirstName} {request.LastName}",
                Email = request.Email,
                Phone = request.Phone ?? "",
                Role = request.Role, // Use role from request
                IsActive = true,
                PasswordHash = passwordHash
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
                    firstName = request.FirstName, 
                    lastName = request.LastName,
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



            // Debug: Log password and hash, check for null/empty
            var inputPassword = request.Password;
            if (string.IsNullOrEmpty(inputPassword))
            {
                Console.WriteLine("[DEBUG][Login] WARNING: Input Password is null or empty!");
            }
            else
            {
                Console.WriteLine($"[DEBUG][Login] Input Password (raw): '{inputPassword}' (Length: {inputPassword.Length})");
            }
            Console.WriteLine($"[DEBUG][Login] Stored Hash: {existing.PasswordHash}");
            var verifyResult = PasswordHelper.VerifyPassword(inputPassword, existing.PasswordHash);
            Console.WriteLine($"[DEBUG][Login] Verify Result: {verifyResult}");

            if (!verifyResult)
                return Unauthorized("Invalid password");

            var token = _tokenService.GenerateToken(existing);

            // Split the name into first and last name
            var nameParts = existing.Name?.Split(' ', 2) ?? new string[] { "", "" };
            var firstName = nameParts.Length > 0 ? nameParts[0] : "";
            var lastName = nameParts.Length > 1 ? nameParts[1] : "";

            return Ok(new { token, role = existing.Role, nic = existing.NIC, firstName = firstName, lastName = lastName, forcePasswordChange = existing.ForcePasswordChange });
        }
    }

    public class LoginRequest
    {
        public string NIC { get; set; }
        public string Password { get; set; }
    }
}