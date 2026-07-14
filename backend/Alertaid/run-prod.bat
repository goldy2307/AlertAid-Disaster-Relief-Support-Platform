@echo off
setlocal ENABLEDELAYEDEXPANSION

REM Allow overrides via environment variables before calling this script
REM Example:
REM   set SERVER_PORT=9090
REM   set SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/alertaid_db
REM   set SPRING_DATASOURCE_USERNAME=root
REM   set SPRING_DATASOURCE_PASSWORD=root

set JAR=target\Alertaid-0.0.1-SNAPSHOT.jar
if not exist "%JAR%" (
  echo Building project JAR...
  call .\mvnw.cmd -DskipTests clean package || goto :error
)

echo Starting AlertAid with prod profile...
java -jar "%JAR%" --spring.profiles.active=prod
exit /b 0

:error
echo Build failed. Check the logs above.
exit /b 1
