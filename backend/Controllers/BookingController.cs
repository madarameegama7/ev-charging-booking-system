// File: BookingController.cs
// Description: Web API controller for booking reservations and updates

using Backend.Models;
using Backend.Services;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Authorization;

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
		public async Task<IActionResult> Create([FromBody] Booking booking)
		{
			try
			{
				var result = await _service.CreateAsync(booking);
				return Ok(result); // returns JSON
			}
			catch (ArgumentException ex)
			{
				// Return structured JSON error instead of plain text
				return BadRequest(new { error = ex.Message });
			}
			catch (Exception ex)
			{
				// General server error
				return StatusCode(500, new { error = "Internal server error" });
			}
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
