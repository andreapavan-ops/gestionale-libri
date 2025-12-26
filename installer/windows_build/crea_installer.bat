@echo off
echo ==========================================
echo    CREAZIONE INSTALLER WINDOWS
echo    Gestionale Libri v1.0
echo ==========================================
echo.

REM Verifica Java
java -version >nul 2>&1
if errorlevel 1 (
    echo ERRORE: Java non trovato!
    echo Scarica JDK 21 da: https://adoptium.net/
    pause
    exit /b 1
)

REM Verifica jpackage
jpackage --version >nul 2>&1
if errorlevel 1 (
    echo ERRORE: jpackage non trovato!
    echo Assicurati di avere JDK 21 installato (non solo JRE)
    pause
    exit /b 1
)

echo Java trovato! Creazione installer in corso...
echo.

REM Crea l'installer EXE
jpackage ^
  --type exe ^
  --name "Gestionale Libri" ^
  --input input ^
  --main-jar GestionaleLibri.jar ^
  --main-class Main ^
  --dest output ^
  --app-version 1.0 ^
  --vendor "Carlo e AnnaMaria" ^
  --description "Gestionale Biblioteca Personale" ^
  --win-shortcut ^
  --win-menu ^
  --win-menu-group "Gestionale Libri"

if errorlevel 1 (
    echo.
    echo ERRORE durante la creazione dell'installer!
    pause
    exit /b 1
)

echo.
echo ==========================================
echo    COMPLETATO!
echo ==========================================
echo.
echo L'installer si trova in: output\Gestionale Libri-1.0.exe
echo.
echo Puoi distribuire questo file .exe
echo Include Java, non serve installarlo separatamente!
echo.
pause
