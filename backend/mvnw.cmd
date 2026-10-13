@REM ----------------------------------------------------------------------------
@REM Maven Wrapper Script for Vehicle Service Management System
@REM ----------------------------------------------------------------------------
@echo off
setlocal
cd /d "%~dp0"

echo Starting Spring Boot Backend for Vehicle Service Management System...
mvn -s settings.xml spring-boot:run "-Dspring-boot.run.profiles=h2"
pause
