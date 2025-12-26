import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Comparator;
import java.util.ArrayList;

public class GestionaleGUI extends JFrame {
    private Biblioteca biblioteca;
    private JTable tabellaLibri;
    private DefaultTableModel modelloTabella;
    private JTextField txtIsbn, txtTitolo, txtAutore, txtAnno, txtGenere, txtCerca;
    private JPanel sidebar, areaCentrale, barraSuperiore, panelRicerca;
    private JLabel lblTitolo, lblInfo, labelCerca;
    private JButton btnCerca, btnMostraTutti;
    private JScrollPane scrollPane;
    private List<JButton> tuttiBottoni = new ArrayList<>();
    private List<JLabel> tutteLabel = new ArrayList<>();
    private List<JTextField> tuttiCampi = new ArrayList<>();

    // Tema corrente
    private boolean temaSuro = false;

    // Colori tema chiaro
    private final Color LIGHT_HEADER = new Color(128, 0, 32);  // Rosso bordeaux
    private final Color COLORE_BORDEAUX = new Color(128, 0, 32);  // Per bordi e testo
    private final Color COLORE_SFONDO_BTN = new Color(255, 245, 247);  // Rosa molto chiaro
    private final Color LIGHT_BG = new Color(236, 240, 241);
    private final Color LIGHT_SIDEBAR = new Color(240, 240, 240);
    private final Color LIGHT_TEXT = new Color(44, 62, 80);
    private final Color LIGHT_TABLE_HEADER = new Color(52, 73, 94);
    private final Color LIGHT_TABLE_ALT = new Color(245, 245, 245);

    // Colori tema scuro
    private final Color DARK_HEADER = new Color(30, 30, 30);
    private final Color DARK_BG = new Color(45, 45, 45);
    private final Color DARK_SIDEBAR = new Color(35, 35, 35);
    private final Color DARK_TEXT = new Color(220, 220, 220);
    private final Color DARK_TABLE_HEADER = new Color(25, 25, 25);
    private final Color DARK_TABLE_ALT = new Color(55, 55, 55);
    private final Color DARK_FIELD_BG = new Color(60, 60, 60);

    // Ordinamento tabella
    private int colonnaOrdinata = -1;
    private boolean ordinamentoAscendente = true;

    // Font che supporta le stelle Unicode su tutti i sistemi
    private static final Font FONT_STELLE = creaFontStelle(13);

    private static Font creaFontStelle(int size) {
        String[] fontNames = {"Segoe UI Symbol", "Apple Symbols", "Symbola", "DejaVu Sans", "Arial Unicode MS", "Dialog"};
        for (String fontName : fontNames) {
            Font font = new Font(fontName, Font.PLAIN, size);
            if (font.canDisplay('\u2605')) {  // ★
                return font;
            }
        }
        return new Font(Font.SANS_SERIF, Font.PLAIN, size);
    }

    public GestionaleGUI() {
        biblioteca = new Biblioteca();
        inizializzaGUI();
        caricaLibriNellaTabella();
    }

    private void inizializzaGUI() {
        setTitle("La Mia Biblioteca - Carlo & AnnaMaria");
        setSize(1100, 720);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout principale
        setLayout(new BorderLayout());

        // BARRA SUPERIORE
        barraSuperiore = creaBarraSuperiore();
        add(barraSuperiore, BorderLayout.NORTH);

        // SIDEBAR A SINISTRA
        sidebar = creaSidebar();
        add(sidebar, BorderLayout.WEST);

        // AREA CENTRALE (tabella libri)
        areaCentrale = creaAreaCentrale();
        add(areaCentrale, BorderLayout.CENTER);

        // Applica tema iniziale
        applicaTema();
    }

    private JPanel creaBarraSuperiore() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBorder(new EmptyBorder(15, 20, 15, 20));

        lblTitolo = new JLabel("LA MIA BIBLIOTECA - CARLO & ANNAMARIA -");
        lblTitolo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitolo.setForeground(Color.WHITE);

        // Pannello destro con info e toggle tema
        JPanel panelDestra = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelDestra.setOpaque(false);

