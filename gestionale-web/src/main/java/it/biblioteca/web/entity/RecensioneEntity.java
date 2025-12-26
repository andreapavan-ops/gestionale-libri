package it.biblioteca.web.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "recensioni")
public class RecensioneEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String utente;
    private int stelle;
    private String commento;
    private String data;

    @ManyToOne
    @JoinColumn(name = "libro_isbn")
    private LibroEntity libro;

    public RecensioneEntity() {}

    public RecensioneEntity(String utente, int stelle, String commento) {
        this.utente = utente;
        this.stelle = Math.max(1, Math.min(5, stelle));
        this.commento = commento;
        this.data = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    // Getter e Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUtente() { return utente; }
    public void setUtente(String utente) { this.utente = utente; }

    public int getStelle() { return stelle; }
    public void setStelle(int stelle) { this.stelle = Math.max(1, Math.min(5, stelle)); }

    public String getCommento() { return commento; }
    public void setCommento(String commento) { this.commento = commento; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public LibroEntity getLibro() { return libro; }
    public void setLibro(LibroEntity libro) { this.libro = libro; }

    public String getStelleVisual() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            sb.append(i < stelle ? "\u2605" : "\u2606");
        }
        return sb.toString();
    }
}
