EV Charging Booking Backend

Run locally

1. Install MongoDB and start it on mongodb://localhost:27017
2. Update `appsettings.json` `Jwt:Key` with a secure 32+ char string
3. dotnet restore
4. dotnet run

Primary endpoints

- POST /api/Auth/login -> returns JWT for a valid active user (by NIC)
- POST /api/User (Backoffice)
- GET /api/User (Backoffice)
- PATCH /api/User/{nic}/status?isActive= (Backoffice)
- POST /api/Station (Backoffice)
- PATCH /api/Station/{id}/status?isActive= (Backoffice)
- POST /api/Booking (Owner/Operator/Backoffice)
- PUT /api/Booking/{id} (Owner/Operator/Backoffice, 12h rule)

Use `backend.http` for examples.

