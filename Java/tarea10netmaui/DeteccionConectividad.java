package tarea10netmaui;

import javax.swing.*;
import java.awt.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class DeteccionConectividad extends JFrame {
    
    private JLabel estadoLabel;
    
    public DeteccionConectividad() {
        initUI();
        iniciarChequeoConectividad();
    }
    
    private void initUI() {
        setTitle("Detección de Conectividad de Red");
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        estadoLabel = new JLabel("Verificando conexión a Internet...", SwingConstants.CENTER);
        estadoLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        add(estadoLabel);
    }
   
    private void iniciarChequeoConectividad() {
        Thread hiloConectividad = new Thread(() -> {
            while (true) {
                boolean conectado = isConectadoInternet();
                actualizarEstado(conectado);
                try {
                    Thread.sleep(5000); 
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        hiloConectividad.setDaemon(true);
        hiloConectividad.start();
    }
    
    private boolean isConectadoInternet() {
        try {
            URL url = new URL("http://www.google.com");
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setConnectTimeout(3000);
            conexion.connect();
            int codigoRespuesta = conexion.getResponseCode();
            return codigoRespuesta == 200;
        } catch (Exception e) {
            return false;
        }
    }
    
    private void actualizarEstado(boolean conectado) {
        SwingUtilities.invokeLater(() -> {
            if (conectado) {
                estadoLabel.setText("Conexión a Internet disponible.");
                estadoLabel.setForeground(new Color(0, 128, 0));
            } else {
                estadoLabel.setText("No se detecta conexión a Internet.");
                estadoLabel.setForeground(Color.RED);
            }
        });
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DeteccionConectividad app = new DeteccionConectividad();
            app.setVisible(true);
        });
    }
}