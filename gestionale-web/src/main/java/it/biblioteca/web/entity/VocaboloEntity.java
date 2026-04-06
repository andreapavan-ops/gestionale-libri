package it.biblioteca.web.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "vocabolario")
public class VocaboloEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String parolaInglese;

    @Column(nullable = false)
    private String traduzione;

    private String contesto;

    private String dataAggiunta;

    // 0=nuovo, 1=visto, 2=quasi, 3=memorizzato
    private int livello = 0;

    public VocaboloEntity() {
        this.dataAggiunta = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public VocaboloEntity(String parolaInglese, String traduzione, String contesto) {
        this();
        this.parolaInglese = parolaInglese;
        this.traduzione = traduzione;
        this.contesto = contesto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getParolaInglese() { return parolaInglese; }
    public void setParolaInglese(String parolaInglese) { this.parolaInglese = parolaInglese; }

    public String getTraduzione() { return traduzione; }
    public void setTraduzione(String traduzione) { this.traduzione = traduzione; }

    public String getContesto() { return contesto; }
    public void setContesto(String contesto) { this.contesto = contesto; }

    public String getDataAggiunta() { return dataAggiunta; }
    public void setDataAggiunta(String dataAggiunta) { this.dataAggiunta = dataAggiunta; }

    public int getLivello() { return livello; }
    public void setLivello(int livello) { this.livello = livello; }

    public String getLivelloVisual() {
        return switch (livello) {
            case 0 -> "Nuovo";
            case 1 -> "Visto";
            case 2 -> "Quasi";
            case 3 -> "Memorizzato";
            default -> "Nuovo";
        };
    }

    public String getLivelloColore() {
        return switch (livello) {
            case 0 -> "#e74c3c";
            case 1 -> "#f39c12";
            case 2 -> "#3498db";
            case 3 -> "#27ae60";
            default -> "#e74c3c";
        };
    }
}
