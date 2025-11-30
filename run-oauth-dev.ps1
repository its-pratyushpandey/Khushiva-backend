# PowerShell script to run Spring Boot backend with OAuth2 credentials
# This script uses Java 21 explicitly to avoid Lombok compatibility issues

Write-Host "==================================================" -ForegroundColor Cyan
Write-Host "Starting AI Chatbot Backend with OAuth2" -ForegroundColor Cyan
Write-Host "==================================================" -ForegroundColor Cyan
Write-Host ""

# Set Java 21 explicitly
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
$env:PATH = "C:\Program Files\Java\jdk-21\bin;$env:PATH"

# Verify Java version
Write-Host "Using Java version:" -ForegroundColor Green
& java -version
Write-Host ""

# Navigate to backend directory
Set-Location -Path $PSScriptRoot

# Run Spring Boot with dev profile (contains OAuth credentials)
Write-Host "Starting Spring Boot with dev profile..." -ForegroundColor Yellow
Write-Host ""

mvn spring-boot:run -Dspring-boot.run.profiles=dev
