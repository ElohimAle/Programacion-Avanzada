package PDV;

import database.DatabaseManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VentanaLogin extends JFrame {
    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JButton btnIngresar;

    public VentanaLogin() {
        setTitle("Login");
        setSize(1920, 1080);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panelLogin = new JPanel();
        panelLogin.setBackground(Color.decode("#FECDCD"));
        panelLogin.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        try {
            BufferedImage carritoImg = ImageUtils.resizeImage("/resources/carrito.png", 118, 100);
            JLabel lblCarrito = new JLabel(new ImageIcon(carritoImg));
            gbc.gridx = 0;
            gbc.gridy = 0;
            panelLogin.add(lblCarrito, gbc);

            JLabel lblTitulo = new JLabel("TIENDA ABRAROTES");
            lblTitulo.setFont(new Font("Khmer Sleokchher", Font.BOLD, 48));
            gbc.gridy = 1;
            panelLogin.add(lblTitulo, gbc);

            BufferedImage usuarioImg = ImageUtils.resizeImage("/resources/usuario.png", 30, 30);
            JLabel lblUsuario = new JLabel("USUARIO:");
            lblUsuario.setIcon(new ImageIcon(usuarioImg));
            lblUsuario.setHorizontalTextPosition(SwingConstants.RIGHT);
            gbc.gridy = 2;
            panelLogin.add(lblUsuario, gbc);

            txtUsuario = new JTextField();
            txtUsuario.setPreferredSize(new Dimension(400, 30));
            txtUsuario.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            gbc.gridy = 3;
            panelLogin.add(txtUsuario, gbc);

            BufferedImage contrasenaImg = ImageUtils.resizeImage("/resources/contraseña.png", 30, 30);
            JLabel lblContrasena = new JLabel("CONTRASEÑA:");
            lblContrasena.setIcon(new ImageIcon(contrasenaImg));
            lblContrasena.setHorizontalTextPosition(SwingConstants.RIGHT);
            gbc.gridy = 4;
            panelLogin.add(lblContrasena, gbc);

            txtContrasena = new JPasswordField();
            txtContrasena.setPreferredSize(new Dimension(400, 30));
            txtContrasena.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            gbc.gridy = 5;
            panelLogin.add(txtContrasena, gbc);

            btnIngresar = new JButton("INGRESAR");
            btnIngresar.setBackground(Color.decode("#9A4343"));
            btnIngresar.setForeground(Color.WHITE);
            btnIngresar.setFont(new Font("Khmer Sleokchher", Font.BOLD, 16));
            btnIngresar.setBorderPainted(false);
            btnIngresar.setFocusPainted(false);
            btnIngresar.setContentAreaFilled(true);
            gbc.gridy = 6;
            gbc.insets = new Insets(50, 10, 10, 10);
            panelLogin.add(btnIngresar, gbc);

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar las imágenes.");
        }

        add(panelLogin, BorderLayout.WEST);

        JPanel panelPatron = new JPanel();
        panelPatron.setBackground(Color.decode("#FECDCD"));
        panelPatron.setLayout(new BorderLayout());
        try {
            BufferedImage patronImg = ImageUtils.resizeImage("/resources/patron_iconos.png", getWidth(), getHeight());
            JLabel lblPatron = new JLabel(new ImageIcon(patronImg));
            panelPatron.add(lblPatron, BorderLayout.CENTER);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar el patrón de iconos.");
        }

        add(panelPatron, BorderLayout.EAST);

        // Acciones
        btnIngresar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String usuario = txtUsuario.getText();
                String contrasena = new String(txtContrasena.getPassword());

                if (validarCredenciales1(usuario, contrasena)) {
                    // Cierra la ventana de login
                    dispose();

                    // Abre la ventana de caja SOLO UNA VEZ ✅
                    new VentanaCaja().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(VentanaLogin.this, "Usuario o contraseña incorrectos.");
                }
            }
        });
    }

    private boolean validarCredenciales1(String usuario, String contrasena) {
        String sql = "SELECT id, nombre, rol FROM usuarios WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario);
            pstmt.setString(2, contrasena);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("id");
                    String nombre = rs.getString("nombre");
                    String rol = rs.getString("rol");

                    Sesion.setUsuarioId(userId);
                    Sesion.setNombreUsuario(nombre);
                    Sesion.setRolUsuario(rol);

                    JOptionPane.showMessageDialog(this, "Bienvenido, " + nombre);
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos");
        }

        return false;
    }

    private boolean validarCredenciales(String usuario, String contrasena) {
        return usuario.equals("admin") && contrasena.equals("1234");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaLogin().setVisible(true));
    }
}