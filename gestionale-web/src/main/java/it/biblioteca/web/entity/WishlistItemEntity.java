package it.biblioteca.web.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "wishlist")
public class WishlistItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String utente;

    @Column(nullable = false)
    private String titolo;

    private String autore;

    private String note;

    private String dataAggiunta;

    private boolean acquistato;

    public WishlistItemEntity() {}

    public WishlistItemEntity(String utente, String titolo, String autore, String note) {
        this.utente = utente;
        this.titolo = titolo;
        this.autore = autore;
        this.note = note;
        this.dataAggiunta = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        this.acquistato = false;
    }

    // Getter e Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUtente() { return utente; }
    public void setUtente(String utente) { this.utente = utente; }

    public String getTitolo() { return titolo; }
    public void setTitolo(String titolo) { this.titolo = titolo; }

    public String getAutore() { return autore; }
    public void setAutore(String autore) { this.autore = autore; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getDataAggiunta() { return dataAggiunta; }
    public void setDataAggiunta(String dataAggiunta) { this.dataAggiunta = dataAggiunta; }

    public boolean isAcquistato() { return acquistato; }
    public void setAcquistato(boolean acquistato) { this.acquistato = acquistato; }

    public String getStatoVisual() {
        return acquistato ? "Acquistato" : "Da acquistare";
    }
}
