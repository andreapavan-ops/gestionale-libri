import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class SchermataStatistiche extends JDialog {
    private List<Libro> libri;

    // Colori per i grafici
    private final Color[] COLORI_GRAFICO = {
        new Color(52, 152, 219),   // Blu
        new Color(46, 204, 113),   // Verde
        new Color(155, 89, 182),   // Viola
        new Color(241, 196, 15),   // Giallo
        new Color(231, 76, 60),    // Rosso
        new Color(26, 188, 156),   // Turchese
        new Color(230, 126, 34),   // Arancione
        new Color(149, 165, 166),  // Grigio
        new Color(52, 73, 94),     // Blu scuro
        new Color(192, 57, 43)     // Rosso scuro
    };

    public SchermataStatistiche(JFrame parent, Biblioteca biblioteca) {
        super(parent, "Statistiche Biblioteca", true);
        this.libri = biblioteca.getTuttiLibri();

        inizializzaGUI();
    }

    private void inizializzaGUI() {
        setSize(900, 700);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(236, 240, 241));

        // Titolo
        JLabel titolo = new JLabel("Statistiche Biblioteca", SwingConstants.CENTER);
        titolo.setFont(new Font("Arial", Font.BOLD, 24));
        titolo.setForeground(new Color(41, 128, 185));
        titolo.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(titolo, BorderLayout.NORTH);

        // Pannello principale con grafici
        JPanel pannelloGrafici = new JPanel(new GridLayout(1, 2, 20, 0));
        pannelloGrafici.setBackground(new Color(236, 240, 241));
        pannelloGrafici.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Grafico a torta - Generi
        pannelloGrafici.add(creaPannelloGraficoTorta());

        // Grafico a barre - Anni
        pannelloGrafici.add(creaPannelloGraficoBarre());

        add(pannelloGrafici, BorderLayout.CENTER);

        // Pannello statistiche testuali
        add(creaPannelloStatistiche(), BorderLayout.SOUTH);
    }

    private JPanel creaPannelloGraficoTorta() {
        JPanel pannello = new JPanel(new BorderLayout());
        pannello.setBackground(Color.WHITE);
        pannello.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel labelTitolo = new JLabel("Libri per Genere", SwingConstants.CENTER);
        labelTitolo.setFont(new Font("Arial", Font.BOLD, 16));
        labelTitolo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        pannello.add(labelTitolo, BorderLayout.NORTH);

        // Calcola distribuzione generi
        Map<String, Integer> generi = calcolaDistribuzioneGeneri();

        if (generi.isEmpty()) {
            JLabel noData = new JLabel("Nessun libro presente", SwingConstants.CENTER);
            noData.setForeground(Color.GRAY);
            pannello.add(noData, BorderLayout.CENTER);
        } else {
            pannello.add(new GraficoTorta(generi), BorderLayout.CENTER);
            pannello.add(creaLegenda(generi), BorderLayout.SOUTH);
        }

        return pannello;
    }

    private JPanel creaPannelloGraficoBarre() {
        JPanel pannello = new JPanel(new BorderLayout());
        pannello.setBackground(Color.WHITE);
        pannello.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel labelTitolo = new JLabel("Libri per Anno", SwingConstants.CENTER);
        labelTitolo.setFont(new Font("Arial", Font.BOLD, 16));
        labelTitolo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        pannello.add(labelTitolo, BorderLayout.NORTH);

        // Calcola distribuzione anni
        Map<Integer, Integer> anni = calcolaDistribuzioneAnni();

        if (anni.isEmpty()) {
            JLabel noData = new JLabel("Nessun libro presente", SwingConstants.CENTER);
            noData.setForeground(Color.GRAY);
            pannello.add(noData, BorderLayout.CENTER);
        } else {
            pannello.add(new GraficoBarre(anni), BorderLayout.CENTER);
        }

        return pannello;
    }

    private JPanel creaPannelloStatistiche() {
        JPanel pannello = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        pannello.setBackground(new Color(41, 128, 185));
        pannello.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Statistiche
        int totaleLibri = libri.size();
        int totaleGeneri = calcolaDistribuzioneGeneri().size();
        int totaleAutori = calcolaAutoriUnici();
        String annoPiuRecente = libri.isEmpty() ? "-" :
            String.valueOf(libri.stream().mapToInt(Libro::getAnnoPubblicazione).max().orElse(0));
        String annoPiuVecchio = libri.isEmpty() ? "-" :
            String.valueOf(libri.stream().mapToInt(Libro::getAnnoPubblicazione).min().orElse(0));

        pannello.add(creaStatLabel("Totale Libri", String.valueOf(totaleLibri)));
        pannello.add(creaStatLabel("Generi", String.valueOf(totaleGeneri)));
        pannello.add(creaStatLabel("Autori", String.valueOf(totaleAutori)));
        pannello.add(creaStatLabel("Anno più vecchio", annoPiuVecchio));
        pannello.add(creaStatLabel("Anno più recente", annoPiuRecente));

        return pannello;
    }

    private JPanel creaStatLabel(String nome, String valore) {
        JPanel pannello = new JPanel();
        pannello.setLayout(new BoxLayout(pannello, BoxLayout.Y_AXIS));
        pannello.setOpaque(false);

        JLabel labelValore = new JLabel(valore, SwingConstants.CENTER);
        labelValore.setFont(new Font("Arial", Font.BOLD, 28));
        labelValore.setForeground(Color.WHITE);
        labelValore.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel labelNome = new JLabel(nome, SwingConstants.CENTER);
        labelNome.setFont(new Font("Arial", Font.PLAIN, 12));
        labelNome.setForeground(new Color(200, 220, 240));
        labelNome.setAlignmentX(Component.CENTER_ALIGNMENT);

        pannello.add(labelValore);
        pannello.add(labelNome);

        return pannello;
    }

    private JPanel creaLegenda(Map<String, Integer> dati) {
        JPanel legenda = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        legenda.setBackground(Color.WHITE);

        int i = 0;
        for (Map.Entry<String, Integer> entry : dati.entrySet()) {
            JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
            item.setBackground(Color.WHITE);

            JPanel colorBox = new JPanel();
            colorBox.setBackground(COLORI_GRAFICO[i % COLORI_GRAFICO.length]);
            colorBox.setPreferredSize(new Dimension(12, 12));

            JLabel label = new JLabel(entry.getKey() + " (" + entry.getValue() + ")");
            label.setFont(new Font("Arial", Font.PLAIN, 11));

            item.add(colorBox);
            item.add(label);
            legenda.add(item);
            i++;
        }

        return legenda;
    }

    private Map<String, Integer> calcolaDistribuzioneGeneri() {
        Map<String, Integer> distribuzione = new LinkedHashMap<>();
        for (Libro libro : libri) {
            String genere = libro.getGenere();
            if (genere == null || genere.trim().isEmpty()) {
                genere = "Non specificato";
            }
            distribuzione.merge(genere, 1, Integer::sum);
        }
        // Ordina per quantità decrescente
        return distribuzione.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .collect(java.util.stream.Collectors.toMap(
                Map.Entry::getKey, Map.Entry::getValue,
                (e1, e2) -> e1, LinkedHashMap::new));
    }

    private Map<Integer, Integer> calcolaDistribuzioneAnni() {
        Map<Integer, Integer> distribuzione = new TreeMap<>();
        for (Libro libro : libri) {
            int anno = libro.getAnnoPubblicazione();
            distribuzione.merge(anno, 1, Integer::sum);
        }
        return distribuzione;
    }

    private int calcolaAutoriUnici() {
        Set<String> autori = new HashSet<>();
        for (Libro libro : libri) {
            if (libro.getAutore() != null && !libro.getAutore().trim().isEmpty()) {
                autori.add(libro.getAutore().toLowerCase().trim());
            }
        }
        return autori.size();
    }

    // Classe interna per il grafico a torta
    private class GraficoTorta extends JPanel {
        private Map<String, Integer> dati;
        private int totale;

        public GraficoTorta(Map<String, Integer> dati) {
            this.dati = dati;
            this.totale = dati.values().stream().mapToInt(Integer::intValue).sum();
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = Math.min(getWidth(), getHeight()) - 40;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            int startAngle = 0;
            int i = 0;

            for (Map.Entry<String, Integer> entry : dati.entrySet()) {
                int arcAngle = (int) Math.round(360.0 * entry.getValue() / totale);

                // Ultimo spicchio prende tutto lo spazio rimanente
                if (i == dati.size() - 1) {
                    arcAngle = 360 - startAngle;
                }

                g2d.setColor(COLORI_GRAFICO[i % COLORI_GRAFICO.length]);
                g2d.fillArc(x, y, size, size, startAngle, arcAngle);

                // Bordo bianco
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawArc(x, y, size, size, startAngle, arcAngle);

                startAngle += arcAngle;
                i++;
            }
        }
    }

    // Classe interna per il grafico a barre
    private class GraficoBarre extends JPanel {
        private Map<Integer, Integer> dati;
        private int maxValore;

        public GraficoBarre(Map<Integer, Integer> dati) {
            this.dati = dati;
            this.maxValore = dati.values().stream().mapToInt(Integer::intValue).max().orElse(1);
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int padding = 50;
            int areaWidth = getWidth() - padding * 2;
            int areaHeight = getHeight() - padding * 2;

            if (dati.isEmpty()) return;

            int barWidth = Math.min(50, (areaWidth / dati.size()) - 10);
            int spacing = (areaWidth - (barWidth * dati.size())) / (dati.size() + 1);

            // Asse Y
            g2d.setColor(new Color(189, 195, 199));
            g2d.drawLine(padding, padding, padding, getHeight() - padding);

            // Asse X
            g2d.drawLine(padding, getHeight() - padding, getWidth() - padding, getHeight() - padding);

            // Barre
            int x = padding + spacing;
            int i = 0;

            for (Map.Entry<Integer, Integer> entry : dati.entrySet()) {
                int barHeight = (int) ((double) entry.getValue() / maxValore * areaHeight);
                int y = getHeight() - padding - barHeight;

                // Barra
                g2d.setColor(COLORI_GRAFICO[i % COLORI_GRAFICO.length]);
                g2d.fillRoundRect(x, y, barWidth, barHeight, 5, 5);

                // Valore sopra la barra
                g2d.setColor(new Color(52, 73, 94));
                g2d.setFont(new Font("Arial", Font.BOLD, 11));
                String valore = String.valueOf(entry.getValue());
                int textWidth = g2d.getFontMetrics().stringWidth(valore);
                g2d.drawString(valore, x + (barWidth - textWidth) / 2, y - 5);

                // Anno sotto la barra
                g2d.setFont(new Font("Arial", Font.PLAIN, 10));
                String anno = String.valueOf(entry.getKey());
                textWidth = g2d.getFontMetrics().stringWidth(anno);
                g2d.drawString(anno, x + (barWidth - textWidth) / 2, getHeight() - padding + 15);

                x += barWidth + spacing;
                i++;
            }
        }
    }
}
