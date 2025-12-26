@echo off
echo ====================================
echo Compilazione Gestionale Libri
echo ====================================

REM Crea la cartella bin se non esiste
if not exist bin mkdir bin

REM Compila tutti i file Java
echo.
echo Compilazione in corso...
javac -d bin *.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✓ Compilazione completata con successo!
    echo.
    
    REM Crea il file MANIFEST
    echo Main-Class: Main > bin\MANIFEST.MF
    echo. >> bin\MANIFEST.MF
    
    REM Crea il JAR
    echo Creazione file JAR...
    cd bin
    jar cfm gestionale-libri.jar MANIFEST.MF *.class
    move gestionale-libri.jar ..
    cd ..
    
    echo.
    echo ✓ File JAR creato: gestionale-libri.jar
    echo.
    echo Per eseguire il programma:
    echo   - Doppio click su gestionale-libri.jar
    echo   - Oppure: java -jar gestionale-libri.jar
) else (
    echo.
    echo ✗ Errore durante la compilazione!
)

echo.
pause