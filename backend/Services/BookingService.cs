// File: BookingService.cs
using Backend.Models;
using Backend.Repositories;

namespace Backend.Services
{
    public class BookingService : IBookingService
    {
        private readonly IBookingRepository _repo;
        private readonly IStationRepository _stationRepo;
        
        public BookingService(IBookingRepository repo, IStationRepository stationRepo)
        {
            _repo = repo;
            _stationRepo = stationRepo;
        }

        public async Task<Booking> CreateAsync(Booking booking)
        {
            try
            {
                Console.WriteLine($"[BookingService] Creating booking:");
                Console.WriteLine($"[BookingService] StationId: {booking.StationId}");
                Console.WriteLine($"[BookingService] OwnerNIC: {booking.OwnerNIC}");
                Console.WriteLine($"[BookingService] StartTimeUtc: {booking.StartTimeUtc}");
                Console.WriteLine($"[BookingService] EndTimeUtc: {booking.EndTimeUtc}");

                // Convert UTC to local for validation
                var startLocal = booking.StartTimeUtc.ToLocalTime();
                var endLocal = booking.EndTimeUtc.ToLocalTime();
                var nowLocal = DateTime.Now;

                Console.WriteLine($"[BookingService] Start (local): {startLocal}");
                Console.WriteLine($"[BookingService] End (local): {endLocal}");
                Console.WriteLine($"[BookingService] Current (local): {nowLocal}");

                // Validation: at least 1 hour from now
                if (startLocal < nowLocal.AddHours(1)) 
                {
                    var msg = $"Start time must be at least 1 hour from now. Selected: {startLocal}, Current: {nowLocal}";
                    Console.WriteLine($"[BookingService] Validation failed: {msg}");
                    throw new ArgumentException(msg);
                }

                // Validation: within 7 days
                if (startLocal > nowLocal.AddDays(7)) 
                {
                    Console.WriteLine($"[BookingService] Validation failed: Start time beyond 7 days");
                    throw new ArgumentException("Start time must be within 7 days.");
                }

                // Validation: end after start
                if (endLocal <= startLocal) 
                {
                    Console.WriteLine($"[BookingService] Validation failed: End time before start time");
                    throw new ArgumentException("End must be after start.");
                }

                // Ensure station exists and is active
                var station = await _stationRepo.GetByStationIdAsync(booking.StationId);
                if (station is null || !station.IsActive) 
                {
                    Console.WriteLine($"[BookingService] Validation failed: Invalid or inactive station");
                    throw new ArgumentException("Invalid or inactive station.");
                }

                // Set Id to null for MongoDB auto-generation
                booking.Id = null;

                var result = await _repo.CreateAsync(booking);
                Console.WriteLine($"[BookingService] Booking created successfully: {result.BookingId}");
                return result;
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[BookingService] Error creating booking: {ex.Message}");
                Console.WriteLine($"[BookingService] StackTrace: {ex.StackTrace}");
                throw;
            }
        }

        // ... rest of your methods (GetByIdAsync, GetByOwnerAsync, etc.)
        
        public Task<List<Booking>> GetAllAsync() => _repo.GetAllAsync();
        public Task<Booking?> GetByIdAsync(string id) => _repo.GetByIdAsync(id);
        public Task<List<Booking>> GetByOwnerAsync(string nic) => _repo.GetByOwnerAsync(nic);
        
        public async Task<List<Booking>> GetByStationAsync(string stationId)
        {
            var station = await _stationRepo.GetByStationIdAsync(stationId);
            var stationName = station?.Name;
            return await _repo.GetByStationLooseAsync(stationId, stationName);
        }

        public async Task<Booking?> UpdateAsync(string id, Booking update)
        {
            var existing = await _repo.GetByIdAsync(id);
            if (existing is null) 
            {
                Console.WriteLine($"[BookingService] Update failed: Booking {id} not found");
                return null;
            }
            
            try
            {
                var startLocal = update.StartTimeUtc.ToLocalTime();
                var endLocal = update.EndTimeUtc.ToLocalTime();
                var nowLocal = DateTime.Now;

                // Update rules: modify/cancel at least 12 hours before reservation start
                var existingStartLocal = existing.StartTimeUtc.ToLocalTime();
                if (existingStartLocal <= nowLocal.AddHours(12)) 
                {
                    Console.WriteLine($"[BookingService] Update failed: Too close to start time");
                    throw new InvalidOperationException("Updates must be at least 12 hours before start.");
                }

                // Validation: within 7 days
                if (startLocal > nowLocal.AddDays(7)) 
                {
                    Console.WriteLine($"[BookingService] Update failed: Start time beyond 7 days");
                    throw new ArgumentException("Start time must be within 7 days.");
                }

                // Validation: end after start
                if (endLocal <= startLocal) 
                {
                    Console.WriteLine($"[BookingService] Update failed: End time before start time");
                    throw new ArgumentException("End must be after start.");
                }

                update.Id = existing.Id;
                update.OwnerNIC = existing.OwnerNIC;
                
                var result = await _repo.UpdateAsync(id, update);
                Console.WriteLine($"[BookingService] Booking updated successfully: {result.BookingId}");
                return result;
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[BookingService] Error updating booking: {ex.Message}");
                throw;
            }
        }
    }
}