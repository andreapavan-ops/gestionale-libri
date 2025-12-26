package it.biblioteca.web.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "libri")
public class LibroEntity {

    @Id
    @Column(length = 20)
    private String isbn;

    @Column(nullable = false)
    private String titolo;

    private String autore;

    private int annoPubblicazione;

    private String genere;

    @OneToMany(mappedBy = "libro", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<RecensioneEntity> recensioni = new ArrayList<>();

    public LibroEntity() {}

    public LibroEntity(String isbn, String titolo, String autore, int annoPubblicazione, String genere) {
        this.isbn = isbn;
        this.titolo = titolo;
        this.autore = autore;
        this.annoPubblicazione = annoPubblicazione;
        this.genere = genere;
    }

    // Getter e Setter
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitolo() { return titolo; }
    public void setTitolo(String titolo) { this.titolo = titolo; }

    public String getAutore() { return autore; }
    public void setAutore(String autore) { this.autore = autore; }

    public int getAnnoPubblicazione() { return annoPubblicazione; }
    public void setAnnoPubblicazione(int annoPubblicazione) { this.annoPubblicazione = annoPubblicazione; }

    public String getGenere() { return genere; }
    public void setGenere(String genere) { this.genere = genere; }

    public List<RecensioneEntity> getRecensioni() { return recensioni; }
    public void setRecensioni(List<RecensioneEntity> recensioni) { this.recensioni = recensioni; }

    public void aggiungiRecensione(RecensioneEntity recensione) {
        recensioni.add(recensione);
        recensione.setLibro(this);
    }

    public double getMediaVoti() {
        if (recensioni == null || recensioni.isEmpty()) return 0;
        return recensioni.stream()
            .mapToInt(RecensioneEntity::getStelle)
            .average()
            .orElse(0);
    }

    public String getMediaVotiVisual() {
        double media = getMediaVoti();
        if (media == 0) return "Nessuna valutazione";
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            if (i <= media) sb.append("\u2605");
            else sb.append("\u2606");
        }
        return sb.toString() + " (" + String.format("%.1f", media) + ")";
    }
}
