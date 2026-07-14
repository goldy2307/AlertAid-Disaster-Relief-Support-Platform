<# 
.SYNOPSIS
    Loads variables from .env and launches the Spring Boot app so that sensitive
    credentials (OAuth, payment gateway, etc.) are available at runtime.

.USAGE
    PS> .\start-env.ps1
#>

Write-Host "Loading environment variables from .env file..." -ForegroundColor Cyan
Write-Host ""

if (-not (Test-Path ".env")) {
    Write-Host "ERROR: .env file not found!" -ForegroundColor Red
    Write-Host "Copy .env.example to .env and add your credentials." -ForegroundColor Yellow
    exit 1
}

$loadedVars = @()
Get-Content .env | ForEach-Object {
    $line = $_.Trim()
    if ($line -and -not $line.StartsWith("#")) {
        if ($line -match "^([^=]+)=(.*)$") {
            $varName = $matches[1].Trim()
            $varValue = $matches[2].Trim()
            if ($varValue.StartsWith('"') -and $varValue.EndsWith('"')) {
                $varValue = $varValue.Substring(1, $varValue.Length - 2)
            }
            if ($varValue.StartsWith("'") -and $varValue.EndsWith("'")) {
                $varValue = $varValue.Substring(1, $varValue.Length - 2)
            }
            [Environment]::SetEnvironmentVariable($varName, $varValue, "Process")
            $loadedVars += $varName
            Write-Host "Loaded: $varName" -ForegroundColor Green
        }
    }
}

Write-Host ""
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Environment variables loaded for this run" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

if (-not $env:PAYMENT_RAZORPAY_KEY_ID -or -not $env:PAYMENT_RAZORPAY_KEY_SECRET) {
    Write-Host "WARNING: Payment gateway keys not set (PAYMENT_RAZORPAY_KEY_ID / PAYMENT_RAZORPAY_KEY_SECRET)." -ForegroundColor Yellow
    Write-Host "         Add them to .env so online donations can be processed." -ForegroundColor Yellow
    Write-Host ""
}

if (-not $env:GOOGLE_CLIENT_ID -or -not $env:GOOGLE_CLIENT_SECRET) {
    Write-Host "NOTE: OAuth credentials are optional. Provide GOOGLE_CLIENT_ID/SECRET if you use Google sign-in." -ForegroundColor DarkYellow
    Write-Host ""
}

Write-Host "Starting Spring Boot (mvnw spring-boot:run)..." -ForegroundColor Cyan
Write-Host ""

.\mvnw spring-boot:run
