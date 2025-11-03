@echo off
echo Iniciando KilomboCRM...
echo.

REM Verificar si Java está instalado
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java no está instalado o no está en el PATH
    echo.
    echo Por favor, instala Java 17 o superior desde:
    echo https://adoptium.net/
    echo.
    pause
    exit /b 1
)

REM Verificar versión de Java (básica)
for /f "tokens=3" %%i in ('java -version 2^>^&1 ^| findstr /i "version"') do (
    set JAVA_VERSION=%%i
)
set JAVA_VERSION=%JAVA_VERSION:"=%

echo Java detectado: %JAVA_VERSION%

REM Ejecutar la aplicación
echo Iniciando aplicación...
java -jar "%~dp0KilomboCRM-1.0.0-jar-with-dependencies.jar"

pause