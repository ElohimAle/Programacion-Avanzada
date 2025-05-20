package PDV;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
        	database.DatabaseManager.initializeDatabase();
            new VentanaLogin().setVisible(true);
        });
    }
}
