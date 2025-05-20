package PDV;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
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
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import funciones.BaseDeDatos;
import funciones.Factura;
import funciones.Producto;

public class VentasPanel extends JPanel {
    private JTextField txtCodigoBarra;
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    private JLabel lblTotal;
    private JLabel lblCantidadEnCaja;

    public VentasPanel() {
        // Configuración principal para 1920x1080
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
        txtCodigoBarra = new JTextField(40);
        txtCodigoBarra.setFont(new Font("Arial", Font.PLAIN, 18));
        JButton btnAnadirProducto = new JButton("Añadir producto");
        btnAnadirProducto.setFont(new Font("Arial", Font.BOLD, 18));
        btnAnadirProducto.setBackground(new Color(0x9A, 0x43, 0x43));
        btnAnadirProducto.setForeground(Color.WHITE);
        btnAnadirProducto.setFocusPainted(false);
        btnAnadirProducto.setPreferredSize(new Dimension(200, 40));
        panelCodigo.add(new JLabel("Código de Barras:"));
        panelCodigo.add(txtCodigoBarra);
        panelCodigo.add(btnAnadirProducto);
        panelSuperior.add(panelCodigo, BorderLayout.CENTER);
        add(panelSuperior, BorderLayout.NORTH);

        // PANEL CENTRAL - Tabla de productos
        String[] columnas = {"Código", "Descripción", "Precio Unitario", "Cantidad", "Importe"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Solo la columna de cantidad es editable
            }
        };
        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setFont(new Font("Arial", Font.PLAIN, 16));
        tablaProductos.setRowHeight(40);
        tablaProductos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        JScrollPane scrollTabla = new JScrollPane(tablaProductos);
        scrollTabla.setPreferredSize(new Dimension(1800, 700));
        add(scrollTabla, BorderLayout.CENTER);

        // PANEL INFERIOR - Total
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBackground(new Color(0x9A, 0x43, 0x43));
        panelInferior.setBorder(new EmptyBorder(15, 20, 15, 20));
        panelInferior.setPreferredSize(new Dimension(1920, 80));
        lblTotal = new JLabel("TOTAL $ 0.00", SwingConstants.CENTER);
        lblTotal.setFont(new Font("Arial", Font.BOLD, 32));
        lblTotal.setForeground(Color.WHITE);
        panelInferior.add(lblTotal, BorderLayout.CENTER);
        JButton btnCobrar = new JButton("COBRAR");
        btnCobrar.setFont(new Font("Arial", Font.BOLD, 20));
        btnCobrar.setBackground(Color.WHITE);
        btnCobrar.setForeground(new Color(0x9A, 0x43, 0x43));
        btnCobrar.setFocusPainted(false);
        btnCobrar.setPreferredSize(new Dimension(200, 50));
        panelInferior.add(btnCobrar, BorderLayout.EAST);
        add(panelInferior, BorderLayout.SOUTH);

        // Listeners
        btnAnadirProducto.addActionListener(e -> agregarProducto());
        tablaProductos.getModel().addTableModelListener(e -> {
            if (e.getColumn() == 3) {
                actualizarImporte(e.getFirstRow());
            }
        });
        btnCobrar.addActionListener(e -> cobrarVenta());

        // Cargar productos al iniciar
        cargarProductos();
    }

    private void cargarProductos() {
        // Aquí puedes cargar productos iniciales si es necesario
    }

    private void agregarProducto() {
        String codigo = txtCodigoBarra.getText().trim();
        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un código de barras", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Producto producto = BaseDeDatos.obtenerProductoPorCodigo(codigo);
        if (producto == null) {
            JOptionPane.showMessageDialog(this, "Producto no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Verificar si el producto ya está en la tabla
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            if (codigo.equals(modeloTabla.getValueAt(i, 0))) {
                int cant = (int) modeloTabla.getValueAt(i, 3);
                modeloTabla.setValueAt(cant + 1, i, 3);
                actualizarImporte(i);
                txtCodigoBarra.setText("");
                return;
            }
        }

        // Agregar nuevo producto a la tabla
        modeloTabla.addRow(new Object[]{
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
            double precio = Double.parseDouble(modeloTabla.getValueAt(fila, 2).toString().replace("$", ""));
            int cant = Integer.parseInt(modeloTabla.getValueAt(fila, 3).toString());
            double importe = precio * cant;
            modeloTabla.setValueAt(String.format("$%.2f", importe), fila, 4);
            calcularTotal();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Cantidad inválida", "Error", JOptionPane.ERROR_MESSAGE);
            modeloTabla.setValueAt(1, fila, 3);
        }
    }

    private void calcularTotal() {
        double total = 0;
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            String importeStr = modeloTabla.getValueAt(i, 4).toString().replace("$", "");
            total += Double.parseDouble(importeStr);
        }
        lblTotal.setText("TOTAL $ " + String.format("%.2f", total));
    }

    private void cobrarVenta() {
        if (modeloTabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay productos para cobrar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Factura.ProductoCantidad> productosVendidos = new ArrayList<>();
        double total = 0.0;

        try {
            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                String codigo = (String) modeloTabla.getValueAt(i, 0);
                Producto producto = BaseDeDatos.obtenerProductoPorCodigo(codigo);
                if (producto == null) {
                    throw new Exception("Producto no encontrado: " + codigo);
                }

                int cantidad = Integer.parseInt(modeloTabla.getValueAt(i, 3).toString());
                if (cantidad > producto.getCantidad()) {
                    throw new Exception("Stock insuficiente para: " + producto.getNombre());
                }

                double precio = Double.parseDouble(modeloTabla.getValueAt(i, 2).toString().replace("$", ""));
                productosVendidos.add(new Factura.ProductoCantidad(producto, cantidad));
                total += (precio * cantidad);
            }

            String fecha = LocalDate.now().toString();
            Factura factura = new Factura(BaseDeDatos.getNextFacturaId(), productosVendidos, total, fecha);

            if (BaseDeDatos.registrarVenta(factura)) {
                mostrarResumenVenta(factura);
                limpiarVenta();
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

        resumen.append("\nTOTAL: $").append(String.format("%.2f", factura.getTotal()));
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