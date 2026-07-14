# PowerShell script to load .env file and start Spring Boot with OAuth
# Usage: .\start-oauth.ps1

Write-Host "Loading OAuth credentials from .env file..." -ForegroundColor Cyan
Write-Host ""

# Check if .env file exists
if (-not (Test-Path ".env")) {
    Write-Host "ERROR: .env file not found!" -ForegroundColor Red
    Write-Host "Please create .env file with your OAuth credentials." -ForegroundColor Yellow
    exit 1
}

# Parse .env file and set environment variables
Get-Content .env | ForEach-Object {
    $line = $_.Trim()
    # Skip empty lines and comments
    if ($line -and -not $line.StartsWith("#")) {
        if ($line -match "^([^=]+)=(.*)$") {
            $varName = $matches[1].Trim()
            $varValue = $matches[2].Trim()
            # Remove quotes if present
            if ($varValue.StartsWith('"') -and $varValue.EndsWith('"')) {
                $varValue = $varValue.Substring(1, $varValue.Length - 2)
            }
            if ($varValue.StartsWith("'") -and $varValue.EndsWith("'")) {
                $varValue = $varValue.Substring(1, $varValue.Length - 2)
            }
            [Environment]::SetEnvironmentVariable($varName, $varValue, "Process")
            Write-Host "Loaded: $varName" -ForegroundColor Green
        }
    }
}

Write-Host ""
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Starting Spring Boot with OAuth enabled" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

# Verify credentials are set
if (-not $env:GOOGLE_CLIENT_ID -or $env:GOOGLE_CLIENT_ID -eq "YOUR_CLIENT_ID_HERE") {
    Write-Host "WARNING: GOOGLE_CLIENT_ID not set or still has placeholder value!" -ForegroundColor Yellow
    Write-Host "Please update .env file with your actual credentials." -ForegroundColor Yellow
    Write-Host ""
}

if (-not $env:GOOGLE_CLIENT_SECRET -or $env:GOOGLE_CLIENT_SECRET -eq "YOUR_CLIENT_SECRET_HERE") {
    Write-Host "WARNING: GOOGLE_CLIENT_SECRET not set or still has placeholder value!" -ForegroundColor Yellow
    Write-Host "Please update .env file with your actual credentials." -ForegroundColor Yellow
    Write-Host ""
}

# Start Spring Boot
.\mvnw spring-boot:run

