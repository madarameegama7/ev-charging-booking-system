// File: IUserRepository.cs
// Description: Repository interface for users.

using Backend.Models;

namespace Backend.Repositories
{
	public interface IUserRepository
	{
		Task<List<User>> GetAllAsync();
		Task<User?> GetByNicAsync(string nic);
		Task<User> CreateAsync(User user);
		Task<User?> UpdateByNicAsync(string nic, User update);
		Task<long> CountAsync();
		Task<bool> DeleteByNicAsync(string nic);
	}
}


