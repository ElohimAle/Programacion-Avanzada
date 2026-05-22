package PDV;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
        	controlador.DatabaseManager.initializeDatabase();
            controlador.BaseDeDatos.inicializarBaseDatos();
            new vista.VentanaLogin().setVisible(true);
        });
    }
}
