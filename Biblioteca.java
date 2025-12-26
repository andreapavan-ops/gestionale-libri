import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Biblioteca {
    private List<Libro> libri;
    private static final String FILE_JSON = "libri.json";
    private static final String FILE_TXT = "libri_export.txt";
    private Gson gson;

    public Biblioteca() {
        this.libri = new ArrayList<>();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        caricaLibri();
    }

    // Aggiunge un libro
    public void aggiungiLibro(Libro libro) {
        libri.add(libro);
        salvaLibri();
        System.out.println("✓ Libro aggiunto con successo!");
    }

    // Rimuove un libro per ISBN
    public boolean rimuoviLibro(String isbn) {
        boolean rimosso = libri.removeIf(l -> l.getIsbn().equals(isbn));
        if (rimosso) {
            salvaLibri();
            System.out.println("✓ Libro rimosso con successo!");
        } else {
            System.out.println("✗ Libro non trovato!");
        }
        return rimosso;
    }

    // Cerca libri per titolo (ricerca parziale)
    public List<Libro> cercaPerTitolo(String titolo) {
        List<Libro> risultati = new ArrayList<>();
        for (Libro libro : libri) {
            if (libro.getTitolo().toLowerCase().contains(titolo.toLowerCase())) {
                risultati.add(libro);
            }
        }
        return risultati;
    }

    // Cerca libri per autore
    public List<Libro> cercaPerAutore(String autore) {
        List<Libro> risultati = new ArrayList<>();
        for (Libro libro : libri) {
            if (libro.getAutore().toLowerCase().contains(autore.toLowerCase())) {
                risultati.add(libro);
            }
        }
        return risultati;
    }

    // Cerca libro per ISBN
    public Libro cercaPerIsbn(String isbn) {
        for (Libro libro : libri) {
            if (libro.getIsbn().equals(isbn)) {
                return libro;
            }
        }
        return null;
    }

    // Restituisce tutti i libri
    public List<Libro> getTuttiLibri() {
        return new ArrayList<>(libri);
    }

    // Conta i libri
    public int getNumeroLibri() {
        return libri.size();
    }

    // Salva i libri su file JSON
    public void salvaLibri() {
        try (Writer writer = new FileWriter(FILE_JSON)) {
            gson.toJson(libri, writer);
        } catch (IOException e) {
            System.err.println("Errore nel salvataggio JSON: " + e.getMessage());
        }
    }

    // Carica i libri da file JSON
    private void caricaLibri() {
        File fileJson = new File(FILE_JSON);

        // Se esiste il JSON, carica da lì
        if (fileJson.exists()) {
            try (Reader reader = new FileReader(fileJson)) {
                Type listType = new TypeToken<ArrayList<Libro>>(){}.getType();
                List<Libro> libriCaricati = gson.fromJson(reader, listType);
                if (libriCaricati != null) {
                    libri = libriCaricati;
                }
                System.out.println("Caricati " + libri.size() + " libri dal file JSON.");
            } catch (IOException e) {
                System.err.println("Errore nel caricamento JSON: " + e.getMessage());
            }
            return;
        }

        // Altrimenti prova a migrare dal vecchio file TXT
        File fileTxt = new File("libri.txt");
        if (fileTxt.exists()) {
            System.out.println("Migrazione dati da libri.txt a libri.json...");
            try (BufferedReader reader = new BufferedReader(new FileReader(fileTxt))) {
                String linea;
                while ((linea = reader.readLine()) != null) {
                    Libro libro = Libro.fromFileLine(linea);
                    if (libro != null) {
                        libri.add(libro);
                    }
                }
                salvaLibri(); // Salva in JSON
                System.out.println("Migrati " + libri.size() + " libri in formato JSON.");
            } catch (IOException e) {
                System.err.println("Errore nella migrazione: " + e.getMessage());
            }
        } else {
            System.out.println("Nessun file dati trovato. Biblioteca vuota.");
        }
    }

    // Esporta i libri in formato TXT leggibile
    public boolean esportaTxt(String percorso) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(percorso))) {
            writer.println("===================================================");
            writer.println("  LA MIA BIBLIOTECA - CARLO & ANNAMARIA");
            writer.println("===================================================");
            writer.println();
            writer.println("Totale libri: " + libri.size());
            writer.println();
            writer.println("-------------------------------------------");

            int i = 1;
            for (Libro libro : libri) {
                writer.println();
                writer.println("Libro #" + i++);
                writer.println("  ISBN:   " + libro.getIsbn());
                writer.println("  Titolo: " + libro.getTitolo());
                writer.println("  Autore: " + libro.getAutore());
                writer.println("  Anno:   " + libro.getAnnoPubblicazione());
                writer.println("  Genere: " + libro.getGenere());
                writer.println("-------------------------------------------");
            }

            writer.println();
            writer.println("Esportato il: " + java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

            return true;
        } catch (IOException e) {
            System.err.println("Errore nell'esportazione TXT: " + e.getMessage());
            return false;
        }
    }

    // Esporta nel percorso di default
    public boolean esportaTxt() {
        return esportaTxt(FILE_TXT);
    }

    // Esporta i libri in formato PDF
    public boolean esportaPdf(String percorso) {
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(percorso));
            document.open();

            // Font per il titolo
            Font fontTitolo = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, new BaseColor(41, 128, 185));
            Font fontSottotitolo = new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL, BaseColor.GRAY);
            Font fontIntestazione = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE);
            Font fontCella = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);

            // Titolo
            Paragraph titolo = new Paragraph("La Mia Biblioteca - Carlo & AnnaMaria", fontTitolo);
            titolo.setAlignment(Element.ALIGN_CENTER);
            titolo.setSpacingAfter(10);
            document.add(titolo);

            // Sottotitolo con data e conteggio
            String dataOra = java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            Paragraph sottotitolo = new Paragraph("Catalogo completo - " + libri.size() + " libri | Esportato il " + dataOra, fontSottotitolo);
            sottotitolo.setAlignment(Element.ALIGN_CENTER);
            sottotitolo.setSpacingAfter(20);
            document.add(sottotitolo);

            // Tabella
            PdfPTable tabella = new PdfPTable(5);
            tabella.setWidthPercentage(100);
            tabella.setWidths(new float[]{2f, 4f, 3f, 1.5f, 2f});

            // Colore intestazione
            BaseColor colorIntestazione = new BaseColor(41, 128, 185);

            // Intestazioni
            String[] intestazioni = {"ISBN", "Titolo", "Autore", "Anno", "Genere"};
            for (String intestazione : intestazioni) {
                PdfPCell cella = new PdfPCell(new Phrase(intestazione, fontIntestazione));
                cella.setBackgroundColor(colorIntestazione);
                cella.setPadding(8);
                cella.setHorizontalAlignment(Element.ALIGN_CENTER);
                tabella.addCell(cella);
            }

            // Righe alternare colori
            BaseColor colorRigaPari = new BaseColor(236, 240, 241);
            BaseColor colorRigaDispari = BaseColor.WHITE;

            int i = 0;
            for (Libro libro : libri) {
                BaseColor colorRiga = (i % 2 == 0) ? colorRigaPari : colorRigaDispari;

                aggiungiCellaPdf(tabella, libro.getIsbn(), fontCella, colorRiga);
                aggiungiCellaPdf(tabella, libro.getTitolo(), fontCella, colorRiga);
                aggiungiCellaPdf(tabella, libro.getAutore(), fontCella, colorRiga);
                aggiungiCellaPdf(tabella, String.valueOf(libro.getAnnoPubblicazione()), fontCella, colorRiga);
                aggiungiCellaPdf(tabella, libro.getGenere(), fontCella, colorRiga);

                i++;
            }

            document.add(tabella);

            // Footer
            Paragraph footer = new Paragraph("\nGenerato da Gestionale Biblioteca", fontSottotitolo);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            return true;

        } catch (DocumentException | FileNotFoundException e) {
            System.err.println("Errore nell'esportazione PDF: " + e.getMessage());
            return false;
        }
    }

    private void aggiungiCellaPdf(PdfPTable tabella, String testo, Font font, BaseColor colore) {
        PdfPCell cella = new PdfPCell(new Phrase(testo, font));
        cella.setBackgroundColor(colore);
        cella.setPadding(6);
        tabella.addCell(cella);
    }
}
