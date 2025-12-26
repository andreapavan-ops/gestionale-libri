#!/bin/bash
# ==========================================
#   BUILD COMPLETO - Gestionale Libri
#   Compila + Crea JAR + Crea Installer Mac
# ==========================================

cd "$(dirname "$0")"

echo "=========================================="
echo "   BUILD GESTIONALE LIBRI"
echo "=========================================="
echo ""

# 1. Pulisci vecchi file
echo "[1/5] Pulizia file vecchi..."
rm -f *.class
rm -f GestionaleLibri.jar

# 2. Compila
echo "[2/5] Compilazione in corso..."
javac -cp ".:lib/gson-2.10.1.jar:lib/itextpdf-5.5.13.3.jar" *.java 2>&1

if [ $? -ne 0 ]; then
    echo ""
    echo "ERRORE: Compilazione fallita!"
    read -p "Premi INVIO per chiudere..."
    exit 1
fi

echo "    Compilazione OK!"

# 3. Crea JAR
echo "[3/5] Creazione JAR..."
mkdir -p build
echo "Main-Class: Main
Class-Path: lib/gson-2.10.1.jar lib/itextpdf-5.5.13.3.jar
" > build/MANIFEST.MF

jar cfm GestionaleLibri.jar build/MANIFEST.MF *.class

echo "    JAR creato!"

# 4. Prepara file per installer
echo "[4/5] Preparazione installer..."
rm -rf installer/input installer/output
mkdir -p installer/input
cp GestionaleLibri.jar installer/input/
cp -r lib installer/input/

# 5. Crea app Mac
echo "[5/5] Creazione app macOS (potrebbe richiedere 1-2 minuti)..."
jpackage \
  --type dmg \
  --name "Gestionale Libri" \
  --input installer/input \
  --main-jar GestionaleLibri.jar \
  --main-class Main \
  --dest installer/output \
  --app-version 1.0 \
  --vendor "Carlo & AnnaMaria" \
  --description "Gestionale Biblioteca Personale" \
  --mac-package-name "GestionaleLibri" 2>&1

if [ $? -ne 0 ]; then
    echo ""
    echo "ERRORE: Creazione installer fallita!"
    read -p "Premi INVIO per chiudere..."
    exit 1
fi

# Aggiorna anche windows_build
echo ""
echo "Aggiornamento cartella Windows..."
mkdir -p installer/windows_build/input
cp GestionaleLibri.jar installer/windows_build/input/
cp -r lib installer/windows_build/input/

echo ""
echo "=========================================="
echo "   BUILD COMPLETATO!"
echo "=========================================="
echo ""
echo "File creati:"
echo "  - GestionaleLibri.jar"
echo "  - installer/output/Gestionale Libri-1.0.dmg"
echo "  - installer/windows_build/ (pronto per Windows)"
echo ""
read -p "Premi INVIO per chiudere..."
