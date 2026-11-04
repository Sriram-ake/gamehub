@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
set "LOCAL_MAVEN=%SCRIPT_DIR%.maven\bin\mvn.cmd"

if exist "%LOCAL_MAVEN%" (
    call "%LOCAL_MAVEN%" %*
    exit /b %ERRORLEVEL%
)

where mvn.cmd >nul 2>nul
if %ERRORLEVEL% equ 0 (
    call mvn.cmd %*
    exit /b %ERRORLEVEL%
)

where mvn >nul 2>nul
if %ERRORLEVEL% equ 0 (
    call mvn %*
    exit /b %ERRORLEVEL%
)

echo Error: Neither local Maven (.maven\bin\mvn.cmd) nor system Maven was found.
exit /b 1
