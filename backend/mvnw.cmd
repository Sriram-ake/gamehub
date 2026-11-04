@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
set "PARENT_MAVEN=%SCRIPT_DIR%..\.maven\bin\mvn.cmd"
set "LOCAL_MAVEN=%SCRIPT_DIR%.maven\bin\mvn.cmd"

if exist "%PARENT_MAVEN%" (
    call "%PARENT_MAVEN%" %*
    exit /b %ERRORLEVEL%
)

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

echo Error: Maven executable not found in .maven or system PATH.
exit /b 1
