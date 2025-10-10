// File: Program.cs
// Description: ASP.NET Core app bootstrap, DI, Swagger, CORS, JWT auth, and MongoDB setup.

using Backend.Settings;
using Backend.Data;
using MongoDB.Driver;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;
using System.Text;

var builder = WebApplication.CreateBuilder(args);

// -------------------- Add services to the container -------------------- //
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

// -------------------- MongoDB DI (Option 1) -------------------- //
var mongoSettings = builder.Configuration.GetSection("MongoDBSettings").Get<MongoDBSettings>();

if (mongoSettings == null)
    throw new Exception("MongoDBSettings section is missing in appsettings.json");

if (string.IsNullOrWhiteSpace(mongoSettings.ConnectionString))
    throw new Exception("MongoDB connection string is missing in appsettings.json");

// Register MongoClient as singleton
builder.Services.AddSingleton<IMongoClient>(sp =>
    new MongoClient(mongoSettings.ConnectionString)
);

// Register MongoContext as scoped (repositories will use it)
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

// Bind JwtSettings section and inject into TokenService
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
            ValidIssuer = cfg["Issuer"],
            ValidAudience = cfg["Audience"],
            IssuerSigningKey = new SymmetricSecurityKey(keyBytes)
        };
    });

// -------------------- Build app -------------------- //
var app = builder.Build();

// -------------------- HTTP request pipeline -------------------- //
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