        lblInfo = new JLabel("Libri totali: 0");
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 14));
        lblInfo.setForeground(Color.WHITE);
        lblInfo.setName("labelInfo");

        JButton btnTema = new JButton("Tema Scuro");
        btnTema.setFont(new Font("Arial", Font.BOLD, 11));
        btnTema.setForeground(COLORE_BORDEAUX);
        btnTema.setBackground(COLORE_SFONDO_BTN);
        btnTema.setFocusPainted(false);
        btnTema.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLORE_BORDEAUX, 2),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        btnTema.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTema.addActionListener(e -> {
            temaSuro = !temaSuro;
            btnTema.setText(temaSuro ? "Tema Chiaro" : "Tema Scuro");
            applicaTema();
        });

        panelDestra.add(lblInfo);
        panelDestra.add(btnTema);

        barra.add(lblTitolo, BorderLayout.WEST);
        barra.add(panelDestra, BorderLayout.EAST);

        return barra;
    }

    private JPanel creaAreaCentrale() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Barra di ricerca
        panelRicerca = new JPanel(new BorderLayout(10, 0));

        txtCerca = new JTextField();
        txtCerca.setFont(new Font("Arial", Font.PLAIN, 14));
        txtCerca.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        txtCerca.addActionListener(e -> cercaLibri());
        tuttiCampi.add(txtCerca);

        btnCerca = creaBottone("Cerca", COLORE_BORDEAUX);
        btnCerca.addActionListener(e -> cercaLibri());

        btnMostraTutti = creaBottone("Mostra tutti", COLORE_BORDEAUX);
        btnMostraTutti.addActionListener(e -> caricaLibriNellaTabella());

        JPanel panelBottoniRicerca = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        panelBottoniRicerca.setOpaque(false);
        panelBottoniRicerca.add(btnCerca);
        panelBottoniRicerca.add(btnMostraTutti);

        labelCerca = new JLabel("Cerca: ");
        tutteLabel.add(labelCerca);

        panelRicerca.add(labelCerca, BorderLayout.WEST);
        panelRicerca.add(txtCerca, BorderLayout.CENTER);
        panelRicerca.add(panelBottoniRicerca, BorderLayout.EAST);

        panel.add(panelRicerca, BorderLayout.NORTH);

        // Tabella libri con ordinamento e modifica diretta
        String[] colonne = {"ISBN", "Titolo", "Autore", "Anno", "Genere"};
        modelloTabella = new DefaultTableModel(colonne, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;  // Tutte le celle sono modificabili
            }
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 3) return Integer.class;
                return String.class;
            }
        };

        // Listener per salvare le modifiche quando si modifica una cella
        modelloTabella.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int riga = e.getFirstRow();
                int colonna = e.getColumn();
                if (riga >= 0 && colonna >= 0) {
                    salvaModificaDaTabella(riga);
                }
            }
        });

        tabellaLibri = new JTable(modelloTabella);
        tabellaLibri.setFont(FONT_STELLE);  // Font per supporto stelle Unicode
        tabellaLibri.setRowHeight(35);
        tabellaLibri.setIntercellSpacing(new Dimension(10, 5));
        tabellaLibri.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabellaLibri.setShowGrid(false);
        tabellaLibri.setShowHorizontalLines(true);

        // Header cliccabile per ordinamento
        JTableHeader header = tabellaLibri.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 13));
        header.setReorderingAllowed(false);
        header.setCursor(new Cursor(Cursor.HAND_CURSOR));
        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int colonna = header.columnAtPoint(e.getPoint());
                ordinaTabella(colonna);
            }
        });

        // Doppio click per caricare dati nei campi
        tabellaLibri.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int riga = tabellaLibri.getSelectedRow();
                    if (riga != -1) {
                        caricaDatiNeiCampi(riga);
                    }
                }
            }
        });

        // Larghezza colonne proporzionale
        tabellaLibri.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        TableColumnModel columnModel = tabellaLibri.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(120);
        columnModel.getColumn(1).setPreferredWidth(280);
        columnModel.getColumn(2).setPreferredWidth(200);
        columnModel.getColumn(3).setPreferredWidth(80);
        columnModel.getColumn(4).setPreferredWidth(120);

        scrollPane = new JScrollPane(tabellaLibri);
        scrollPane.setBorder(new LineBorder(new Color(189, 195, 199), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel creaSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(280, 0));
        sidebar.setBackground(new Color(240, 240, 240));
        sidebar.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 0, 1, new Color(200, 200, 200)),
            new EmptyBorder(20, 15, 20, 15)
        ));

        // Titolo
        JLabel lblSidebarTitolo = new JLabel("GESTIONE LIBRI");
        lblSidebarTitolo.setFont(new Font("Arial", Font.BOLD, 16));
        lblSidebarTitolo.setAlignmentX(Component.LEFT_ALIGNMENT);
        tutteLabel.add(lblSidebarTitolo);
        sidebar.add(lblSidebarTitolo);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        // Form campi
        txtIsbn = aggiungiCampo(sidebar, "ISBN:");
        txtTitolo = aggiungiCampo(sidebar, "Titolo:");
        txtAutore = aggiungiCampo(sidebar, "Autore:");
        txtAnno = aggiungiCampo(sidebar, "Anno:");
        txtGenere = aggiungiCampo(sidebar, "Genere:");

        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));

        // Bottone Aggiungi (stesso stile degli altri)
        JButton btnAggiungi = creaBottoneSidebar("Aggiungi Libro", COLORE_BORDEAUX, true);
        btnAggiungi.addActionListener(e -> aggiungiLibro());
        sidebar.add(btnAggiungi);

        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));

        JButton btnModifica = creaBottoneSidebar("Modifica Selezionato", COLORE_BORDEAUX, true);
        btnModifica.addActionListener(e -> modificaLibro());
        sidebar.add(btnModifica);

        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));

        JButton btnElimina = creaBottoneSidebar("Elimina Selezionato", COLORE_BORDEAUX, true);
        btnElimina.addActionListener(e -> eliminaLibro());
        sidebar.add(btnElimina);

        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        // Separatore
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sidebar.add(separator);

        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        // Pannello per bottoni esportazione affiancati
        JPanel panelEsporta = new JPanel(new GridLayout(1, 2, 8, 0));
        panelEsporta.setOpaque(false);
        panelEsporta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        panelEsporta.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnEsportaTxt = creaBottoneSmall("Esporta TXT", COLORE_BORDEAUX);
        btnEsportaTxt.addActionListener(e -> esportaTxt());
        panelEsporta.add(btnEsportaTxt);

        JButton btnEsportaPdf = creaBottoneSmall("Esporta PDF", COLORE_BORDEAUX);
        btnEsportaPdf.addActionListener(e -> esportaPdf());
        panelEsporta.add(btnEsportaPdf);

        sidebar.add(panelEsporta);

        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));

        JButton btnStatistiche = creaBottoneSidebar("Statistiche", COLORE_BORDEAUX, true);
        btnStatistiche.addActionListener(e -> mostraStatistiche());
        sidebar.add(btnStatistiche);

        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));

        JButton btnRecensioniWishlist = creaBottoneSidebar("Recensioni / Wishlist", COLORE_BORDEAUX, true);
        btnRecensioniWishlist.addActionListener(e -> mostraRecensioniWishlist());
        sidebar.add(btnRecensioniWishlist);

        sidebar.add(Box.createVerticalGlue());

        return sidebar;
    }

    private JTextField aggiungiCampo(JPanel panel, String label) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        tutteLabel.add(lbl);
        panel.add(lbl);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));

        JTextField txt = new JTextField();
        txt.setFont(new Font("Arial", Font.PLAIN, 13));
        txt.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        txt.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(5, 8, 5, 8)
        ));
        txt.setAlignmentX(Component.LEFT_ALIGNMENT);
        tuttiCampi.add(txt);
        panel.add(txt);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        return txt;
    }

    private JButton creaBottone(String testo, Color colore) {
        JButton btn = new JButton(testo);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setForeground(COLORE_BORDEAUX);  // Testo bordeaux
        btn.setBackground(COLORE_SFONDO_BTN);  // Sfondo rosa chiaro
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLORE_BORDEAUX, 2),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(255, 230, 235));  // Rosa più intenso al hover
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(COLORE_SFONDO_BTN);
            }
        });

        return btn;
    }

    private JButton creaBottoneSidebar(String testo, Color colore, boolean pieno) {
        JButton btn = new JButton(testo);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);

        // Stile uniforme: sfondo chiaro, bordo e testo bordeaux
        btn.setForeground(COLORE_BORDEAUX);
        btn.setBackground(COLORE_SFONDO_BTN);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLORE_BORDEAUX, 2),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(255, 230, 235));  // Rosa più intenso al hover
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(temaSuro ? new Color(60, 60, 60) : COLORE_SFONDO_BTN);
            }
        });

        tuttiBottoni.add(btn);
        return btn;
    }

    private JButton creaBottoneSmall(String testo, Color colore) {
        JButton btn = new JButton(testo);
        btn.setFont(new Font("Arial", Font.BOLD, 11));
        btn.setForeground(COLORE_BORDEAUX);
        btn.setBackground(COLORE_SFONDO_BTN);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLORE_BORDEAUX, 2),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(255, 230, 235));
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(temaSuro ? new Color(60, 60, 60) : COLORE_SFONDO_BTN);
            }
        });

        tuttiBottoni.add(btn);
        return btn;
    }

    private void applicaTema() {
        Color headerBg = temaSuro ? DARK_HEADER : LIGHT_HEADER;
        Color bg = temaSuro ? DARK_BG : LIGHT_BG;
        Color sidebarBg = temaSuro ? DARK_SIDEBAR : LIGHT_SIDEBAR;
        Color textColor = temaSuro ? DARK_TEXT : LIGHT_TEXT;
        Color tableHeaderBg = temaSuro ? DARK_TABLE_HEADER : LIGHT_TABLE_HEADER;
        Color tableAltRow = temaSuro ? DARK_TABLE_ALT : LIGHT_TABLE_ALT;
        Color fieldBg = temaSuro ? DARK_FIELD_BG : Color.WHITE;

        // Barra superiore
        barraSuperiore.setBackground(headerBg);

        // Area centrale
        areaCentrale.setBackground(bg);
        panelRicerca.setBackground(bg);

        // Sidebar
        sidebar.setBackground(sidebarBg);

        // Tabella
        tabellaLibri.setBackground(temaSuro ? DARK_BG : Color.WHITE);
        tabellaLibri.setForeground(textColor);
        tabellaLibri.setGridColor(temaSuro ? new Color(70, 70, 70) : new Color(220, 220, 220));
        tabellaLibri.getTableHeader().setBackground(tableHeaderBg);
        tabellaLibri.getTableHeader().setForeground(Color.WHITE);
        tabellaLibri.setSelectionBackground(temaSuro ? new Color(70, 130, 180) : new Color(85, 107, 47));
        tabellaLibri.setSelectionForeground(Color.WHITE);
        scrollPane.getViewport().setBackground(temaSuro ? DARK_BG : Color.WHITE);

        // Renderer per righe alternate
        tabellaLibri.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? (temaSuro ? DARK_BG : Color.WHITE) : tableAltRow);
                    c.setForeground(textColor);
                }
                ((JLabel) c).setBorder(new EmptyBorder(0, 10, 0, 10));
                return c;
            }
        });

        // Stesso renderer per Integer
        tabellaLibri.setDefaultRenderer(Integer.class, tabellaLibri.getDefaultRenderer(Object.class));

        // Label
        for (JLabel label : tutteLabel) {
            label.setForeground(textColor);
        }
        labelCerca.setForeground(textColor);

        // Bottoni ricerca (Cerca e Mostra tutti)
        btnCerca.setForeground(temaSuro ? Color.WHITE : Color.BLACK);
        btnMostraTutti.setForeground(temaSuro ? Color.WHITE : Color.BLACK);

        // Campi di testo
        for (JTextField field : tuttiCampi) {
            field.setBackground(fieldBg);
            field.setForeground(textColor);
            field.setCaretColor(textColor);
            field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(temaSuro ? new Color(100, 100, 100) : new Color(189, 195, 199), 1),
                new EmptyBorder(5, 8, 5, 8)
            ));
        }

        // Bottoni sidebar
        for (JButton btn : tuttiBottoni) {
            if (temaSuro) {
                btn.setBackground(new Color(60, 60, 60));
                btn.setForeground(Color.WHITE);
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            } else {
                btn.setBackground(Color.WHITE);
                btn.setForeground(Color.BLACK);
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        }

        // Aggiorna UI
        SwingUtilities.updateComponentTreeUI(this);
        repaint();
    }

    private void ordinaTabella(int colonna) {
        if (colonnaOrdinata == colonna) {
            ordinamentoAscendente = !ordinamentoAscendente;
        } else {
            colonnaOrdinata = colonna;
            ordinamentoAscendente = true;
        }

        List<Libro> libri = biblioteca.getTuttiLibri();

        Comparator<Libro> comparator = null;
        switch (colonna) {
            case 0: comparator = Comparator.comparing(Libro::getIsbn); break;
            case 1: comparator = Comparator.comparing(Libro::getTitolo, String.CASE_INSENSITIVE_ORDER); break;
            case 2: comparator = Comparator.comparing(Libro::getAutore, String.CASE_INSENSITIVE_ORDER); break;
            case 3: comparator = Comparator.comparingInt(Libro::getAnnoPubblicazione); break;
            case 4: comparator = Comparator.comparing(Libro::getGenere, String.CASE_INSENSITIVE_ORDER); break;
        }

        if (comparator != null) {
            if (!ordinamentoAscendente) {
                comparator = comparator.reversed();
            }
            libri.sort(comparator);
        }

        modelloTabella.setRowCount(0);
        for (Libro libro : libri) {
            modelloTabella.addRow(new Object[]{
                libro.getIsbn(),
                libro.getTitolo(),
                libro.getAutore(),
                libro.getAnnoPubblicazione(),
                libro.getGenere()
            });
        }

        // Aggiorna header per mostrare direzione ordinamento
        String[] colonne = {"ISBN", "Titolo", "Autore", "Anno", "Genere"};
        for (int i = 0; i < colonne.length; i++) {
            String nome = colonne[i];
            if (i == colonna) {
                nome += ordinamentoAscendente ? " ▲" : " ▼";
            }
            tabellaLibri.getColumnModel().getColumn(i).setHeaderValue(nome);
        }
        tabellaLibri.getTableHeader().repaint();
    }

    private void caricaDatiNeiCampi(int riga) {
        txtIsbn.setText((String) modelloTabella.getValueAt(riga, 0));
        txtTitolo.setText((String) modelloTabella.getValueAt(riga, 1));
        txtAutore.setText((String) modelloTabella.getValueAt(riga, 2));
        txtAnno.setText(String.valueOf(modelloTabella.getValueAt(riga, 3)));
        txtGenere.setText((String) modelloTabella.getValueAt(riga, 4));
    }

    private void caricaLibriNellaTabella() {
        modelloTabella.setRowCount(0);
        List<Libro> libri = biblioteca.getTuttiLibri();

        for (Libro libro : libri) {
            modelloTabella.addRow(new Object[]{
                libro.getIsbn(),
                libro.getTitolo(),
                libro.getAutore(),
                libro.getAnnoPubblicazione(),
                libro.getGenere()
            });
        }

        // Reset ordinamento visuale
        String[] colonne = {"ISBN", "Titolo", "Autore", "Anno", "Genere"};
        for (int i = 0; i < colonne.length; i++) {
            tabellaLibri.getColumnModel().getColumn(i).setHeaderValue(colonne[i]);
        }
        tabellaLibri.getTableHeader().repaint();
        colonnaOrdinata = -1;

        aggiornaContatore();
    }

    private void aggiungiLibro() {
        if (txtIsbn.getText().trim().isEmpty() || txtTitolo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "ISBN e Titolo sono obbligatori!", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String isbnError = Libro.getIsbnValidationMessage(txtIsbn.getText().trim());
        if (isbnError != null) {
            JOptionPane.showMessageDialog(this, isbnError + "\n\nFormati accettati:\n- ISBN-10: 0-306-40615-2\n- ISBN-13: 978-3-16-148410-0", "ISBN non valido", JOptionPane.ERROR_MESSAGE);
            txtIsbn.requestFocus();
            return;
        }

        try {
            int anno = Integer.parseInt(txtAnno.getText().trim());

            Libro libro = new Libro(
                txtIsbn.getText().trim(),
                txtTitolo.getText().trim(),
                txtAutore.getText().trim(),
                anno,
                txtGenere.getText().trim()
            );

            biblioteca.aggiungiLibro(libro);
            caricaLibriNellaTabella();
            pulisciCampi();
            JOptionPane.showMessageDialog(this, "Libro aggiunto con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Anno non valido!", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificaLibro() {
        int riga = tabellaLibri.getSelectedRow();
        if (riga == -1) {
            JOptionPane.showMessageDialog(this, "Seleziona un libro dalla tabella!", "Attenzione", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String isbn = (String) modelloTabella.getValueAt(riga, 0);
        biblioteca.rimuoviLibro(isbn);
        aggiungiLibro();
    }

    private void eliminaLibro() {
        int riga = tabellaLibri.getSelectedRow();
        if (riga == -1) {
            JOptionPane.showMessageDialog(this, "Seleziona un libro dalla tabella!", "Attenzione", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int conferma = JOptionPane.showConfirmDialog(this, "Sei sicuro di voler eliminare questo libro?", "Conferma", JOptionPane.YES_NO_OPTION);
        if (conferma == JOptionPane.YES_OPTION) {
            String isbn = (String) modelloTabella.getValueAt(riga, 0);
            biblioteca.rimuoviLibro(isbn);
            caricaLibriNellaTabella();
            pulisciCampi();
        }
    }

    private void salvaModificaDaTabella(int riga) {
        try {
            String isbn = (String) modelloTabella.getValueAt(riga, 0);
            String titolo = (String) modelloTabella.getValueAt(riga, 1);
            String autore = (String) modelloTabella.getValueAt(riga, 2);
            Object annoObj = modelloTabella.getValueAt(riga, 3);
            int anno = (annoObj instanceof Integer) ? (Integer) annoObj : Integer.parseInt(annoObj.toString());
            String genere = (String) modelloTabella.getValueAt(riga, 4);

            // Trova il libro originale e aggiornalo
            Libro libroOriginale = biblioteca.getTuttiLibri().stream()
                .filter(l -> l.getIsbn().equals(isbn))
                .findFirst()
                .orElse(null);

            if (libroOriginale != null) {
                libroOriginale.setTitolo(titolo);
                libroOriginale.setAutore(autore);
                libroOriginale.setAnnoPubblicazione(anno);
                libroOriginale.setGenere(genere);
                biblioteca.salvaLibri();
                aggiornaContatore();
            }
        } catch (Exception ex) {
            // Ignora errori di parsing durante la modifica
        }
    }

    private void cercaLibri() {
        String termine = txtCerca.getText().trim();
        if (termine.isEmpty()) {
            caricaLibriNellaTabella();
            return;
        }

        modelloTabella.setRowCount(0);
        List<Libro> risultati = biblioteca.cercaPerTitolo(termine);
        risultati.addAll(biblioteca.cercaPerAutore(termine));

        for (Libro libro : risultati) {
            modelloTabella.addRow(new Object[]{
                libro.getIsbn(),
                libro.getTitolo(),
                libro.getAutore(),
                libro.getAnnoPubblicazione(),
                libro.getGenere()
            });
        }

        aggiornaContatore();
    }

    private void pulisciCampi() {
        txtIsbn.setText("");
        txtTitolo.setText("");
        txtAutore.setText("");
        txtAnno.setText("");
        txtGenere.setText("");
        tabellaLibri.clearSelection();
    }

    private void esportaTxt() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salva esportazione TXT");
        fileChooser.setSelectedFile(new java.io.File("biblioteca_export.txt"));

        int risultato = fileChooser.showSaveDialog(this);
        if (risultato == JFileChooser.APPROVE_OPTION) {
            String percorso = fileChooser.getSelectedFile().getAbsolutePath();
            if (!percorso.endsWith(".txt")) {
                percorso += ".txt";
            }

            if (biblioteca.esportaTxt(percorso)) {
                JOptionPane.showMessageDialog(this, "Esportazione completata!\nFile: " + percorso, "Successo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Errore durante l'esportazione!", "Errore", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void mostraStatistiche() {
        SchermataStatistiche statistiche = new SchermataStatistiche(this, biblioteca);
        statistiche.setVisible(true);
    }

    private void mostraRecensioniWishlist() {
        SchermataRecensioniWishlist schermata = new SchermataRecensioniWishlist(this, biblioteca);
        schermata.setVisible(true);
        caricaLibriNellaTabella(); // Ricarica per mostrare eventuali nuove valutazioni
    }

    private void esportaPdf() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salva esportazione PDF");
        fileChooser.setSelectedFile(new java.io.File("biblioteca_export.pdf"));

        int risultato = fileChooser.showSaveDialog(this);
        if (risultato == JFileChooser.APPROVE_OPTION) {
            String percorso = fileChooser.getSelectedFile().getAbsolutePath();
            if (!percorso.endsWith(".pdf")) {
                percorso += ".pdf";
            }

            if (biblioteca.esportaPdf(percorso)) {
                JOptionPane.showMessageDialog(this, "PDF creato con successo!\nFile: " + percorso, "Successo", JOptionPane.INFORMATION_MESSAGE);

                int apri = JOptionPane.showConfirmDialog(this, "Vuoi aprire il PDF?", "Apri PDF", JOptionPane.YES_NO_OPTION);
                if (apri == JOptionPane.YES_OPTION) {
                    try {
                        java.awt.Desktop.getDesktop().open(new java.io.File(percorso));
                    } catch (Exception ex) {
                        System.err.println("Impossibile aprire il file: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Errore durante la creazione del PDF!", "Errore", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void aggiornaContatore() {
        lblInfo.setText("Libri totali: " + biblioteca.getNumeroLibri());
    }

    public static void main(String[] args) {
        try {
            // Cross-platform look and feel (uguale su Mac, Windows, Linux)
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            GestionaleGUI gui = new GestionaleGUI();
            gui.setVisible(true);
        });
    }
}
