package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import controlador.BaseDeDatos;
import modelo.Factura;
import modelo.Producto;

public class VentasPanel extends JPanel {
    private static final int COL_IMAGEN = 0;
    private static final int COL_CODIGO = 1;
    private static final int COL_DESCRIPCION = 2;
    private static final int COL_PRECIO = 3;
    private static final int COL_CANTIDAD = 4;
    private static final int COL_IMPORTE = 5;

    private JTextField txtCodigoBarra;
    private JTextField txtDescuento;
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    private JLabel lblTotal;
    private JLabel lblTotalPagar;

    public VentasPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 40, 20, 40));

        // PANEL SUPERIOR - Título y código de barras
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(Color.WHITE);
        panelSuperior.setBorder(new EmptyBorder(0, 0, 30, 0));

        JLabel lblTitulo = new JLabel("VENTAS");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 36));
        panelSuperior.add(lblTitulo, BorderLayout.WEST);

        JPanel panelCodigo = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        panelCodigo.setBackground(Color.WHITE);
        txtCodigoBarra = new JTextField(24);
        txtCodigoBarra.setFont(new Font("Arial", Font.PLAIN, 18));
        txtDescuento = new JTextField("0", 6);
        txtDescuento.setFont(new Font("Arial", Font.PLAIN, 18));
        JButton btnAnadirProducto = new JButton("Añadir producto");
        btnAnadirProducto.setFont(new Font("Arial", Font.BOLD, 18));
        btnAnadirProducto.setBackground(new Color(0x9A, 0x43, 0x43));
        btnAnadirProducto.setForeground(Color.WHITE);
        btnAnadirProducto.setFocusPainted(false);
        btnAnadirProducto.setPreferredSize(new Dimension(200, 40));
        JButton btnBuscarProducto = new JButton("Buscar");
        btnBuscarProducto.setFont(new Font("Arial", Font.BOLD, 16));
        btnBuscarProducto.setBackground(new Color(0x9A, 0x43, 0x43));
        btnBuscarProducto.setForeground(Color.WHITE);
        btnBuscarProducto.setFocusPainted(false);
        JButton btnEliminarProducto = new JButton("Eliminar seleccionado");
        btnEliminarProducto.setFont(new Font("Arial", Font.BOLD, 16));
        btnEliminarProducto.setBackground(new Color(0x9A, 0x43, 0x43));
        btnEliminarProducto.setForeground(Color.WHITE);
        btnEliminarProducto.setFocusPainted(false);

        panelCodigo.add(new JLabel("Código de Barras:"));
        panelCodigo.add(txtCodigoBarra);
        panelCodigo.add(btnAnadirProducto);
        panelCodigo.add(btnBuscarProducto);
        panelCodigo.add(btnEliminarProducto);
        panelCodigo.add(new JLabel("Descuento $:"));
        panelCodigo.add(txtDescuento);
        panelSuperior.add(panelCodigo, BorderLayout.CENTER);
        add(panelSuperior, BorderLayout.NORTH);

        // PANEL CENTRAL - Tabla de productos
        String[] columnas = {"Imagen", "Codigo", "Descripcion", "Precio Unitario", "Cantidad", "Importe"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == COL_CANTIDAD; // Solo cantidad es editable
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == COL_IMAGEN ? ImageIcon.class : Object.class;
            }
        };

        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setRowHeight(64);
        tablaProductos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        tablaProductos.setFont(new Font("Arial", Font.PLAIN, 12));
        tablaProductos.setGridColor(Color.LIGHT_GRAY);
        tablaProductos.getColumnModel().getColumn(COL_IMAGEN).setPreferredWidth(72);
        tablaProductos.getColumnModel().getColumn(COL_CODIGO).setPreferredWidth(90);
        tablaProductos.getColumnModel().getColumn(COL_DESCRIPCION).setPreferredWidth(420);

        JScrollPane scrollTabla = new JScrollPane(tablaProductos);
        scrollTabla.setPreferredSize(new Dimension(1800, 700));
        add(scrollTabla, BorderLayout.CENTER);

        // PANEL INFERIOR - Total
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBackground(new Color(0x9A, 0x43, 0x43));
        panelInferior.setBorder(new EmptyBorder(15, 20, 15, 20));
        panelInferior.setPreferredSize(new Dimension(1920, 80));

        lblTotal = new JLabel("SUBTOTAL $ 0.00", SwingConstants.LEFT);
        lblTotal.setFont(new Font("Arial", Font.BOLD, 28));
        lblTotal.setForeground(Color.WHITE);

        lblTotalPagar = new JLabel("TOTAL A PAGAR $ 0.00", SwingConstants.RIGHT);
        lblTotalPagar.setFont(new Font("Arial", Font.BOLD, 32));
        lblTotalPagar.setForeground(Color.WHITE);

        JPanel panelTotales = new JPanel(new BorderLayout());
        panelTotales.setBackground(new Color(0x9A, 0x43, 0x43));
        panelTotales.add(lblTotal, BorderLayout.WEST);
        panelTotales.add(lblTotalPagar, BorderLayout.CENTER);
        panelInferior.add(panelTotales, BorderLayout.CENTER);

        JButton btnCobrar = new JButton("COBRAR");
        btnCobrar.setFont(new Font("Arial", Font.BOLD, 20));
        btnCobrar.setBackground(Color.WHITE);
        btnCobrar.setForeground(new Color(0x9A, 0x43, 0x43));
        btnCobrar.setFocusPainted(false);
        btnCobrar.setPreferredSize(new Dimension(200, 50));
        panelInferior.add(btnCobrar, BorderLayout.EAST);
        add(panelInferior, BorderLayout.SOUTH);

        // Eventos
        btnAnadirProducto.addActionListener(e -> agregarProducto());
        btnBuscarProducto.addActionListener(e -> buscarProducto());
        btnEliminarProducto.addActionListener(e -> eliminarProductoSeleccionado());
        tablaProductos.getModel().addTableModelListener(e -> {
            if (e.getColumn() == COL_CANTIDAD) {
                actualizarImporte(e.getFirstRow());
            }
        });
        txtDescuento.addActionListener(e -> calcularTotal());
        btnCobrar.addActionListener(e -> cobrarVenta());

        // Cargar productos al iniciar
        cargarProductos();
    }

    private void cargarProductos() {
        // Aquí puedes cargar productos iniciales si es necesario
    }

    private void agregarProductoATabla(Producto producto) {
        String codigo = producto.getCodigo();
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            if (codigo.equals(modeloTabla.getValueAt(i, COL_CODIGO))) {
                int cant = (int) modeloTabla.getValueAt(i, COL_CANTIDAD);
                modeloTabla.setValueAt(cant + 1, i, COL_CANTIDAD);
                actualizarImporte(i);
                return;
            }
        }

        modeloTabla.addRow(new Object[]{
            ImageUtils.loadIcon(producto.getImagen(), 54, 54),
            producto.getCodigo(),
            producto.getNombre(),
            String.format("$%.2f", producto.getPrecio()),
            1,
            String.format("$%.2f", producto.getPrecio())
        });
        calcularTotal();
    }

    private void buscarProducto() {
        String texto = JOptionPane.showInputDialog(this, "Buscar por nombre o codigo:");
        if (texto == null || texto.trim().isEmpty()) {
            return;
        }

        List<Producto> resultados = BaseDeDatos.buscarProductos(texto.trim());
        if (resultados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se encontraron productos", "Busqueda", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JComboBox<Producto> combo = new JComboBox<>(resultados.toArray(new Producto[0]));
        int respuesta = JOptionPane.showConfirmDialog(this, combo, "Seleccione producto", JOptionPane.OK_CANCEL_OPTION);
        if (respuesta == JOptionPane.OK_OPTION && combo.getSelectedItem() instanceof Producto producto) {
            agregarProductoATabla(producto);
        }
    }

    private void eliminarProductoSeleccionado() {
        int fila = tablaProductos.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto de la venta", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        modeloTabla.removeRow(tablaProductos.convertRowIndexToModel(fila));
        calcularTotal();
    }

    private void agregarProducto() {
        String codigo = txtCodigoBarra.getText().trim();
        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Ingrese un código de barras",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        Producto producto = BaseDeDatos.obtenerProductoPorCodigo(codigo);
        if (producto == null) {
            JOptionPane.showMessageDialog(this,
                "Producto no encontrado",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Verificar si ya está en la tabla
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            if (codigo.equals(modeloTabla.getValueAt(i, COL_CODIGO))) {
                int cant = (int) modeloTabla.getValueAt(i, COL_CANTIDAD);
                modeloTabla.setValueAt(cant + 1, i, COL_CANTIDAD);
                actualizarImporte(i);
                txtCodigoBarra.setText("");
                return;
            }
        }

        // Agregar nuevo producto a la tabla
        modeloTabla.addRow(new Object[]{
            ImageUtils.loadIcon(producto.getImagen(), 54, 54),
            producto.getCodigo(),
            producto.getNombre(),
            String.format("$%.2f", producto.getPrecio()),
            1,
            String.format("$%.2f", producto.getPrecio())
        });
        calcularTotal();
        txtCodigoBarra.setText("");
    }

    private void actualizarImporte(int fila) {
        try {
            double precio = Double.parseDouble(modeloTabla.getValueAt(fila, COL_PRECIO).toString().replace("$", ""));
            int cantidad = Integer.parseInt(modeloTabla.getValueAt(fila, COL_CANTIDAD).toString());
            modeloTabla.setValueAt(String.format("$%.2f", precio * cantidad), fila, COL_IMPORTE);
            calcularTotal();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Cantidad inválida",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            modeloTabla.setValueAt(1, fila, COL_CANTIDAD);
        }
    }

    private void calcularTotal() {
        double subtotal = 0.0;
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            String importeStr = modeloTabla.getValueAt(i, COL_IMPORTE).toString().replace("$", "");
            subtotal += Double.parseDouble(importeStr);
        }

        double descuento = 0.0;
        try {
            descuento = Double.parseDouble(txtDescuento.getText().trim());
        } catch (NumberFormatException e) {
            descuento = 0.0;
        }
        double totalPagar = Math.max(0, subtotal - descuento);

        lblTotal.setText("SUBTOTAL $ " + String.format("%.2f", subtotal));
        lblTotalPagar.setText("TOTAL A PAGAR $ " + String.format("%.2f", totalPagar));
    }

    private void cobrarVenta() {
        if (modeloTabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "No hay productos para cobrar",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Factura.ProductoCantidad> productosVendidos = new ArrayList<>();
        double total = 0.0;

        try {
            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                String codigo = (String) modeloTabla.getValueAt(i, COL_CODIGO);
                Producto producto = BaseDeDatos.obtenerProductoPorCodigo(codigo);
                if (producto == null) {
                    throw new Exception("Producto no encontrado: " + codigo);
                }

                int cantidad = Integer.parseInt(modeloTabla.getValueAt(i, COL_CANTIDAD).toString());
                if (cantidad > producto.getCantidad()) {
                    throw new Exception("Stock insuficiente para: " + producto.getNombre());
                }

                double precio = Double.parseDouble(modeloTabla.getValueAt(i, COL_PRECIO).toString().replace("$", ""));
                productosVendidos.add(new Factura.ProductoCantidad(producto, cantidad));
                total += (precio * cantidad);
            }

            double descuento = 0.0;
            try {
                descuento = Double.parseDouble(txtDescuento.getText().trim());
            } catch (NumberFormatException e) {
                descuento = 0.0;
            }

            String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            double totalPagar = Math.max(0, total - descuento);
            Factura factura = new Factura(BaseDeDatos.getNextFacturaId(), productosVendidos, total, descuento, totalPagar, fecha);
            if (BaseDeDatos.registrarVenta(factura)) {
                mostrarResumenVenta(factura);
                limpiarVenta();
                InventarioPanel.inventarioPanel.cargarDatosDesdeBD();
                FacturasPanel.facturasPanel.actualizarFacturas();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al registrar la venta en la base de datos",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Error en formato de cantidad o precio",
                "Error de formato",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al procesar venta: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarResumenVenta(Factura factura) {
        StringBuilder resumen = new StringBuilder();
        resumen.append("=== FACTURA #").append(factura.getId()).append(" ===\n");
        resumen.append("Fecha: ").append(factura.getFecha()).append("\n\n");
        resumen.append("PRODUCTOS:\n");

        for (Factura.ProductoCantidad pc : factura.getProducts()) {
            resumen.append("- ").append(pc.getProducto().getNombre())
                   .append(" x").append(pc.getCantidad())
                   .append(" @ $").append(pc.getProducto().getPrecio())
                   .append(" = $").append(String.format("%.2f", pc.getSubtotal()))
                   .append("\n");
        }

        resumen.append("\nSUBTOTAL: $").append(String.format("%.2f", factura.getTotal()));
        if (factura.getDescuento() > 0) {
            resumen.append("\nDESCUENTO: $").append(String.format("%.2f", factura.getDescuento()));
        }
        resumen.append("\nTOTAL A PAGAR: $").append(String.format("%.2f", factura.getTotalPagar()));
        JOptionPane.showMessageDialog(this, resumen.toString(), "Resumen de Venta", JOptionPane.INFORMATION_MESSAGE);
    }

    private void limpiarVenta() {
        modeloTabla.setRowCount(0);
        lblTotal.setText("TOTAL $ 0.00");
        txtCodigoBarra.setText("");
        txtCodigoBarra.requestFocus();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Sistema de Ventas - 1920x1080");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(1920, 1080);
            frame.setExtendedState(Frame.MAXIMIZED_BOTH);
            frame.add(new VentasPanel());
            frame.setVisible(true);
        });
    }
}
