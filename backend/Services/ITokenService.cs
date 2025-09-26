// File: ITokenService.cs
// Description: Token service contract.
using Backend.Models;

namespace Backend.Services
{
	public interface ITokenService
	{
		string GenerateToken(User user);
	}
}


