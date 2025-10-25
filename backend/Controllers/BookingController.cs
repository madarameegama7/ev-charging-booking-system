// File: BookingController.cs
// Description: Web API controller for booking reservations and updates

using Backend.Models;
using Backend.Services;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Authorization;
using System.Text.Json;

namespace Backend.Controllers
{
	[ApiController]
	[Route("api/[controller]")]
	public class BookingController : ControllerBase
	{
		private readonly IBookingService _service;
		public BookingController(IBookingService service) { _service = service; }

		//add booking
		[HttpPost]
		[Authorize(Roles = "Owner,Operator,Backoffice")]


		[HttpPost]
		public async Task<IActionResult> Create([FromBody] CreateBookingDto dto)
		{
			try
			{
				Console.WriteLine($"[BookingController] Received booking request at: {DateTime.Now}");

				if (dto == null)
				{
					Console.WriteLine($"[BookingController] ERROR: DTO is null");
					return BadRequest("Request body is null");
				}

				Console.WriteLine($"[BookingController] StationId: {dto.StationId}");
				Console.WriteLine($"[BookingController] OwnerNIC: {dto.OwnerNIC}");
				Console.WriteLine($"[BookingController] StartTime: {dto.StartTime}");
				Console.WriteLine($"[BookingController] EndTime: {dto.EndTime}");

				// Validate required fields
				if (string.IsNullOrEmpty(dto.StationId))
				{
					Console.WriteLine($"[BookingController] ERROR: StationId is null or empty");
					return BadRequest("StationId is required");
				}

				if (string.IsNullOrEmpty(dto.OwnerNIC))
				{
					Console.WriteLine($"[BookingController] ERROR: OwnerNIC is null or empty");
					return BadRequest("OwnerNIC is required");
				}

				if (string.IsNullOrEmpty(dto.StartTime))
				{
					Console.WriteLine($"[BookingController] ERROR: StartTime is null or empty");
					return BadRequest("StartTime is required");
				}

				if (string.IsNullOrEmpty(dto.EndTime))
				{
					Console.WriteLine($"[BookingController] ERROR: EndTime is null or empty");
					return BadRequest("EndTime is required");
				}

				// Convert DTO to Booking entity
				var booking = new Booking
				{
					StationId = dto.StationId,
					OwnerNIC = dto.OwnerNIC
				};

				// Set times through properties (this will do the UTC conversion)
				try
				{
					booking.StartTime = dto.StartTime;
					booking.EndTime = dto.EndTime;
				}
				catch (ArgumentException ex)
				{
					Console.WriteLine($"[BookingController] ERROR: Invalid time format - {ex.Message}");
					return BadRequest($"Invalid time format: {ex.Message}");
				}

				Console.WriteLine($"[BookingController] Converted times - StartTimeUtc: {booking.StartTimeUtc}, EndTimeUtc: {booking.EndTimeUtc}");

				var result = await _service.CreateAsync(booking);

				Console.WriteLine($"[BookingController] Booking created successfully: {result.BookingId}");
				return Ok(result);
			}
			catch (Exception ex)
			{
				Console.WriteLine($"[BookingController] UNHANDLED EXCEPTION: {ex.Message}");
				Console.WriteLine($"[BookingController] StackTrace: {ex.StackTrace}");

				// Return proper error response
				return StatusCode(500, new { error = ex.Message, details = ex.StackTrace });
			}
		}

        [HttpPost("test")]
public IActionResult Test([FromBody] object testData)
{
    Console.WriteLine($"[BookingController] Test endpoint hit at: {DateTime.Now}");
    Console.WriteLine($"[BookingController] Test data: {JsonSerializer.Serialize(testData)}");
    return Ok(new { 
        message = "Backend is working", 
        received = testData,
        timestamp = DateTime.Now
    });
}
		//get all bookings
		[HttpGet]
		[Authorize(Roles = "Backoffice,Operator")]
		public async Task<ActionResult<List<Booking>>> GetAll()
		{
			var bookings = await _service.GetAllAsync();
			return Ok(bookings);
		}
		//get booking by id - owner, operator, backoffice
		[HttpGet("{id}")]
		[Authorize(Roles = "Owner,Operator,Backoffice")]
		public async Task<IActionResult> GetById(string id)
		{
			var b = await _service.GetByIdAsync(id);
			return b is null ? NotFound() : Ok(b);
		}

		//get bookings by owner - owner, operator, backoffice
		[HttpGet("owner/{nic}")]

		public async Task<IActionResult> GetByOwner(string nic) => Ok(await _service.GetByOwnerAsync(nic));

		//get bookings by station - public
		[HttpGet("station/{stationId}")]
		[AllowAnonymous] // or [Authorize] if needed
		public async Task<IActionResult> GetByStation(string stationId)
		{
			var bookings = await _service.GetByStationAsync(stationId);
			return Ok(bookings);
		}
		//update booking - owner, operator, backoffice
		[HttpPut("{id}")]
		[Authorize(Roles = "Owner,Operator,Backoffice")]
		public async Task<IActionResult> Update(string id, [FromBody] Booking update)
		{
			var updated = await _service.UpdateAsync(id, update);
			return updated is null ? NotFound() : Ok(updated);
		}
	}
}
