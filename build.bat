@echo off
title RatPoison Builder
@ECHO OFF
set PATH=%JAVA_HOME%\bin
echo JAVA_HOME = %JAVA_HOME%
echo PATH = %PATH%
call gradlew RatPoison --warning-mode all
echo.
echo If there is an error message, close this window and google the issue.
echo.
set /P c=Would you like to randomize the file name for safety? [Y/N] 
if /I "%c%" EQU "Y" goto :y
if /I "%c%" EQU "N" goto :n
:y
rem 16 stings pwd

setlocal ENABLEEXTENSIONS ENABLEDELAYEDEXPANSION
set alfanum=ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789

set pwd=
FOR /L %%b IN (0, 1, 16) DO (
SET /A rnd_num=!RANDOM! * 62 / 32768 + 1
for /F %%c in ('echo %%alfanum:~!rnd_num!^,1%%') do set pwd=!pwd!%%c
)
cd build
cd RatPoison 1.8
ren "RatPoison 1.8.jar" "%pwd%.jar"
powershell -Command "(Get-Content -path 'Start RatPoison 1.8.bat') -replace 'RatPoison 1.8', '%pwd%' | Set-Content -Path 'Start RatPoison 1.8.bat'"
ren "Start RatPoison 1.8.bat" "%pwd%.bat"
echo.
echo File name has been randomized. 
goto :n
:n
cd build
cd RatPoison 1.8
echo.
set /P c=Would you like to open the cheat folder? [Y/N] 
if /I "%c%" EQU "Y" goto :y1
if /I "%c%" EQU "N" goto :n1
:y1
start.
goto :n1
:n1
echo.
echo To run this cheat, open csgo and run the bat folder in the cheat folder.
echo.
pause
