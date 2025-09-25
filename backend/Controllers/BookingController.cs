// File: BookingController.cs
// Description: Web API controller for booking reservations and updates
// ------------------------------------------------------------
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

		[HttpPost]
		[Authorize(Roles = "Owner,Operator,Backoffice")]
		public async Task<IActionResult> Create([FromBody] Booking booking)
		{
			var created = await _service.CreateAsync(booking);
			return CreatedAtAction(nameof(GetById), new { id = created.Id }, created);
		}

		[HttpGet("{id}")]
		[Authorize(Roles = "Owner,Operator,Backoffice")]
		public async Task<IActionResult> GetById(string id)
		{
			var b = await _service.GetByIdAsync(id);
			return b is null ? NotFound() : Ok(b);
		}

		[HttpGet("owner/{nic}")]
		[Authorize(Roles = "Owner,Operator,Backoffice")]
		public async Task<IActionResult> GetByOwner(string nic) => Ok(await _service.GetByOwnerAsync(nic));

		[HttpGet("station/{stationId}")]
		[Authorize(Roles = "Operator,Backoffice")]
		public async Task<IActionResult> GetByStation(string stationId) => Ok(await _service.GetByStationAsync(stationId));

		[HttpPut("{id}")]
		[Authorize(Roles = "Owner,Operator,Backoffice")]
		public async Task<IActionResult> Update(string id, [FromBody] Booking update)
		{
			var updated = await _service.UpdateAsync(id, update);
			return updated is null ? NotFound() : Ok(updated);
		}
	}
}


