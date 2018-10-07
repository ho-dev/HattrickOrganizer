@echo off
:: ---------------------------------------------------------------
:: Windows startup batch file for HattrickOrganizer!
:: Version 0.9
:: Created by the HO-Team
::
:: Last Change: 2012-06-15 by blaghaid
:: ---------------------------------------------------------------

:: Set Debug mode [on/off]
set DEBUG=off

:: Set JavaPath, i.e. the path that contains java.exe/javaw.exe
:: e.g. set JAVA_PATH=c:\Program Files\Java\jre1.6.0_07
:: (leave empty to use the default JRE)
set JAVA_PATH=

:: Set MaxMemory for HO (default: 512m)
set MAX_MEMORY=512m

:: Don't change anything below this line, 
:: unless you know what you are doing!

:: ===============================================================

if "%MAX_MEMORY%" == "" (
	set MAX_MEMORY=512m
)

:: In debug mode, we use java.exe to get System.out.println output,
:: else we use javaw.exe to make the script quiet
if "%DEBUG%" == "on" (
	set JAVA_EXE=java.exe
) else (
	set JAVA_EXE=javaw.exe
)

:: Check, if JavaPath is set manually
if NOT "%JAVA_PATH%" == "" (
	if exist "%JAVA_PATH%\%JAVA_EXE%" (
		set JAVA_CMD="%JAVA_PATH%\%JAVA_EXE%"
	) else if exist "%JAVA_PATH%\bin\%JAVA_EXE%" (
		set JAVA_CMD="%JAVA_PATH%\bin\%JAVA_EXE%"
	) else (
		:: Manually set JRE not found, abort
		echo Error!
		echo.
		echo %JAVA_EXE% not found in %JAVA_PATH%
		echo %JAVA_EXE% not found in %JAVA_PATH%\bin
		echo.
		echo Please set the correct JAVA_PATH in HO.bat.
		echo.
		echo Aborting!
		pause
		exit
	)
) else (
	:: Check Windows Version
	if exist "%WINDIR%\SysWOW64\%JAVA_EXE%" (
		:: Vista 64
		set JAVA_CMD="%WINDIR%\SysWOW64\%JAVA_EXE%"
	) else (
		:: Anything else
		set JAVA_CMD="%JAVA_EXE%"
	)
)

if "%DEBUG%" == "on" (
	:: Check Java version
	echo Using Java: %JAVA_CMD%, DebugMode: %DEBUG%, MaxMemory: %MAX_MEMORY%
	%JAVA_CMD% -version
	echo.
)

:: Starting Launcher/Updater
%JAVA_CMD% -classpath . HOLauncher

:: Starting HO
if "%DEBUG%" == "on" (
	:: Starting HO in foreground for debug purposes
	%JAVA_CMD% -Xmx%MAX_MEMORY% -jar ho.jar DEBUG
	echo.
	pause
) else (
	:: Starting HO in background
	start "" %JAVA_CMD% -Xmx%MAX_MEMORY% -Djava.net.preferIPv4Stack=true -jar ho.jar 
)

if exist extension.bat call extension.bat

exit
