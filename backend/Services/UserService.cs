// File: UserService.cs
// Description: Business logic for user CRUD and activation handling.
using Backend.Models;
using Backend.Repositories;
using Backend.Utils;

namespace Backend.Services
{
    public class UserService : IUserService
    {
        private readonly IUserRepository _repo;

        public UserService(IUserRepository repo)
        {
            _repo = repo;
        }

        public Task<List<User>> GetAllAsync() => _repo.GetAllAsync();
        public Task<User?> GetByNicAsync(string nic) => _repo.GetByNicAsync(nic);

        public async Task<User> CreateAsync(User user)
        {
            // PasswordHash should already be hashed by the controller
            user.IsActive = true;
            return await _repo.CreateAsync(user);
        }

        public async Task<User?> UpdateByNicAsync(string nic, User update)
        {
            var existing = await _repo.GetByNicAsync(nic);
            if (existing is null) return null;

            update.Id = existing.Id;
            update.NIC = existing.NIC; // NIC immutable

            // Only update password if provided. The controller currently provides a hashed PasswordHash
            // so do not re-hash here to avoid double-hashing. If no password supplied, keep existing hash.
            if (!string.IsNullOrEmpty(update.PasswordHash))
            {
                // assume already hashed by caller
            }
            else
            {
                update.PasswordHash = existing.PasswordHash;
            }

            return await _repo.UpdateByNicAsync(nic, update);
        }

        public async Task<User?> SetStatusAsync(string nic, bool isActive)
        {
            var existing = await _repo.GetByNicAsync(nic);
            if (existing is null) return null;

            existing.IsActive = isActive;
            return await _repo.UpdateByNicAsync(nic, existing);
        }

        public Task<long> CountAsync() => _repo.CountAsync();
        public Task<bool> DeleteByNicAsync(string nic) => _repo.DeleteByNicAsync(nic);
    }
}
