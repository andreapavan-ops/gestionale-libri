import java.util.ArrayList;
import java.util.List;

public class Libro {
    private String isbn;
    private String titolo;
    private String autore;
    private int annoPubblicazione;
    private String genere;
    private List<Recensione> recensioni;

    public Libro(String isbn, String titolo, String autore, int annoPubblicazione, String genere) {
        this.isbn = isbn;
        this.titolo = titolo;
        this.autore = autore;
        this.annoPubblicazione = annoPubblicazione;
        this.genere = genere;
        this.recensioni = new ArrayList<>();
    }

    // Getter
    public String getIsbn() { return isbn; }
    public String getTitolo() { return titolo; }
    public String getAutore() { return autore; }
    public int getAnnoPubblicazione() { return annoPubblicazione; }
    public String getGenere() { return genere; }
    public List<Recensione> getRecensioni() {
        if (recensioni == null) recensioni = new ArrayList<>();
        return recensioni;
    }

    // Setter
    public void setTitolo(String titolo) { this.titolo = titolo; }
    public void setAutore(String autore) { this.autore = autore; }
    public void setAnnoPubblicazione(int anno) { this.annoPubblicazione = anno; }
    public void setGenere(String genere) { this.genere = genere; }

    // Metodi per le recensioni
    public void aggiungiRecensione(Recensione recensione) {
        if (recensioni == null) recensioni = new ArrayList<>();
        // Rimuovi eventuale recensione precedente dello stesso utente
        recensioni.removeIf(r -> r.getUtente().equals(recensione.getUtente()));
        recensioni.add(recensione);
    }

    public Recensione getRecensione(String utente) {
        if (recensioni == null) return null;
        return recensioni.stream()
            .filter(r -> r.getUtente().equals(utente))
            .findFirst()
            .orElse(null);
    }

    public double getMediaVoti() {
        if (recensioni == null || recensioni.isEmpty()) return 0;
        return recensioni.stream()
            .mapToInt(Recensione::getStelle)
            .average()
            .orElse(0);
    }

    public String getMediaVotiVisual() {
        double media = getMediaVoti();
        if (media == 0) return "Nessuna valutazione";
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            if (i <= media) sb.append("\u2605");       // ★ stella piena
            else if (i - 0.5 <= media) sb.append("\u2605");
            else sb.append("\u2606");                   // ☆ stella vuota
        }
        return sb.toString() + " (" + String.format("%.1f", media) + ")";
    }
    
    @Override
    public String toString() {
        return String.format("ISBN: %s | Titolo: %s | Autore: %s | Anno: %d | Genere: %s",
                isbn, titolo, autore, annoPubblicazione, genere);
    }
    
    // Metodo per serializzare in formato testo
    public String toFileLine() {
        return isbn + ";" + titolo + ";" + autore + ";" + annoPubblicazione + ";" + genere;
    }
    
    // Metodo statico per creare un Libro da una riga di file
    public static Libro fromFileLine(String line) {
        String[] parti = line.split(";");
        if (parti.length == 5) {
            return new Libro(parti[0], parti[1], parti[2],
                           Integer.parseInt(parti[3]), parti[4]);
        }
        return null;
    }

    // Valida un ISBN (supporta ISBN-10 e ISBN-13)
    public static boolean isValidIsbn(String isbn) {
        if (isbn == null) return false;

        // Rimuove trattini e spazi
        String clean = isbn.replaceAll("[\\s-]", "");

        if (clean.length() == 10) {
            return isValidIsbn10(clean);
        } else if (clean.length() == 13) {
            return isValidIsbn13(clean);
        }
        return false;
    }

    // Valida ISBN-10
    private static boolean isValidIsbn10(String isbn) {
        int sum = 0;
        for (int i = 0; i < 10; i++) {
            char c = isbn.charAt(i);
            int digit;

            if (i == 9 && (c == 'X' || c == 'x')) {
                digit = 10;
            } else if (Character.isDigit(c)) {
                digit = Character.getNumericValue(c);
            } else {
                return false;
            }

            sum += digit * (10 - i);
        }
        return sum % 11 == 0;
    }

    // Valida ISBN-13
    private static boolean isValidIsbn13(String isbn) {
        if (!isbn.matches("\\d{13}")) return false;

        int sum = 0;
        for (int i = 0; i < 13; i++) {
            int digit = Character.getNumericValue(isbn.charAt(i));
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        return sum % 10 == 0;
    }

    // Restituisce un messaggio descrittivo sulla validità dell'ISBN
    public static String getIsbnValidationMessage(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return "ISBN non può essere vuoto";
        }

        String clean = isbn.replaceAll("[\\s-]", "");

        if (clean.length() != 10 && clean.length() != 13) {
            return "ISBN deve avere 10 o 13 caratteri (trovati: " + clean.length() + ")";
        }

        if (clean.length() == 10 && !isValidIsbn10(clean)) {
            return "ISBN-10 non valido (cifra di controllo errata)";
        }

        if (clean.length() == 13 && !isValidIsbn13(clean)) {
            return "ISBN-13 non valido (cifra di controllo errata)";
        }

        return null; // ISBN valido
    }
}