// File: Program.cs
// Description: ASP.NET Core app bootstrap, DI, Swagger, CORS, JWT auth, and MongoDB setup.
// Adjusted for IIS hosting under /EVChargingAPI

using Backend.Settings;
using Backend.Data;
using MongoDB.Driver;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;
using System.Text;

var builder = WebApplication.CreateBuilder(args);

// -------------------- Add services -------------------- //
builder.Services.AddControllers();

// Swagger/OpenAPI
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

// CORS
builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowAll", policy =>
    {
        policy.AllowAnyOrigin().AllowAnyHeader().AllowAnyMethod();
    });
});

// -------------------- MongoDB DI -------------------- //
var mongoSettings = builder.Configuration.GetSection("MongoDBSettings").Get<MongoDBSettings>();

if (mongoSettings == null)
    throw new Exception("MongoDBSettings section is missing in appsettings.json");

if (string.IsNullOrWhiteSpace(mongoSettings.ConnectionString))
    throw new Exception("MongoDB connection string is missing in appsettings.json");

// Register MongoClient
builder.Services.AddSingleton<IMongoClient>(sp =>
    new MongoClient(mongoSettings.ConnectionString)
);

builder.Services.AddScoped<MongoContext>(sp =>
{
    var client = sp.GetRequiredService<IMongoClient>();
    return new MongoContext(client, mongoSettings);
});

// -------------------- Repositories -------------------- //
builder.Services.AddScoped<Backend.Repositories.IUserRepository, Backend.Repositories.UserRepository>();
builder.Services.AddScoped<Backend.Repositories.IStationRepository, Backend.Repositories.StationRepository>();
builder.Services.AddScoped<Backend.Repositories.IBookingRepository, Backend.Repositories.BookingRepository>();

// -------------------- Services -------------------- //
builder.Services.AddScoped<Backend.Services.IUserService, Backend.Services.UserService>();
builder.Services.AddScoped<Backend.Services.IStationService, Backend.Services.StationService>();
builder.Services.AddScoped<Backend.Services.IBookingService, Backend.Services.BookingService>();

builder.Services.Configure<JwtSettings>(builder.Configuration.GetSection("Jwt"));
builder.Services.AddSingleton<Backend.Services.ITokenService, Backend.Services.TokenService>();

// -------------------- JWT Authentication -------------------- //
var jwtKey = builder.Configuration["Jwt:Key"];
if (string.IsNullOrWhiteSpace(jwtKey))
    throw new Exception("JWT Key is missing in appsettings.json");

builder.Services
    .AddAuthentication(options =>
    {
        options.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
        options.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
    })
    .AddJwtBearer(options =>
    {
        var cfg = builder.Configuration.GetSection("Jwt");
        var keyBytes = Encoding.UTF8.GetBytes(cfg["Key"]!);

        options.TokenValidationParameters = new TokenValidationParameters
        {
            ValidateIssuer = true,
            ValidateAudience = true,
            ValidateIssuerSigningKey = true,
            ValidateLifetime = true,
            IssuerSigningKey = new SymmetricSecurityKey(keyBytes),
            RoleClaimType = "http://schemas.microsoft.com/ws/2008/06/identity/claims/role",
            NameClaimType = "sub",
            ValidIssuer = "EVCharging",
            ValidAudience = "EVChargingClients",
        };
    });

// -------------------- Build app -------------------- //
var app = builder.Build();

// -------------------- Path base for IIS -------------------- //
app.UsePathBase("/EVChargingAPI");

// -------------------- HTTP request pipeline -------------------- //
app.UseCors("AllowAll");

// Swagger configuration
//if (app.Environment.IsDevelopment())
//{
    app.UseSwagger(c =>
    {
        c.RouteTemplate = "swagger/{documentName}/swagger.json";
    });

    app.UseSwaggerUI(c =>
    {
        c.SwaggerEndpoint("/EVChargingAPI/swagger/v1/swagger.json", "EVChargingAPI V1");
        c.RoutePrefix = "swagger"; // Accessible at /EVChargingAPI/swagger
    });
//}

// AuthN/Z
app.UseAuthentication();
app.UseAuthorization();

app.MapControllers();

app.Run();
