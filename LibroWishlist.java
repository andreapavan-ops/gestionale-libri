public class LibroWishlist {
    private String titolo;
    private String autore;
    private String note;
    private String dataAggiunta;
    private boolean acquistato;

    public LibroWishlist(String titolo, String autore, String note) {
        this.titolo = titolo;
        this.autore = autore;
        this.note = note;
        this.dataAggiunta = java.time.LocalDate.now().format(
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        this.acquistato = false;
    }

    // Getter
    public String getTitolo() { return titolo; }
    public String getAutore() { return autore; }
    public String getNote() { return note; }
    public String getDataAggiunta() { return dataAggiunta; }
    public boolean isAcquistato() { return acquistato; }

    // Setter
    public void setTitolo(String titolo) { this.titolo = titolo; }
    public void setAutore(String autore) { this.autore = autore; }
    public void setNote(String note) { this.note = note; }
    public void setAcquistato(boolean acquistato) { this.acquistato = acquistato; }

    @Override
    public String toString() {
        return titolo + " - " + autore + (acquistato ? " [ACQUISTATO]" : "");
    }
}
