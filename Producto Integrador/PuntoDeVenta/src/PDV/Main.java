package PDV;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
        	controlador.DatabaseManager.initializeDatabase();
            new vista.VentanaLogin().setVisible(true);
        });
    }
}
