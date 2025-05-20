package PDV;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import funciones.BaseDeDatos;

public class ComprasPanel extends JPanel {
    private JTextField txtNombreProducto;
    private JTextField txtCantidad;
    private JTextField txtPrecioUnitario;
    private JComboBox<String> cmbDistribuidor;
    private JTable tablaHistorialCompras;
    private DefaultTableModel modeloHistorial;
    private JLabel lblTotalCompras;
    private JButton btnRegistrarCompra;
    private JButton btnActualizar;

    public ComprasPanel() {
        // Configurar layout principal
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // PANEL SUPERIOR - Título y botones
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(Color.decode("#FECDCD"));

        JLabel lblTitulo = new JLabel("Gestión de Compras", SwingConstants.LEFT);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        panelSuperior.add(lblTitulo, BorderLayout.WEST);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        panelBotones.setBackground(Color.decode("#FECDCD"));

        btnActualizar = new JButton("Actualizar");
        estilizarBoton(btnActualizar);
        btnActualizar.addActionListener(e -> cargarHistorialCompras());

        panelBotones.add(btnActualizar);
        panelSuperior.add(panelBotones, BorderLayout.EAST);

        add(panelSuperior, BorderLayout.NORTH);

        // PANEL CENTRAL - Formulario y tabla
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel de formulario
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(Color.decode("#FECDCD"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Componentes del formulario
        JLabel lblNombreProducto = new JLabel("Nombre del Producto:");
        lblNombreProducto.setFont(new Font("Arial", Font.BOLD, 14));

        txtNombreProducto = new JTextField(20);
        txtNombreProducto.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel lblCantidad = new JLabel("Cantidad:");
        lblCantidad.setFont(new Font("Arial", Font.BOLD, 14));

        txtCantidad = new JTextField(10);
        txtCantidad.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel lblPrecioUnitario = new JLabel("Precio Unitario:");
        lblPrecioUnitario.setFont(new Font("Arial", Font.BOLD, 14));

        txtPrecioUnitario = new JTextField(10);
        txtPrecioUnitario.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel lblDistribuidor = new JLabel("Distribuidor:");
        lblDistribuidor.setFont(new Font("Arial", Font.BOLD, 14));

        // Obtener distribuidores de la base de datos
        List<String> distribuidores = BaseDeDatos.obtenerDistribuidores();
        cmbDistribuidor = new JComboBox<>(distribuidores.toArray(new String[0]));
        cmbDistribuidor.setFont(new Font("Arial", Font.PLAIN, 14));

        btnRegistrarCompra = new JButton("Registrar Compra");
        estilizarBoton(btnRegistrarCompra);
        btnRegistrarCompra.addActionListener(e -> registrarCompra());

        // Posicionamiento de componentes
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelFormulario.add(lblNombreProducto, gbc);

        gbc.gridx = 1;
        panelFormulario.add(txtNombreProducto, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panelFormulario.add(lblCantidad, gbc);

        gbc.gridx = 1;
        panelFormulario.add(txtCantidad, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panelFormulario.add(lblPrecioUnitario, gbc);

        gbc.gridx = 1;
        panelFormulario.add(txtPrecioUnitario, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panelFormulario.add(lblDistribuidor, gbc);

        gbc.gridx = 1;
        panelFormulario.add(cmbDistribuidor, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panelFormulario.add(btnRegistrarCompra, gbc);

        // Configuración de la tabla de historial
        String[] columnas = {"ID", "Fecha", "Producto", "Cantidad", "Precio Unitario", "Total", "Distribuidor"};
        modeloHistorial = new DefaultTableModel(columnas, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: return Integer.class;  // ID
                    case 2: return String.class;  // Producto
                    case 3: return Integer.class; // Cantidad
                    case 4: return Double.class;  // Precio Unitario
                    case 5: return Double.class;  // Total
                    case 6: return String.class;  // Distribuidor
                    default: return String.class; // Fecha
                }
            }
        };

        tablaHistorialCompras = new JTable(modeloHistorial);
        tablaHistorialCompras.setRowHeight(30);
        tablaHistorialCompras.setAutoCreateRowSorter(true);
        tablaHistorialCompras.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        tablaHistorialCompras.setFont(new Font("Arial", Font.PLAIN, 12));
        tablaHistorialCompras.setGridColor(Color.LIGHT_GRAY);

        JScrollPane scrollPane = new JScrollPane(tablaHistorialCompras);
        panelCentral.add(panelFormulario, BorderLayout.NORTH);
        panelCentral.add(scrollPane, BorderLayout.CENTER);

        add(panelCentral, BorderLayout.CENTER);

        // PANEL INFERIOR - Estadísticas
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBackground(Color.decode("#9A4343"));
        panelInferior.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        lblTotalCompras = new JLabel("Total de compras: 0", SwingConstants.CENTER);
        lblTotalCompras.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotalCompras.setForeground(Color.WHITE);

        panelInferior.add(lblTotalCompras, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);

        // Cargar datos iniciales
        cargarHistorialCompras();
    }

    private void registrarCompra() {
        String nombre = txtNombreProducto.getText().trim();
        String cantidadStr = txtCantidad.getText().trim();
        String precioStr = txtPrecioUnitario.getText().trim();
        String distribuidor = (String) cmbDistribuidor.getSelectedItem();

        if (nombre.isEmpty() || cantidadStr.isEmpty() || precioStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Todos los campos son obligatorios",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int cantidad = Integer.parseInt(cantidadStr);
            double precio = Double.parseDouble(precioStr);

            if (cantidad <= 0 || precio <= 0) {
                throw new NumberFormatException();
            }

            if (BaseDeDatos.registrarCompra(nombre, cantidad, precio, distribuidor)) {
                JOptionPane.showMessageDialog(this,
                    "Compra registrada con éxito",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                cargarHistorialCompras();
                limpiarFormulario();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al registrar la compra",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Cantidad y precio deben ser números válidos mayores que cero",
                "Error de formato",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarHistorialCompras() {
        modeloHistorial.setRowCount(0);
        List<Object[]> compras = BaseDeDatos.obtenerHistorialCompras();
        double totalCompras = 0;

        for (Object[] compra : compras) {
            modeloHistorial.addRow(compra);
            totalCompras += (Double) compra[5]; // Sumar al total
        }

        lblTotalCompras.setText(String.format("Total de compras: $%.2f", totalCompras));
    }

    private void limpiarFormulario() {
        txtNombreProducto.setText("");
        txtCantidad.setText("");
        txtPrecioUnitario.setText("");
        txtNombreProducto.requestFocus();
    }

    private void estilizarBoton(JButton boton) {
        boton.setBackground(Color.decode("#9A4343"));
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("Arial", Font.BOLD, 14));
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setContentAreaFilled(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Gestión de Compras");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(1000, 600);
            frame.add(new ComprasPanel());
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}