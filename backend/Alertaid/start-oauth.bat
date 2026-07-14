@echo off
REM This script loads OAuth credentials from .env file and starts Spring Boot
REM Usage: start-oauth.bat

echo ==========================================
echo Loading OAuth credentials from .env file
echo ==========================================
echo.

REM Check if .env file exists
if not exist .env (
    echo ERROR: .env file not found!
    echo.
    echo Please create a .env file with your credentials:
    echo   GOOGLE_CLIENT_ID=your-client-id
    echo   GOOGLE_CLIENT_SECRET=your-client-secret
    echo.
    pause
    exit /b 1
)

REM Use PowerShell to properly load .env and start Spring Boot
powershell -ExecutionPolicy Bypass -File "%~dp0start-oauth.ps1"

if errorlevel 1 (
    echo.
    echo ERROR: Failed to start application
    pause
    exit /b 1
)

