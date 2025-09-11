# EV Charging Station Booking System

📌 **Sri Lanka Institute of Information Technology (SLIIT)**
- Module: SE4040 – Enterprise Application Development  
- Year 4, Semester 1 – 2025  

---

## 🚗 Project Overview
This project is an **end-to-end EV Charging Station Booking System** built using **client–server architecture**.  
It consists of:
- A **Web Application** for Backoffice staff and Station Operators
- A **Mobile Application** for EV Owners and Station Operators
- A **C# Web API Service** with a **NoSQL Database (MongoDB)** for business logic

---

## 🏗️ System Architecture
- **Web Application**: React.js + Bootstrap/Tailwind CSS  
- **Mobile Application**: Pure Android (Java) + SQLite (local DB)  
- **Backend API**: ASP.NET Core Web API (C#) + MongoDB  
- **Deployment**: IIS Server  

---

## 🔑 Features

### Web Application
- **User Management** (Backoffice/Admin, Station Operators)  
- **EV Owner Management** (CRUD, activate/deactivate)  
- **Charging Station Management** (create, update, deactivate stations)  
- **Booking Management** (create, update, cancel reservations with rules)

### Mobile Application
- **User Management** (EV owner self-registration, local DB sync)  
- **Reservation Management** (create, modify, cancel, QR code generation)  
- **Dashboard** (pending & approved reservations, nearby stations via Google Maps API)  
- **Station Operator Mode** (QR scan + confirm charging session)  

### Backend Web API
- Centralized business logic ("FAT Service")  
- Booking rules: reservations within 7 days, changes ≥12 hours before  
- QR code generation & verification  
- NoSQL data storage (MongoDB)  

---

## 🗂️ Repository Structure
