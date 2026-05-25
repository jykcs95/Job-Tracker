@echo off
echo ===================================================================
echo LAUNCHING JOB TRACKER SYSTEM CLUSTER (JAVA 25 & REACT TELEMETRY)
echo ===================================================================

:: 1. Navigate to infrastructure and wake up all your Docker containers
cd /d "C:\Path\To\Your\Job Tracker\infrastructure"
call docker-compose up -d

:: 2. Launch the main Spring Boot Workflow Write Service (Port 8080)
echo Starting Spring Boot Workflow Service engine...
cd /d "C:\Path\To\Your\Job Tracker\job-workflow-service"
start /B "" mvnw.cmd spring-boot:run

:: 3. Launch the new background Analytics Processing Engine (Port 8081)
echo Starting Spring Boot Analytics Processing Engine...
cd /d "C:\Path\To\Your\Job Tracker\analytics-service"
start /B "" mvnw.cmd spring-boot:run

:: 4. Launch the React Vite Frontend Server UI (Port 5173)
echo Starting React Vite Frontend Server framework...
cd /d "C:\Path\To\Your\Job Tracker\job-tracker-ui"
start /B "" npm run dev

:: 5. Pause for 5 seconds to allow full compilation, then launch Chrome
timeout /t 5 /nobreak > nul
echo Opening Google Chrome dashboard workspace...
start chrome "http://localhost:5173"

echo ===================================================================
echo LIVE WORKSPACE ACTIVE. DO NOT CLOSE THIS CONSOLE WINDOW.
echo Closing this window will instantly kill your entire system.
echo ===================================================================
pause

:shutdown
echo ===================================================================
echo SHUTDOWN DETECTED: TERMINATING ALL CODE BASES AND DATABASES...
echo ===================================================================
cd /d "C:\Path\To\Your\Job Tracker\infrastructure"
call docker-compose stop
exit
