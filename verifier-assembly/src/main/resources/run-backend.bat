@echo off

setlocal

set "VERIFIER_SERVICE=verifier-backend"

if ""%1""=="""" goto errorAction
if ""%1""==""startup"" goto startupServers
if ""%1""==""shutdown"" goto shutdownServers

:startupServers
start "%VERIFIER_SERVICE%" %JAVA_HOME%\bin\java.exe -Xms20G -Xmx20G -jar ./verifier-backend.jar
goto end

:shutdownServers
rem Shutdown Spring boot backend server
taskkill /FI "WindowTitle eq %VERIFIER_SERVICE%*" /T /F
goto end

:end
