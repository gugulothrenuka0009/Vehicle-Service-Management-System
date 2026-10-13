Set-Location "$PSScriptRoot\backend"
Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "Starting Vehicle Service Management System (AutoCare PRO)" -ForegroundColor Green
Write-Host "Server URL: http://localhost:8085" -ForegroundColor Yellow
Write-Host "========================================================" -ForegroundColor Cyan

& "C:\Program Files\Java\jdk-17\bin\java.exe" -cp "target\classes;lib\*" com.vehicleservice.VehicleServiceApplication --spring.profiles.active=h2
