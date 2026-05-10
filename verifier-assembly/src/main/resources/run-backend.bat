@echo off

setlocal

rem Set local environment variables
set "VERIFIER_HOME=%cd%"
set "JAVA_HOME=%VERIFIER_HOME%\embedded-jre"
set "JRE_HOME=%VERIFIER_HOME%\embedded-jre"

rem Create Verifier service
set "VERIFIER_SERVICE=verifier-backend"

rem Get action "startup" or "shutdown"
if ""%1""=="""" goto errorAction
if ""%1""==""startup"" goto startupServer
if ""%1""==""shutdown"" goto shutdownServer

:startupServer
rem Run Spring boot backend server
start "%VERIFIER_SERVICE%" %JAVA_HOME%\bin\java.exe -Xms20G -Xmx20G -jar ./verifier-backend.jar
goto end

:shutdownServer
rem Shutdown Spring boot backend server
taskkill /FI "WindowTitle eq %VERIFIER_SERVICE%*" /T /F
goto end

:end
