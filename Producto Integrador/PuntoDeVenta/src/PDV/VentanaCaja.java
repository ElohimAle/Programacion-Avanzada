package PDV;

import database.DatabaseManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class VentanaCaja extends JFrame {
    private JTextField txtEfectivoInicial;

    public VentanaCaja() {
        setTitle("Inicio de Turno");
        setSize(506, 285);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel superior: título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(Color.decode("#9A4343"));
        JLabel lblTitulo = new JLabel("INICIO DE TURNO", SwingConstants.CENTER);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        panelTitulo.add(lblTitulo);
        add(panelTitulo, BorderLayout.NORTH);

        // Panel central: formulario
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(Color.decode("#FECDCD"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Etiqueta y campo de texto
        JLabel lblEfectivo = new JLabel("Efectivo inicial en caja:");
        lblEfectivo.setFont(new Font("Arial", Font.BOLD, 16));

        txtEfectivoInicial = new JTextField("$ 0.00");
        txtEfectivoInicial.setFont(new Font("Arial", Font.PLAIN, 18));
        txtEfectivoInicial.setHorizontalAlignment(SwingConstants.CENTER);
        txtEfectivoInicial.setPreferredSize(new Dimension(400, 40));

        // Botón Confirmar
        JButton btnConfirmar = new JButton("Confirmar");
        configurarBoton(btnConfirmar);

        // Posicionar componentes
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelFormulario.add(lblEfectivo, gbc);

        gbc.gridy = 1;
        panelFormulario.add(txtEfectivoInicial, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        panelFormulario.add(btnConfirmar, gbc);

        add(panelFormulario, BorderLayout.CENTER);

        // Acción del botón
        btnConfirmar.addActionListener(e -> confirmarEfectivoInicial());
    }

    private void configurarBoton(JButton boton) {
        boton.setBackground(Color.decode("#9A4343"));
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("Arial", Font.BOLD, 16));
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setContentAreaFilled(true);
    }

    private void confirmarEfectivoInicial() {
        try {
            double monto = Double.parseDouble(txtEfectivoInicial.getText().replace("$ ", ""));

            // Registrar turno en base de datos
            int turnoId = registrarTurno(monto);

            if (turnoId > 0) {
                Sesion.setTurnoId(turnoId);
                JOptionPane.showMessageDialog(this, "Turno iniciado con éxito. Efectivo inicial: $" + monto);
                dispose(); // Cerrar esta ventana
                new VentanaPrincipal().setVisible(true); // Abrir VentanaPrincipal
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar el turno.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un monto válido");
        }
    }

    private int registrarTurno(double efectivoInicial) {
        String sql = "INSERT INTO turnos (usuario_id, fecha_apertura, efectivo_inicial) VALUES (?, datetime('now'), ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, Sesion.getUsuarioId());
            pstmt.setDouble(2, efectivoInicial);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al registrar turno: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "No se pudo iniciar el turno: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return -1;
    }
}