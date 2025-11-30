@echo off
REM Run Spring Boot backend with Java 21
REM This ensures compatibility regardless of system Java version

set "JAVA_HOME=C:\Program Files\Java\jdk-21"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo Using Java version:
java -version
echo.
echo Starting backend with Maven...
echo.

mvn spring-boot:run
