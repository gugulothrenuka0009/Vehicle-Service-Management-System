@echo off
cd /d "%~dp0\backend"
echo =========================================================================
echo Starting Vehicle Service Management System (AutoCare PRO) - Spring Boot
echo Server listening on: http://localhost:8085
echo =========================================================================
"C:\Program Files\Java\jdk-17\bin\java.exe" -cp "target\classes;lib\*" com.vehicleservice.VehicleServiceApplication --spring.profiles.active=h2
pause
