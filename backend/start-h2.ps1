Set-Location $PSScriptRoot
& "C:\Program Files\Java\jdk-17\bin\java.exe" -cp "target\classes;lib\*" com.vehicleservice.VehicleServiceApplication --spring.profiles.active=h2
