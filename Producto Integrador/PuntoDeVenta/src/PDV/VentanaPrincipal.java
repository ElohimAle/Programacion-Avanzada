package PDV;

import PDV.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class VentanaPrincipal extends JFrame {
    private CardLayout cardLayout;
    private JPanel contentPane;
    private JButton btnCerrarSesion;
    private JButton btnVentas;
    private JButton btnProductos;
    private JButton btnFacturas;
    private JButton btnCompras;
    private JButton btnInventario;

    public VentanaPrincipal() {
        setTitle("Punto de Venta");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Barra de navegación superior
        JPanel barraNavegacion = new JPanel();
        barraNavegacion.setBackground(Color.decode("#FECDCD"));
        barraNavegacion.setLayout(new GridLayout(1, 6));

        btnCerrarSesion = crearBotonConIcono("TERMINAR TURNO", "/resources/cerrar.png", 30, 30);
        btnCerrarSesion.addActionListener(e -> {
            // Ejemplo simple
            new VentanaCierreTurno().setVisible(true);
        });

        btnVentas = crearBotonConIcono("VENTAS", "/resources/carrito.png", 30, 30);
        btnProductos = crearBotonConIcono("PRODUCTOS", "/resources/producto.png", 30, 30);
        btnFacturas = crearBotonConIcono("FACTURAS", "/resources/factura.png", 30, 30);
        btnCompras = crearBotonConIcono("COMPRAS", "/resources/compras.png", 30, 30);
        btnInventario = crearBotonConIcono("INVENTARIO", "/resources/inventario.png", 30, 30);

        barraNavegacion.add(btnCerrarSesion);
        barraNavegacion.add(btnVentas);
        barraNavegacion.add(btnProductos);
        barraNavegacion.add(btnFacturas);
        barraNavegacion.add(btnCompras);
        barraNavegacion.add(btnInventario);

        barraNavegacion.setPreferredSize(new Dimension(1920, 83));
        add(barraNavegacion, BorderLayout.NORTH);

        // Panel central con CardLayout
        cardLayout = new CardLayout();
        contentPane = new JPanel(cardLayout);
        contentPane.setBackground(Color.decode("#FECDCD"));

        contentPane.add(new VentasPanel(), "ventas");
        contentPane.add(new ProductosPanel(), "productos");
        contentPane.add(new FacturasPanel(), "facturas");
        contentPane.add(new ComprasPanel(), "compras");
        contentPane.add(new InventarioPanel(), "inventario");

        add(contentPane, BorderLayout.CENTER);

        // Acciones de navegación
        ActionListener navegarAPanel = this::navegarEntrePaneles;
        btnVentas.addActionListener(navegarAPanel);
        btnProductos.addActionListener(navegarAPanel);
        btnFacturas.addActionListener(navegarAPanel);
        btnCompras.addActionListener(navegarAPanel);
        btnInventario.addActionListener(navegarAPanel);
    }

    private JButton crearBotonConIcono(String texto, String imagePath, int iconWidth, int iconHeight) {
        JButton boton = new JButton(texto);
        try {
            BufferedImage img = ImageUtils.resizeImage(imagePath, iconWidth, iconHeight);
            boton.setIcon(new ImageIcon(img));
        } catch (IOException e) {
            System.err.println("Error al cargar icono: " + imagePath);
            JOptionPane.showMessageDialog(this, "No se pudo cargar la imagen: " + imagePath);
        }

        // Estilo general
        boton.setBackground(Color.decode("#FECDCD"));
        boton.setForeground(Color.BLACK);
        boton.setFont(new Font("Arial", Font.BOLD, 16));
        boton.setFocusPainted(false);
        boton.setHorizontalTextPosition(SwingConstants.RIGHT);
        boton.setVerticalTextPosition(SwingConstants.CENTER);
        boton.setPreferredSize(new Dimension(200, 70)); // Botones uniformes
        return boton;
    }

    private void navegarEntrePaneles(ActionEvent e) {
        // Restablecer color de todos los botones
        btnVentas.setBackground(Color.decode("#FECDCD"));
        btnProductos.setBackground(Color.decode("#FECDCD"));
        btnFacturas.setBackground(Color.decode("#FECDCD"));
        btnCompras.setBackground(Color.decode("#FECDCD"));
        btnInventario.setBackground(Color.decode("#FECDCD"));

        // Actualizar según origen del evento
        if (e.getSource() == btnVentas) {
            cardLayout.show(contentPane, "ventas");
            btnVentas.setBackground(Color.decode("#9A4343"));
        } else if (e.getSource() == btnProductos) {
            cardLayout.show(contentPane, "productos");
            btnProductos.setBackground(Color.decode("#9A4343"));
        } else if (e.getSource() == btnFacturas) {
            cardLayout.show(contentPane, "facturas");
            btnFacturas.setBackground(Color.decode("#9A4343"));
        } else if (e.getSource() == btnCompras) {
            cardLayout.show(contentPane, "compras");
            btnCompras.setBackground(Color.decode("#9A4343"));
        } else if (e.getSource() == btnInventario) {
            cardLayout.show(contentPane, "inventario");
            btnInventario.setBackground(Color.decode("#9A4343"));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new VentanaPrincipal().setVisible(true);
        });
    }
}