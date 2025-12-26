# Gestionale Libri - Monorepo

Gestionale per biblioteca personale con versione **Desktop** (Swing) e **Web** (Spring Boot).

## Struttura Progetto

```
gestionale-libri/
├── gestionale-common/     # Classi condivise (Model: Libro, Recensione)
├── gestionale-desktop/    # App Desktop Swing (JAR eseguibile)
├── gestionale-web/        # App Web Spring Boot (per Railway)
└── pom.xml               # Parent POM Maven
```

## Requisiti

- Java 21
- Maven 3.8+

## Build Completa

```bash
# Compila tutto il progetto
mvn clean install

# Compila e crea JAR eseguibili
mvn clean package
```

## Esecuzione Desktop

```bash
# Opzione 1: Esegui direttamente con Maven
cd gestionale-desktop
mvn exec:java -Dexec.mainClass="it.biblioteca.desktop.Main"

# Opzione 2: Esegui il JAR con tutte le dipendenze
java -jar gestionale-desktop/target/gestionale-desktop-1.0.0-jar-with-dependencies.jar
```

## Esecuzione Web (Sviluppo Locale)

```bash
# Avvia con profilo dev (database H2 in memoria)
cd gestionale-web
mvn spring-boot:run

# Oppure:
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```
Apri nel browser: http://localhost:8080

## Deploy su Railway

### 1. Prepara il Repository GitHub

```bash
# Dalla cartella GESTIONALE_LIBRI_MONOREPO
git init
git add .
git commit -m "Initial commit - Gestionale Libri Monorepo"
git branch -M main
git remote add origin https://github.com/TUO_USERNAME/gestionale-libri.git
git push -u origin main
```

### 2. Configura Railway

1. Vai su https://railway.app e accedi con GitHub
2. Clicca "New Project" > "Deploy from GitHub repo"
3. Seleziona il repository `gestionale-libri`
4. Railway rileva automaticamente il progetto Maven

### 3. Aggiungi Database PostgreSQL

1. Nel progetto Railway, clicca "New" > "Database" > "PostgreSQL"
2. Railway crea automaticamente la variabile `DATABASE_URL`

### 4. Configura le Variabili d'Ambiente

Nel servizio web, aggiungi queste variabili:

| Variabile | Valore |
|-----------|--------|
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `RAILWAY_ROOT_DIRECTORY` | `gestionale-web` |

### 5. Configura il Build

Railway usa automaticamente i file `railway.json` e `Procfile` nella cartella `gestionale-web`.

Build Command (automatico):
```
cd .. && mvn clean package -pl gestionale-web -am -DskipTests
```

Start Command (automatico):
```
java -jar target/gestionale-web-1.0.0.jar
```

### 6. Deploy

Railway fa il deploy automatico ad ogni push su GitHub!

## Funzionalita

### Versione Desktop
- Gestione libri (CRUD)
- Esportazione PDF/TXT
- Statistiche con grafici
- Recensioni e valutazioni
- Wishlist per utente
- Tema chiaro/scuro

### Versione Web
- Catalogo libri online
- Aggiunta/modifica/eliminazione libri
- Visualizzazione dettagli
- Recensioni con stelle
- Responsive design

## Tecnologie

- **Java 21**
- **Maven** (Multi-module)
- **Swing** (Desktop GUI)
- **Spring Boot 3.2** (Web)
- **Thymeleaf** (Template engine)
- **Spring Data JPA** (Persistenza)
- **PostgreSQL** (Database produzione)
- **H2** (Database sviluppo)
- **iTextPDF** (Esportazione PDF)
- **Gson** (JSON)

## Autori

Carlo & AnnaMaria
