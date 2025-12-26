import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class SchermataRecensioniWishlist extends JDialog {
    private Biblioteca biblioteca;
    private Wishlist wishlist;
    private String utenteCorrente = "Carlo";

    private JComboBox<String> comboUtente;
    private JLabel lblAvatar;
    private Map<String, ImageIcon> avatars;
    private JTable tabellaLibri;
    private DefaultTableModel modelloTabellaLibri;
    private JTable tabellaWishlist;
    private DefaultTableModel modelloTabellaWishlist;
    private JTextArea txtRecensioni;

    private final Color COLORE_PRINCIPALE = new Color(128, 0, 32);  // Rosso bordeaux
    private final Color COLORE_BORDEAUX = new Color(128, 0, 32);  // Per bordi e testo
    private final Color COLORE_SFONDO_BTN = new Color(255, 245, 247);  // Rosa molto chiaro
    private final Color COLORE_SFONDO = new Color(245, 245, 245);

    // Font che supporta le stelle Unicode su tutti i sistemi
    private static final Font FONT_STELLE = creaFontStelle(13);
    private static final Font FONT_STELLE_GRANDE = creaFontStelle(14);

    private static Font creaFontStelle(int size) {
        // Prova diversi font che supportano i simboli delle stelle
        String[] fontNames = {"Segoe UI Symbol", "Apple Symbols", "Symbola", "DejaVu Sans", "Arial Unicode MS", "Dialog"};
        for (String fontName : fontNames) {
            Font font = new Font(fontName, Font.PLAIN, size);
            if (font.canDisplay('\u2605')) {  // ★
                return font;
            }
        }
        return new Font(Font.SANS_SERIF, Font.PLAIN, size);
    }

    public SchermataRecensioniWishlist(JFrame parent, Biblioteca biblioteca) {
        super(parent, "Recensioni e Wishlist", true);
        this.biblioteca = biblioteca;
        this.wishlist = new Wishlist();

        // Crea gli avatar
        creaAvatars();

        inizializzaGUI();
        caricaDati();
    }

    private void creaAvatars() {
        avatars = new HashMap<>();
        // Avatar maschile per Carlo (blu)
        avatars.put("Carlo", creaAvatarIcon("C", new Color(41, 128, 185), true));
        // Avatar femminile per AnnaMaria (rosa)
        avatars.put("AnnaMaria", creaAvatarIcon("A", new Color(219, 112, 147), false));
    }

    private ImageIcon creaAvatarIcon(String iniziale, Color colore, boolean maschile) {
        int size = 40;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();

        // Antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Sfondo circolare
        g2d.setColor(colore);
        g2d.fillOval(0, 0, size, size);

        // Bordo
        g2d.setColor(colore.darker());
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(1, 1, size - 3, size - 3);

        // Simbolo genere piccolo in alto a destra
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        String simbolo = maschile ? "\u2642" : "\u2640"; // ♂ o ♀
        g2d.drawString(simbolo, size - 12, 12);

        // Iniziale al centro
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        FontMetrics fm = g2d.getFontMetrics();
        int x = (size - fm.stringWidth(iniziale)) / 2;
        int y = (size - fm.getHeight()) / 2 + fm.getAscent();
        g2d.drawString(iniziale, x, y);

        g2d.dispose();
        return new ImageIcon(img);
    }

    private void inizializzaGUI() {
        setSize(1000, 700);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(COLORE_SFONDO);

        // Header con selezione utente
        JPanel header = creaHeader();
        add(header, BorderLayout.NORTH);

        // Contenuto principale con tab
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));

        // Tab Recensioni
        JPanel panelRecensioni = creaPanelRecensioni();
        tabbedPane.addTab("Recensioni Libri", panelRecensioni);

        // Tab Wishlist
        JPanel panelWishlist = creaPanelWishlist();
        tabbedPane.addTab("Wishlist", panelWishlist);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel creaHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLORE_PRINCIPALE);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titolo = new JLabel("Gestione Recensioni e Wishlist");
        titolo.setFont(new Font("Arial", Font.BOLD, 20));
        titolo.setForeground(Color.WHITE);

        // Selezione utente con avatar
        JPanel panelUtente = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelUtente.setOpaque(false);

        // Avatar
        lblAvatar = new JLabel(avatars.get(utenteCorrente));
        lblAvatar.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        JLabel lblUtente = new JLabel("Utente: ");
        lblUtente.setForeground(Color.WHITE);
        lblUtente.setFont(new Font("Arial", Font.BOLD, 14));

        comboUtente = new JComboBox<>(new String[]{"Carlo", "AnnaMaria"});
        comboUtente.setFont(new Font("Arial", Font.PLAIN, 14));
        comboUtente.addActionListener(e -> {
            utenteCorrente = (String) comboUtente.getSelectedItem();
            // Aggiorna avatar
            lblAvatar.setIcon(avatars.get(utenteCorrente));
            caricaDati();
        });

        panelUtente.add(lblAvatar);
        panelUtente.add(lblUtente);
        panelUtente.add(comboUtente);

        header.add(titolo, BorderLayout.WEST);
        header.add(panelUtente, BorderLayout.EAST);

        return header;
    }

    private JPanel creaPanelRecensioni() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLORE_SFONDO);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Lista libri a sinistra
        JPanel panelLibri = new JPanel(new BorderLayout(5, 5));
        panelLibri.setBackground(Color.WHITE);
        panelLibri.setBorder(BorderFactory.createTitledBorder("Seleziona un libro"));

        String[] colonne = {"Titolo", "Autore", "Valutazione"};
        modelloTabellaLibri = new DefaultTableModel(colonne, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tabellaLibri = new JTable(modelloTabellaLibri);
        tabellaLibri.setRowHeight(30);
        tabellaLibri.setFont(FONT_STELLE);  // Font per stelle
        tabellaLibri.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabellaLibri.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostraRecensioniLibroSelezionato();
            }
        });

        JScrollPane scrollLibri = new JScrollPane(tabellaLibri);
        panelLibri.add(scrollLibri, BorderLayout.CENTER);

        // Pannello recensioni a destra
        JPanel panelRecensione = new JPanel(new BorderLayout(10, 10));
        panelRecensione.setBackground(Color.WHITE);
        panelRecensione.setBorder(BorderFactory.createTitledBorder("Recensioni"));

        txtRecensioni = new JTextArea();
        txtRecensioni.setEditable(false);
        txtRecensioni.setFont(FONT_STELLE_GRANDE);  // Font per stelle
        txtRecensioni.setLineWrap(true);
        txtRecensioni.setWrapStyleWord(true);
        JScrollPane scrollRecensioni = new JScrollPane(txtRecensioni);

        // Form per aggiungere recensione
        JPanel formRecensione = new JPanel(new GridBagLayout());
        formRecensione.setBackground(Color.WHITE);
        formRecensione.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblStelle = new JLabel(" Stelle (1-5):");
        lblStelle.setFont(new Font("Arial", Font.PLAIN, 13));
        lblStelle.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        lblStelle.setOpaque(true);
        lblStelle.setBackground(Color.WHITE);
        formRecensione.add(lblStelle, gbc);

        JSpinner spinnerStelle = new JSpinner(new SpinnerNumberModel(5, 1, 5, 1));
        spinnerStelle.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        formRecensione.add(spinnerStelle, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblCommento = new JLabel(" Commento:");
        lblCommento.setFont(new Font("Arial", Font.PLAIN, 13));
        lblCommento.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        lblCommento.setOpaque(true);
        lblCommento.setBackground(Color.WHITE);
        formRecensione.add(lblCommento, gbc);

        JTextField txtCommento = new JTextField(20);
        txtCommento.setFont(new Font("Arial", Font.PLAIN, 13));
        gbc.gridx = 1;
        formRecensione.add(txtCommento, gbc);

        JButton btnAggiungiRecensione = new JButton("Aggiungi Recensione");
        btnAggiungiRecensione.setBackground(COLORE_SFONDO_BTN);
        btnAggiungiRecensione.setForeground(COLORE_BORDEAUX);
        btnAggiungiRecensione.setFont(new Font("Arial", Font.BOLD, 14));
        btnAggiungiRecensione.setPreferredSize(new Dimension(200, 40));
        btnAggiungiRecensione.setFocusPainted(false);
        btnAggiungiRecensione.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLORE_BORDEAUX, 2),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        btnAggiungiRecensione.setCursor(new Cursor(Cursor.HAND_CURSOR));
        aggiungiEffettoHoverNuovo(btnAggiungiRecensione);
        btnAggiungiRecensione.addActionListener(e -> {
            int riga = tabellaLibri.getSelectedRow();
            if (riga == -1) {
                JOptionPane.showMessageDialog(this, "Seleziona un libro!", "Attenzione", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String titolo = (String) modelloTabellaLibri.getValueAt(riga, 0);
            Libro libro = biblioteca.cercaPerTitolo(titolo).stream().findFirst().orElse(null);

            if (libro != null) {
                int stelle = (int) spinnerStelle.getValue();
                String commento = txtCommento.getText().trim();
                if (commento.isEmpty()) commento = "Nessun commento";

                libro.aggiungiRecensione(new Recensione(utenteCorrente, stelle, commento));
                biblioteca.salvaLibri();
                caricaDati();
                mostraRecensioniLibroSelezionato();
                txtCommento.setText("");
                JOptionPane.showMessageDialog(this, "Recensione aggiunta!", "Successo", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formRecensione.add(btnAggiungiRecensione, gbc);

        panelRecensione.add(scrollRecensioni, BorderLayout.CENTER);
        panelRecensione.add(formRecensione, BorderLayout.SOUTH);

        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelLibri, panelRecensione);
        splitPane.setDividerLocation(450);
        splitPane.setResizeWeight(0.5);

        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel creaPanelWishlist() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLORE_SFONDO);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Tabella wishlist
        JPanel panelTabella = new JPanel(new BorderLayout());
        panelTabella.setBackground(Color.WHITE);
        panelTabella.setBorder(BorderFactory.createTitledBorder("Lista desideri di " + utenteCorrente));

        String[] colonne = {"Titolo", "Autore", "Note", "Data", "Stato"};
        modelloTabellaWishlist = new DefaultTableModel(colonne, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tabellaWishlist = new JTable(modelloTabellaWishlist);
        tabellaWishlist.setRowHeight(30);
        tabellaWishlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollWishlist = new JScrollPane(tabellaWishlist);
        panelTabella.add(scrollWishlist, BorderLayout.CENTER);

        // Form per aggiungere alla wishlist
        JPanel formWishlist = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        formWishlist.setBackground(Color.WHITE);
        formWishlist.setBorder(BorderFactory.createTitledBorder("Aggiungi alla wishlist"));

        formWishlist.add(new JLabel("Titolo:"));
        JTextField txtTitolo = new JTextField(15);
        formWishlist.add(txtTitolo);

        formWishlist.add(new JLabel("Autore:"));
        JTextField txtAutore = new JTextField(12);
        formWishlist.add(txtAutore);

        formWishlist.add(new JLabel("Note:"));
        JTextField txtNote = new JTextField(15);
        formWishlist.add(txtNote);

        JButton btnAggiungi = new JButton("Aggiungi");
        btnAggiungi.setBackground(COLORE_SFONDO_BTN);
        btnAggiungi.setForeground(COLORE_BORDEAUX);
        btnAggiungi.setFocusPainted(false);
        btnAggiungi.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLORE_BORDEAUX, 2),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        btnAggiungi.setCursor(new Cursor(Cursor.HAND_CURSOR));
        aggiungiEffettoHoverNuovo(btnAggiungi);
        btnAggiungi.addActionListener(e -> {
            String titolo = txtTitolo.getText().trim();
            String autore = txtAutore.getText().trim();
            String note = txtNote.getText().trim();

            if (titolo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Inserisci almeno il titolo!", "Attenzione", JOptionPane.WARNING_MESSAGE);
                return;
            }

            wishlist.aggiungiLibro(utenteCorrente, new LibroWishlist(titolo, autore, note));
            caricaWishlist();
            txtTitolo.setText("");
            txtAutore.setText("");
            txtNote.setText("");
            JOptionPane.showMessageDialog(this, "Libro aggiunto alla wishlist!", "Successo", JOptionPane.INFORMATION_MESSAGE);
        });
        formWishlist.add(btnAggiungi);

        // Bottoni azioni
        JPanel panelAzioni = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panelAzioni.setBackground(Color.WHITE);

        JButton btnAcquistato = new JButton("Segna Acquistato");
        btnAcquistato.setBackground(COLORE_SFONDO_BTN);
        btnAcquistato.setForeground(COLORE_BORDEAUX);
        btnAcquistato.setFocusPainted(false);
        btnAcquistato.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLORE_BORDEAUX, 2),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        btnAcquistato.setCursor(new Cursor(Cursor.HAND_CURSOR));
        aggiungiEffettoHoverNuovo(btnAcquistato);
        btnAcquistato.addActionListener(e -> {
            int riga = tabellaWishlist.getSelectedRow();
            if (riga == -1) {
                JOptionPane.showMessageDialog(this, "Seleziona un libro!", "Attenzione", JOptionPane.WARNING_MESSAGE);
                return;
            }
            wishlist.segnaAcquistato(utenteCorrente, riga);
            caricaWishlist();
        });

        JButton btnRimuovi = new JButton("Rimuovi");
        btnRimuovi.setBackground(COLORE_SFONDO_BTN);
        btnRimuovi.setForeground(COLORE_BORDEAUX);
        btnRimuovi.setFocusPainted(false);
        btnRimuovi.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLORE_BORDEAUX, 2),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        btnRimuovi.setCursor(new Cursor(Cursor.HAND_CURSOR));
        aggiungiEffettoHoverNuovo(btnRimuovi);
        btnRimuovi.addActionListener(e -> {
            int riga = tabellaWishlist.getSelectedRow();
            if (riga == -1) {
                JOptionPane.showMessageDialog(this, "Seleziona un libro!", "Attenzione", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int conferma = JOptionPane.showConfirmDialog(this, "Rimuovere dalla wishlist?", "Conferma", JOptionPane.YES_NO_OPTION);
            if (conferma == JOptionPane.YES_OPTION) {
                wishlist.rimuoviLibro(utenteCorrente, riga);
                caricaWishlist();
            }
        });

        panelAzioni.add(btnAcquistato);
        panelAzioni.add(btnRimuovi);

        JPanel panelSud = new JPanel(new BorderLayout());
        panelSud.setBackground(Color.WHITE);
        panelSud.add(formWishlist, BorderLayout.NORTH);
        panelSud.add(panelAzioni, BorderLayout.SOUTH);

        panel.add(panelTabella, BorderLayout.CENTER);
        panel.add(panelSud, BorderLayout.SOUTH);

        return panel;
    }

    private void caricaDati() {
        caricaLibriConRecensioni();
        caricaWishlist();
    }

    private void caricaLibriConRecensioni() {
        modelloTabellaLibri.setRowCount(0);
        List<Libro> libri = biblioteca.getTuttiLibri();

        for (Libro libro : libri) {
            modelloTabellaLibri.addRow(new Object[]{
                libro.getTitolo(),
                libro.getAutore(),
                libro.getMediaVotiVisual()
            });
        }
    }

    private void caricaWishlist() {
        modelloTabellaWishlist.setRowCount(0);
        List<LibroWishlist> lista = wishlist.getWishlist(utenteCorrente);

        for (LibroWishlist libro : lista) {
            modelloTabellaWishlist.addRow(new Object[]{
                libro.getTitolo(),
                libro.getAutore(),
                libro.getNote(),
                libro.getDataAggiunta(),
                libro.isAcquistato() ? "Acquistato" : "Da comprare"
            });
        }
    }

    private void mostraRecensioniLibroSelezionato() {
        int riga = tabellaLibri.getSelectedRow();
        if (riga == -1) {
            txtRecensioni.setText("");
            return;
        }

        String titolo = (String) modelloTabellaLibri.getValueAt(riga, 0);
        Libro libro = biblioteca.cercaPerTitolo(titolo).stream().findFirst().orElse(null);

        if (libro != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("RECENSIONI PER: ").append(libro.getTitolo()).append("\n");
            sb.append("Media: ").append(libro.getMediaVotiVisual()).append("\n");
            sb.append("─".repeat(40)).append("\n\n");

            List<Recensione> recensioni = libro.getRecensioni();
            if (recensioni.isEmpty()) {
                sb.append("Nessuna recensione ancora.\n");
                sb.append("Sii il primo a recensire questo libro!");
            } else {
                for (Recensione rec : recensioni) {
                    sb.append(rec.getUtente()).append("\n");
                    sb.append(rec.getStelleVisual()).append("\n");
                    sb.append("\"").append(rec.getCommento()).append("\"\n");
                    sb.append("Data: ").append(rec.getData()).append("\n\n");
                }
            }

            txtRecensioni.setText(sb.toString());
            txtRecensioni.setCaretPosition(0);
        }
    }

    private void aggiungiEffettoHoverNuovo(JButton btn) {
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(255, 230, 235));  // Rosa più intenso al hover
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(COLORE_SFONDO_BTN);
            }
        });
    }
}
