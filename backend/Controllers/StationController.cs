// File: StationController.cs
// Description: Web API controller for station management CRUD and activation
using Backend.Models;
using Backend.Services;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Authorization;

namespace Backend.Controllers
{
	[ApiController]
	[Route("api/[controller]")]
	public class StationController : ControllerBase
	{
		private readonly IStationService _service;
		public StationController(IStationService service) { _service = service; }

		[HttpGet]
		[AllowAnonymous]
		public async Task<IActionResult> GetAll() => Ok(await _service.GetAllAsync());

		[HttpGet("{id}")]
		[AllowAnonymous]
		public async Task<IActionResult> GetById(string id)
		{
			var s = await _service.GetByIdAsync(id);
			return s is null ? NotFound() : Ok(s);
		}

		[HttpPost]
		[Authorize(Roles = "Backoffice")]
		public async Task<IActionResult> Create([FromBody] Station station)
		{
			var created = await _service.CreateAsync(station);
			return CreatedAtAction(nameof(GetById), new { id = created.Id }, created);
		}

		[HttpPut("{id}")]
		[Authorize(Roles = "Backoffice,Operator")]
		public async Task<IActionResult> Update(string id, [FromBody] Station update)
		{
			var updated = await _service.UpdateAsync(id, update);
			return updated is null ? NotFound() : Ok(updated);
		}

		[HttpPatch("{id}/status")]
		[Authorize(Roles = "Backoffice")]
		public async Task<IActionResult> SetActive(string id, [FromQuery] bool isActive)
		{
			var ok = await _service.SetActiveAsync(id, isActive);
			return ok ? Ok() : Conflict("Cannot deactivate station with active bookings or station not found.");
		}
	}
}


