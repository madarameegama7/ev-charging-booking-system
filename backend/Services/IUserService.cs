// File: IUserService.cs
// Description: User service contract.
using Backend.Models;

namespace Backend.Services
{
	public interface IUserService
	{
		Task<List<User>> GetAllAsync();
		Task<User?> GetByNicAsync(string nic);
		Task<User> CreateAsync(User user);
		Task<User?> UpdateByNicAsync(string nic, User update);
		Task<User?> SetStatusAsync(string nic, bool isActive);
		Task<long> CountAsync();
	}
}


