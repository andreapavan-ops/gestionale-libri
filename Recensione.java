public class Recensione {
    private String utente;
    private int stelle; // da 1 a 5
    private String commento;
    private String data;

    public Recensione(String utente, int stelle, String commento) {
        this.utente = utente;
        this.stelle = Math.max(1, Math.min(5, stelle)); // Limita tra 1 e 5
        this.commento = commento;
        this.data = java.time.LocalDate.now().format(
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    // Getter
    public String getUtente() { return utente; }
    public int getStelle() { return stelle; }
    public String getCommento() { return commento; }
    public String getData() { return data; }

    // Setter
    public void setStelle(int stelle) {
        this.stelle = Math.max(1, Math.min(5, stelle));
    }
    public void setCommento(String commento) { this.commento = commento; }

    // Restituisce le stelle come stringa di simboli
    public String getStelleVisual() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            sb.append(i < stelle ? "\u2605" : "\u2606");  // ★ e ☆ usando codici Unicode
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return utente + ": " + getStelleVisual() + " - " + commento + " (" + data + ")";
    }
}
