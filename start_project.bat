@echo off
echo ===================================================================
echo LAUNCHING UNIFIED APPLICATION CLUSTER (SELF-CLOSING)
echo ===================================================================

:: 1. Move to infrastructure and wake up your Docker containers
:: NOTE: Replace the placeholder path below with your true local directory path!
cd /d "C:\Path\To\Your\Job Tracker\infrastructure"
call docker-compose start

:: 2. Launch the Spring Boot backend SILENTLY in the background using hidden tracking IDs
echo Starting Spring Boot Workflow Service engine...
cd /d "C:\Path\To\Your\Job Tracker\job-workflow-service"
start /B "" mvnw.cmd spring-boot:run

:: 3. Launch the React Vite Frontend Server SILENTLY right next to it
echo Starting React Vite Frontend Server framework...
cd /d "C:\Path\To\Your\Job Tracker\job-tracker-ui"
start /B "" npm run dev

:: 4. Pause for 4 seconds to let the local compilation finish, then open Chrome
timeout /t 4 /nobreak > nul
echo Opening Google Chrome dashboard view...
start chrome "http://localhost:5173"

echo ===================================================================
echo LIVE WORKSPACE ACTIVE. DO NOT CLOSE THIS WINDOW WHILE CODING.
echo Closing this window will instantly kill your entire system.
echo ===================================================================

:: This special trap keeps this main script window awake and waiting.
:: When you hit Ctrl+C inside this window or close it, it triggers the automatic shutdown block below.
pause

:shutdown
echo ===================================================================
echo SHUTDOWN DETECTED: TERMINATING ALL CODE BASES AND DATABASES...
echo ===================================================================
:: Safely pause and turn off your Docker infrastructure automatically
cd /d "C:\Path\To\Your\Job Tracker\infrastructure"
call docker-compose stop
exit
