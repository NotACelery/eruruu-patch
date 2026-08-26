@echo off
setlocal EnableExtensions
cd /d "%~dp0"
title Eruruu Patch - Compilacion dev

set "GRADLE_VERSION=9.2.1"
set "DIST_ROOT=%CD%\.gradle-dist"
set "DIST_DIR=%DIST_ROOT%\gradle-%GRADLE_VERSION%"
set "DIST_ZIP=%DIST_ROOT%\gradle-%GRADLE_VERSION%-bin.zip"
set "JAVA_EXE="
set "MOD_VERSION=1.2.0"
if exist "gradle.properties" (
    for /f "tokens=1,* delims==" %%A in ('findstr /b /c:"mod_version=" "gradle.properties"') do set "MOD_VERSION=%%B"
)

echo ============================================================
echo             ERURUU PATCH - BUILD %MOD_VERSION%
echo ============================================================
echo Directorio: %CD%
echo.


rem ------------------------------------------------------------
rem 0. Limpiar residuos de versiones anteriores si el source fue
rem    extraido encima de una carpeta existente.
rem ------------------------------------------------------------
if exist "cleanup-migrated-features.bat" (
    call "cleanup-migrated-features.bat"
    if errorlevel 1 (
        echo.
        echo ERROR: Fallo la limpieza de residuos migrados de Eruruu.
        goto :failure
    )
)
if exist "cleanup-removed-features.bat" (
    call "cleanup-removed-features.bat"
    if errorlevel 1 (
        echo.
        echo ERROR: Fallo la limpieza de funcionalidades retiradas de Eruruu.
        goto :failure
    )
)
echo.

rem ------------------------------------------------------------
rem 1. Buscar Java: PATH, JAVA_HOME y runtimes administrados por Prism.
rem ------------------------------------------------------------
where java.exe >nul 2>nul
if not errorlevel 1 (
    for /f "delims=" %%J in ('where java.exe') do if not defined JAVA_EXE set "JAVA_EXE=%%J"
)

if not defined JAVA_EXE if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" (
    set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
)

if not defined JAVA_EXE (
    echo Java no esta en PATH. Buscando una instalacion de Prism Launcher...
    for /f "usebackq delims=" %%J in (`powershell -NoProfile -ExecutionPolicy Bypass -Command "$roots=@($env:APPDATA+'\PrismLauncher\java',$env:LOCALAPPDATA+'\PrismLauncher\java',$env:LOCALAPPDATA+'\Programs\PrismLauncher\java',$env:ProgramFiles+'\PrismLauncher\java',$env:ProgramFiles+'\Eclipse Adoptium',$env:ProgramFiles+'\Java'); foreach($root in $roots){if(Test-Path -LiteralPath $root){$found=Get-ChildItem -LiteralPath $root -Filter java.exe -File -Recurse -ErrorAction SilentlyContinue ^| Where-Object {$_.FullName -match '\\bin\\java\.exe$'} ^| Select-Object -First 1; if($found){$found.FullName; break}}}"`) do if not defined JAVA_EXE set "JAVA_EXE=%%J"
)

if not defined JAVA_EXE goto :java_missing
if not exist "%JAVA_EXE%" goto :java_missing

for %%I in ("%JAVA_EXE%") do set "JAVA_BIN=%%~dpI"
for %%I in ("%JAVA_BIN%..") do set "JAVA_HOME=%%~fI"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo Java encontrado:
echo   %JAVA_EXE%
"%JAVA_EXE%" -version
if errorlevel 1 goto :java_broken
echo.

