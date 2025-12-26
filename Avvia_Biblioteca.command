#!/bin/bash
cd "$(dirname "$0")"

# Compila se necessario
if [ ! -f "GestionaleGUI.class" ] || [ "GestionaleGUI.java" -nt "GestionaleGUI.class" ]; then
    echo "Compilazione..."
    javac -cp "lib/*:." *.java
fi

# Avvia l'applicazione
java -cp "lib/*:." GestionaleGUI
