package PDV;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import database.DatabaseManager;

public class VentanaCierreTurno extends JFrame {
    private JTextField txtEfectivoInicial;
    private JTextField txtEfectivoActual;
    private JTextField txtDiferencia;
    private double efectivoInicial = 0.0;

    public VentanaCierreTurno() {
        setTitle("Cierre de Turno");
        setSize(540, 327);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Configurar layout principal
        setLayout(new BorderLayout());

        // Barra superior: "CIERRE DE TURNO"
        JPanel panelBarra = new JPanel();
        panelBarra.setBackground(Color.decode("#9A4343"));
        JLabel lblTitulo = new JLabel("CIERRE DE TURNO", SwingConstants.CENTER);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        panelBarra.add(lblTitulo);

        add(panelBarra, BorderLayout.NORTH);

        // Panel central: Contenido principal
        JPanel panelCentral = new JPanel();
        panelCentral.setBackground(Color.decode("#FECDCD"));
        panelCentral.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Agregar texto de instrucción
        JLabel lblInstruccion = new JLabel("Por favor cuenta el dinero en caja e ingrésalo para proceder con el cierre de turno.");
        lblInstruccion.setFont(new Font("Arial", Font.PLAIN, 10));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panelCentral.add(lblInstruccion, gbc);

        // Obtener efectivo inicial de la base de datos
        obtenerEfectivoInicial();

        // Etiqueta y campo de texto para efectivo inicial
        JLabel lblEfectivoInicial = new JLabel("Efectivo Inicial:");
        lblEfectivoInicial.setFont(new Font("Arial", Font.BOLD, 16));

        txtEfectivoInicial = new JTextField("$ " + String.format("%.2f", efectivoInicial));
        txtEfectivoInicial.setFont(new Font("Arial", Font.PLAIN, 18));
        txtEfectivoInicial.setHorizontalAlignment(SwingConstants.CENTER);
        txtEfectivoInicial.setPreferredSize(new Dimension(200, 40));
        txtEfectivoInicial.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        txtEfectivoInicial.setEditable(false);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panelCentral.add(lblEfectivoInicial, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panelCentral.add(txtEfectivoInicial, gbc);

        // Etiqueta y campo de texto para efectivo actual
        JLabel lblEfectivoActual = new JLabel("¿Cuánto efectivo hay en Caja?");
        lblEfectivoActual.setFont(new Font("Arial", Font.BOLD, 16));

        txtEfectivoActual = new JTextField("$ 0.00");
        txtEfectivoActual.setFont(new Font("Arial", Font.PLAIN, 18));
        txtEfectivoActual.setHorizontalAlignment(SwingConstants.CENTER);
        txtEfectivoActual.setPreferredSize(new Dimension(200, 40));
        txtEfectivoActual.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panelCentral.add(lblEfectivoActual, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panelCentral.add(txtEfectivoActual, gbc);

        // Etiqueta y campo de texto para diferencia
        JLabel lblDiferencia = new JLabel("Diferencia:");
        lblDiferencia.setFont(new Font("Arial", Font.BOLD, 16));

        txtDiferencia = new JTextField("$ 0.00");
        txtDiferencia.setFont(new Font("Arial", Font.PLAIN, 18));
        txtDiferencia.setHorizontalAlignment(SwingConstants.CENTER);
        txtDiferencia.setPreferredSize(new Dimension(200, 40));
        txtDiferencia.setEditable(false);
        txtDiferencia.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panelCentral.add(lblDiferencia, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panelCentral.add(txtDiferencia, gbc);

        // Botones "Cancelar" y "Cerrar Turno"
        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(Color.decode("#FECDCD"));
        panelBotones.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(Color.decode("#9A4343"));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(new Font("Arial", Font.BOLD, 16));
        btnCancelar.setBorder(BorderFactory.createLineBorder(Color.decode("#9A4343"), 2));
        btnCancelar.setFocusPainted(false);
        btnCancelar.setContentAreaFilled(true);

        JButton btnCerrarTurno = new JButton("Cerrar Turno");
        btnCerrarTurno.setBackground(Color.decode("#9A4343"));
        btnCerrarTurno.setForeground(Color.WHITE);
        btnCerrarTurno.setFont(new Font("Arial", Font.BOLD, 16));
        btnCerrarTurno.setBorder(BorderFactory.createLineBorder(Color.decode("#9A4343"), 2));
        btnCerrarTurno.setFocusPainted(false);
        btnCerrarTurno.setContentAreaFilled(true);

        panelBotones.add(btnCancelar);
        panelBotones.add(btnCerrarTurno);

        add(panelBotones, BorderLayout.SOUTH);

        // Acciones
        btnCancelar.addActionListener(e -> dispose());

        btnCerrarTurno.addActionListener(e -> cerrarTurno());

        add(panelCentral, BorderLayout.CENTER);
    }

    private void obtenerEfectivoInicial() {
        String sql = "SELECT efectivo_inicial FROM turnos WHERE id = ? AND fecha_cierre IS NULL";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Sesion.getTurnoId());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                efectivoInicial = rs.getDouble("efectivo_inicial");
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró un turno abierto");
                dispose();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al obtener datos del turno");
            dispose();
        }
    }

    private void cerrarTurno() {
        try {
            double efectivoActual = Double.parseDouble(txtEfectivoActual.getText().replace("$ ", ""));
            double diferencia = efectivoActual - efectivoInicial;

            // Actualizar el campo de diferencia
            txtDiferencia.setText("$ " + String.format("%.2f", diferencia));

            // Registrar cierre en base de datos
            if (actualizarTurnoEnBD(efectivoActual)) {
                JOptionPane.showMessageDialog(this,
                    "Turno cerrado. Diferencia: $" + String.format("%.2f", diferencia));

                // Limpiar sesión
                Sesion.setTurnoId(-1);

                // Cerrar ventanas y volver al login
                dispose();
                SwingUtilities.invokeLater(() -> new VentanaLogin().setVisible(true));
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar el cierre del turno");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, ingresa un monto válido.");
        }
    }

    private boolean actualizarTurnoEnBD(double efectivoFinal) {
        String sql = "UPDATE turnos SET fecha_cierre = datetime('now'), efectivo_final = ? " +
                     "WHERE id = ? AND fecha_cierre IS NULL";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, efectivoFinal);
            pstmt.setInt(2, Sesion.getTurnoId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new VentanaCierreTurno().setVisible(true);
        });
    }
}