rem ------------------------------------------------------------
rem 2. Descargar Gradle si aun no esta disponible localmente.
rem ------------------------------------------------------------
if not exist "%DIST_DIR%\bin\gradle.bat" (
    echo Gradle %GRADLE_VERSION% no esta descargado.
    if not exist "%DIST_ROOT%" mkdir "%DIST_ROOT%"
    if errorlevel 1 goto :mkdir_failed

    if exist "%DIST_ZIP%" del /q "%DIST_ZIP%"

    echo Descargando Gradle %GRADLE_VERSION%...
    where curl.exe >nul 2>nul
    if not errorlevel 1 (
        curl.exe -L --fail --retry 3 --output "%DIST_ZIP%" "https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip"
    ) else (
        powershell -NoProfile -ExecutionPolicy Bypass -Command "$ProgressPreference='SilentlyContinue'; Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip' -OutFile '%DIST_ZIP%'"
    )
    if errorlevel 1 goto :download_failed
    if not exist "%DIST_ZIP%" goto :download_failed

    echo.
    echo Descomprimiendo Gradle...
    powershell -NoProfile -ExecutionPolicy Bypass -Command "Expand-Archive -LiteralPath '%DIST_ZIP%' -DestinationPath '%DIST_ROOT%' -Force"
    if errorlevel 1 goto :extract_failed
)

if not exist "%DIST_DIR%\bin\gradle.bat" goto :gradle_missing

rem ------------------------------------------------------------
rem 3. Compilar el mod con ModDevGradle.
rem ------------------------------------------------------------
echo.
echo Compilando Eruruu Patch %MOD_VERSION%...
echo La primera compilacion puede descargar dependencias de NeoForge.
echo.
set "BUILD_LOG=%CD%\build-dev.log"
if exist "%BUILD_LOG%" del /q "%BUILD_LOG%"
echo Guardando salida completa en:
echo   %BUILD_LOG%
echo.
call "%DIST_DIR%\bin\gradle.bat" --no-daemon clean build --stacktrace > "%BUILD_LOG%" 2>&1
set "BUILD_EXIT=%ERRORLEVEL%"
type "%BUILD_LOG%"
if not "%BUILD_EXIT%"=="0" goto :build_failed

set "JAR_FILE="
for /f "delims=" %%F in ('dir /b /a-d "build\libs\*.jar" 2^>nul') do if not defined JAR_FILE set "JAR_FILE=build\libs\%%F"

if not defined JAR_FILE goto :jar_missing

echo.
echo ============================================================
echo COMPILACION TERMINADA CORRECTAMENTE
echo JAR generado:
echo   %CD%\%JAR_FILE%
echo ============================================================
goto :success


:java_missing
echo.
echo ERROR: No encontre Java para ejecutar Gradle.
echo Este mod para Minecraft 1.21.1 necesita Java 21.
echo.
echo Prism puede ejecutar Minecraft con un Java interno sin agregarlo al PATH.
echo Soluciones:
echo   1. En Prism: Ajustes ^> Java ^> abrir o copiar la ruta de Java.
echo   2. Instalar Java 21, por ejemplo Eclipse Temurin 21.
echo   3. Abrir CMD aqui y ejecutar:
echo        set "JAVA_HOME=C:\ruta\a\java-21"
echo        build-dev.bat
goto :failure

:java_broken
echo ERROR: La instalacion de Java encontrada no puede ejecutarse.
goto :failure

:mkdir_failed
echo ERROR: No pude crear la carpeta "%DIST_ROOT%".
goto :failure

:download_failed
echo.
echo ERROR: No se pudo descargar Gradle.
echo Revisa internet, antivirus, proxy o firewall.
echo URL:
echo https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip
goto :failure

:extract_failed
echo ERROR: Gradle se descargo, pero no pudo descomprimirse.
echo Borra la carpeta .gradle-dist y vuelve a ejecutar este archivo.
goto :failure

:gradle_missing
echo ERROR: No existe "%DIST_DIR%\bin\gradle.bat" despues de descomprimir.
goto :failure

:build_failed
echo.
echo ============================================================
echo LA COMPILACION FALLO
echo La salida completa quedo guardada en:
echo   %BUILD_LOG%
echo Enviame ese archivo completo.
echo ============================================================
goto :failure

:jar_missing
echo ERROR: Gradle termino, pero no encontre ningun JAR en build\libs\.
goto :failure

:failure
echo.
echo La ventana quedara abierta para que puedas leer o copiar el error.
pause
endlocal & exit /b 1

:success
echo.
echo Puedes copiar el JAR a la carpeta mods de tu instancia de Prism Launcher.
pause
endlocal & exit /b 0
