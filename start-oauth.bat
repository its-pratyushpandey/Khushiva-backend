@echo off
echo ==================================================
echo Starting AI Chatbot Backend with OAuth2
echo ==================================================
echo.

set JAVA_HOME=C:\Program Files\Java\jdk-21
set PATH=C:\Program Files\Java\jdk-21\bin;%PATH%
set MAVEN_OPTS=-Xmx1024m -XX:MaxPermSize=256m

echo Using Java version:
java -version
echo.

echo Starting Spring Boot with dev profile...
echo.

cd /d "%~dp0"
call mvn clean spring-boot:run -Dspring-boot.run.profiles=dev

pause
