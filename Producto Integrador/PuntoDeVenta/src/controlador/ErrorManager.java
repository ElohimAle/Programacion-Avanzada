package controlador;

import java.awt.Component;
import javax.swing.JOptionPane;

public class ErrorManager {
    public static void showError(Component parent, String mensaje, Exception ex) {
        String detalle = (ex != null && ex.getMessage() != null) ? "\n" + ex.getMessage() : "";
        JOptionPane.showMessageDialog(parent, mensaje + detalle, "Error", JOptionPane.ERROR_MESSAGE);
        log(ex);
    }

    public static void showWarning(Component parent, String mensaje) {
        JOptionPane.showMessageDialog(parent, mensaje, "Advertencia", JOptionPane.WARNING_MESSAGE);
    }

    public static void log(Exception ex) {
        if (ex != null) {
            ex.printStackTrace(System.err);
        }
    }
}
