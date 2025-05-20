package PDV;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import funciones.BaseDeDatos;
import funciones.Producto;

public class ProductosPanel extends JPanel {
    private JTextField txtIdProducto;
    private JTextField txtNombreProducto;
    private JTextField txtPrecio;
    private JTable tablaProductos;
    private JLabel lblTotalProductos;

    public ProductosPanel() {
        // Inicializar la base de datos
        BaseDeDatos.inicializarBaseDatos();

        // Configurar layout principal
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // PANEL SUPERIOR - Título
        JPanel panelSuperior = new JPanel();
        panelSuperior.setBackground(Color.decode("#FECDCD"));
        JLabel lblTitulo = new JLabel("Gestión de Productos");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        panelSuperior.add(lblTitulo);

        add(panelSuperior, BorderLayout.NORTH);

        // SEPARADOR
        add(new JSeparator(SwingConstants.HORIZONTAL), BorderLayout.PAGE_START);

        // PANEL CENTRAL - Formulario y tabla
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel de formulario
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(Color.decode("#FECDCD"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Componentes del formulario
        JLabel lblIdProducto = new JLabel("ID (Código de Barras):");
        lblIdProducto.setFont(new Font("Arial", Font.BOLD, 14));

        txtIdProducto = new JTextField(20);
        txtIdProducto.setFont(new Font("Arial", Font.PLAIN, 14));
        txtIdProducto.setPreferredSize(new Dimension(200, 30));

        JLabel lblNombreProducto = new JLabel("Nombre del Producto:");
        lblNombreProducto.setFont(new Font("Arial", Font.BOLD, 14));

        txtNombreProducto = new JTextField(20);
        txtNombreProducto.setFont(new Font("Arial", Font.PLAIN, 14));
        txtNombreProducto.setPreferredSize(new Dimension(200, 30));

        JLabel lblPrecio = new JLabel("Precio:");
        lblPrecio.setFont(new Font("Arial", Font.BOLD, 14));

        txtPrecio = new JTextField(10);
        txtPrecio.setFont(new Font("Arial", Font.PLAIN, 14));
        txtPrecio.setPreferredSize(new Dimension(100, 30));

        JButton btnAgregarProducto = new JButton("Agregar Producto");
        estilizarBoton(btnAgregarProducto);
        btnAgregarProducto.setMargin(new Insets(8, 20, 8, 20));

        // Posicionamiento de componentes
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelFormulario.add(lblIdProducto, gbc);

        gbc.gridx = 1;
        panelFormulario.add(txtIdProducto, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panelFormulario.add(lblNombreProducto, gbc);

        gbc.gridx = 1;
        panelFormulario.add(txtNombreProducto, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panelFormulario.add(lblPrecio, gbc);

        gbc.gridx = 1;
        panelFormulario.add(txtPrecio, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        panelFormulario.add(btnAgregarProducto, gbc);

        // Configuración de la tabla
        String[] columnas = {"ID", "Nombre", "Precio"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer que la tabla no sea editable
            }
        };
        tablaProductos = new JTable(modelo);
        tablaProductos.setRowHeight(30);
        tablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaProductos.setBackground(Color.WHITE);
        tablaProductos.setGridColor(Color.LIGHT_GRAY);
        tablaProductos.getTableHeader().setBackground(Color.decode("#E6B9B9"));
        tablaProductos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        // Botones de acciones
        JButton btnEditarProducto = new JButton("Editar Producto");
        estilizarBoton(btnEditarProducto);

        JButton btnEliminarProducto = new JButton("Eliminar Producto");
        estilizarBoton(btnEliminarProducto);

        JPanel panelBotonesTabla = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotonesTabla.add(btnEditarProducto);
        panelBotonesTabla.add(btnEliminarProducto);

        // Organización del panel central
        panelCentral.add(panelFormulario, BorderLayout.NORTH);
        panelCentral.add(new JScrollPane(tablaProductos), BorderLayout.CENTER);
        panelCentral.add(panelBotonesTabla, BorderLayout.SOUTH);

        add(panelCentral, BorderLayout.CENTER);

        // PANEL INFERIOR - Total de productos
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBackground(Color.decode("#9A4343"));
        panelInferior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lblTotalProductos = new JLabel("Total de Productos: 0");
        lblTotalProductos.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotalProductos.setForeground(Color.WHITE);

        panelInferior.add(lblTotalProductos, BorderLayout.CENTER);

        add(panelInferior, BorderLayout.SOUTH);

        // Configuración de eventos
        btnAgregarProducto.addActionListener(e -> agregarProducto());
        btnEditarProducto.addActionListener(e -> editarProducto());
        btnEliminarProducto.addActionListener(e -> eliminarProducto());

        // Cargar productos al iniciar
        cargarProductos();
    }

    private void agregarProducto() {
        String codigo = txtIdProducto.getText().trim();
        String nombre = txtNombreProducto.getText().trim();
        String precioStr = txtPrecio.getText().trim();

        if (codigo.isEmpty() || nombre.isEmpty() || precioStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double precio = Double.parseDouble(precioStr);
            if (precio <= 0) {
                JOptionPane.showMessageDialog(this, "El precio debe ser mayor que cero", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Producto producto = new Producto(codigo, nombre, precio, 0);

            if (BaseDeDatos.guardarProducto(producto)) {
                JOptionPane.showMessageDialog(this, "Producto agregado con éxito", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarProductos();
                limpiarFormulario();
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar el producto", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El precio debe ser un número válido", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarProducto() {
        int filaSeleccionada = tablaProductos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para editar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String codigo = (String) tablaProductos.getValueAt(filaSeleccionada, 0);
        Producto producto = BaseDeDatos.obtenerProductoPorCodigo(codigo);

        if (producto != null) {
            JPanel panelEdicion = new JPanel(new GridLayout(3, 2, 5, 5));

            JTextField txtNombre = new JTextField(producto.getNombre());
            JTextField txtPrecio = new JTextField(String.valueOf(producto.getPrecio()));

            panelEdicion.add(new JLabel("Nombre:"));
            panelEdicion.add(txtNombre);
            panelEdicion.add(new JLabel("Precio:"));
            panelEdicion.add(txtPrecio);
            panelEdicion.add(new JLabel("Código:"));
            panelEdicion.add(new JLabel(producto.getCodigo())); // Código no editable

            int resultado = JOptionPane.showConfirmDialog(
                this,
                panelEdicion,
                "Editar Producto",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );

            if (resultado == JOptionPane.OK_OPTION) {
                try {
                    String nuevoNombre = txtNombre.getText().trim();
                    double nuevoPrecio = Double.parseDouble(txtPrecio.getText().trim());

                    if (nuevoNombre.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (nuevoPrecio <= 0) {
                        JOptionPane.showMessageDialog(this, "El precio debe ser mayor que cero", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    producto.setNombre(nuevoNombre);
                    producto.setPrecio(nuevoPrecio);

                    if (BaseDeDatos.guardarProducto(producto)) {
                        JOptionPane.showMessageDialog(this, "Producto actualizado con éxito", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        cargarProductos();
                    } else {
                        JOptionPane.showMessageDialog(this, "Error al actualizar el producto", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "El precio debe ser un número válido", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void eliminarProducto() {
        int filaSeleccionada = tablaProductos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para eliminar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String codigo = (String) tablaProductos.getValueAt(filaSeleccionada, 0);
        String nombre = (String) tablaProductos.getValueAt(filaSeleccionada, 1);

        int confirmacion = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro de eliminar el producto: " + nombre + "?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            if (BaseDeDatos.eliminarProducto(codigo)) {
                JOptionPane.showMessageDialog(this, "Producto eliminado con éxito", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarProductos();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar el producto", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cargarProductos() {
        DefaultTableModel modelo = (DefaultTableModel) tablaProductos.getModel();
        modelo.setRowCount(0);

        Map<String, Producto> productos = BaseDeDatos.obtenerTodosLosProductos();

        for (Producto p : productos.values()) {
            modelo.addRow(new Object[]{
                p.getCodigo(),
                p.getNombre(),
                String.format("$%.2f", p.getPrecio())
            });
        }

        lblTotalProductos.setText("Total de Productos: " + productos.size());
    }

    private void limpiarFormulario() {
        txtIdProducto.setText("");
        txtNombreProducto.setText("");
        txtPrecio.setText("");
        txtIdProducto.requestFocus();
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
            JFrame frame = new JFrame("Gestión de Productos");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(1000, 600);
            frame.add(new ProductosPanel());
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}