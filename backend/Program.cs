// File: Program.cs
// Description: ASP.NET Core app bootstrap, DI, Swagger, CORS, and JWT auth setup.
var builder = WebApplication.CreateBuilder(args);

// Add services to the container.

builder.Services.AddControllers();
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

// CORS for web and mobile apps
builder.Services.AddCors(options =>
{
	options.AddPolicy("AllowAll", policy =>
	{
		policy.AllowAnyOrigin().AllowAnyHeader().AllowAnyMethod();
	});
});

// MongoDB settings and DI
builder.Services.Configure<Backend.Settings.MongoDBSettings>(
	builder.Configuration.GetSection("MongoDBSettings"));
builder.Services.Configure<Backend.Settings.JwtSettings>(
	builder.Configuration.GetSection("Jwt"));
builder.Services.AddSingleton<Backend.Data.MongoContext>();
builder.Services.AddScoped<Backend.Repositories.IUserRepository, Backend.Repositories.UserRepository>();
builder.Services.AddScoped<Backend.Services.IUserService, Backend.Services.UserService>();
builder.Services.AddSingleton<Backend.Services.ITokenService, Backend.Services.TokenService>();
builder.Services.AddScoped<Backend.Repositories.IStationRepository, Backend.Repositories.StationRepository>();
builder.Services.AddScoped<Backend.Services.IStationService, Backend.Services.StationService>();
builder.Services.AddScoped<Backend.Repositories.IBookingRepository, Backend.Repositories.BookingRepository>();
builder.Services.AddScoped<Backend.Services.IBookingService, Backend.Services.BookingService>();

// JWT Auth setup
builder.Services
	.AddAuthentication(options =>
	{
		options.DefaultAuthenticateScheme = Microsoft.AspNetCore.Authentication.JwtBearer.JwtBearerDefaults.AuthenticationScheme;
		options.DefaultChallengeScheme = Microsoft.AspNetCore.Authentication.JwtBearer.JwtBearerDefaults.AuthenticationScheme;
	})
	.AddJwtBearer(options =>
	{
		var cfg = builder.Configuration.GetSection("Jwt");
		options.TokenValidationParameters = new Microsoft.IdentityModel.Tokens.TokenValidationParameters
		{
			ValidateIssuer = true,
			ValidateAudience = true,
			ValidateIssuerSigningKey = true,
			ValidateLifetime = true,
			ValidIssuer = cfg["Issuer"],
			ValidAudience = cfg["Audience"],
			IssuerSigningKey = new Microsoft.IdentityModel.Tokens.SymmetricSecurityKey(System.Text.Encoding.UTF8.GetBytes(cfg["Key"]))
		};
	});

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();

app.UseCors("AllowAll");

// AuthN/Z
app.UseAuthentication();
app.UseAuthorization();

app.MapControllers();

app.Run();
