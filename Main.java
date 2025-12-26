import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // Imposta look and feel cross-platform (uguale su Mac, Windows, Linux)
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            // Usa look and feel di default se non disponibile
        }

        // Avvia la GUI nel thread EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            GestionaleGUI gui = new GestionaleGUI();
            gui.setVisible(true);
        });
    }
}
