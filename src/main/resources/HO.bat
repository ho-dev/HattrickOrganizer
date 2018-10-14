@rem ##########################################################################
@rem Windows startup batch file for HattrickOrganizer!
@rem HO startup script for Windows
@rem Version 1.0
@rem Created by the HO-Team
@rem Last Change: 2018-10-13 by akasolace
@rem ##########################################################################

@rem Set Debug mode [on/off]
set DEBUG=off


set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..


@rem Set JavaPath, i.e. the path that contains java.exe/javaw.exe
@rem e.g. set JAVA_PATH=c:\Program Files\Java\jre1.6.0_07
@rem (leave empty to use the default JRE)
set JAVA_PATH=



@rem Don't change anything below this line, 
@rem unless you know what you are doing!

@rem ===============================================================
set CLASSPATH=%APP_HOME%;%APP_HOME%\bin;%APP_HOME%\lib\commons-codec-1.4.jar;%APP_HOME%\lib\hamcrest-core-1.3.jar;%APP_HOME%\lib\HO.jar;%APP_HOME%\lib\hsqldb-2.4.1.jar;%APP_HOME%\lib\jcalendar-1.4.jar;%APP_HOME%\lib\jgoodies-common-1.8.1.jar;%APP_HOME%\lib\jgoodies-looks-2.7.0.jar;%APP_HOME%\lib\scribe-1.3.0.jar

@rem In debug mode, we use java.exe to get System.out.println output,
@rem else we use javaw.exe to make the script quiet
if "%DEBUG%" == "on" (
	set JAVA_EXE=java.exe
) else (
	set JAVA_EXE=javaw.exe
)


@rem Check, if JavaPath is set manually
if NOT "%JAVA_PATH%" == "" (
	if exist "%JAVA_PATH%\%JAVA_EXE%" (
		set JAVA_CMD="%JAVA_PATH%\%JAVA_EXE%"
	) else if exist "%JAVA_PATH%\bin\%JAVA_EXE%" (
		set JAVA_CMD="%JAVA_PATH%\bin\%JAVA_EXE%"
	) else (
		@rem Manually set JRE not found, abort
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
	@rem Check Windows Version
	if exist "%WINDIR%\SysWOW64\%JAVA_EXE%" (
		:: Vista 64
		set JAVA_CMD="%WINDIR%\SysWOW64\%JAVA_EXE%"
	) else (
		:: Anything else
		set JAVA_CMD="%JAVA_EXE%"
	)
)

if "%DEBUG%" == "on" (
	@rem Check Java version
	echo Using Java: %JAVA_CMD%, DebugMode: %DEBUG%
	%JAVA_CMD% -version
	echo.
)

@rem Starting Launcher/Updater


%JAVA_CMD% HOUpdater


@rem Starting HO
if "%DEBUG%" == "on" (
	@rem Starting HO in foreground for debug purposes
	%JAVA_CMD% -classpath "%CLASSPATH%" HO DEBUG
	echo.
	pause
) else (
	@rem Starting HO in background
	start "" %JAVA_CMD%  -classpath "%CLASSPATH%" HO
)

exit
