#!/bin/bash
# Compila e avvia il Gestionale Biblioteca

echo "Compilazione in corso..."
javac -cp "lib/*:." *.java

if [ $? -eq 0 ]; then
    echo "Compilazione completata!"
    echo "Avvio applicazione..."
    java -cp "lib/*:." GestionaleGUI
else
    echo "Errore durante la compilazione!"
fi
