package PDV;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import funciones.BaseDeDatos;
import funciones.Producto;

public class InventarioPanel extends JPanel {
    private JTable tablaInventario;
    private DefaultTableModel modelo;
    private JButton btnActualizar;
    private JButton btnAjustarStock;
    private JLabel lblTotalProductos;
    public static InventarioPanel inventarioPanel;

    public InventarioPanel() {
        inventarioPanel = this;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // PANEL SUPERIOR - Título y botones
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(Color.decode("#FECDCD"));
        JLabel titulo = new JLabel("Inventario", SwingConstants.LEFT);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        panelSuperior.add(titulo, BorderLayout.WEST);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        panelBotones.setBackground(Color.decode("#FECDCD"));

        btnActualizar = new JButton("Actualizar");
        estilizarBoton(btnActualizar);
        btnActualizar.addActionListener(e -> cargarDatosDesdeBD());

        btnAjustarStock = new JButton("Ajustar Stock");
        estilizarBoton(btnAjustarStock);
        btnAjustarStock.addActionListener(e -> ajustarStock());

        panelBotones.add(btnActualizar);
        panelBotones.add(btnAjustarStock);
        panelSuperior.add(panelBotones, BorderLayout.EAST);
        add(panelSuperior, BorderLayout.NORTH);

        // TABLA DE INVENTARIO
        String[] columnas = {"Código", "Nombre", "Precio", "Stock"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 0, 1 -> String.class;
                    case 2, 3 -> Number.class;
                    default -> String.class;
                };
            }
        };

        tablaInventario = new JTable(modelo);
        tablaInventario.setRowHeight(30);
        tablaInventario.setBackground(Color.WHITE);
        tablaInventario.setGridColor(Color.LIGHT_GRAY);
        tablaInventario.getTableHeader().setBackground(Color.decode("#E6B9B9"));
        tablaInventario.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        tablaInventario.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(new JTextField()) {
            @Override
            public boolean stopCellEditing() {
                try {
                    Integer.parseInt(getCellEditorValue().toString());
                    return super.stopCellEditing();
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Ingrese un valor numérico válido", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        });

        tablaInventario.getColumnModel().getColumn(2).setCellRenderer((TableCellRenderer) new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                setText("$" + value);
                return this;
            }
        });

        add(new JScrollPane(tablaInventario), BorderLayout.CENTER);

        // PANEL INFERIOR - Estadísticas
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelInferior.setBackground(Color.decode("#9A4343"));
        panelInferior.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        lblTotalProductos = new JLabel("Total de productos: 0");
        lblTotalProductos.setFont(new Font("Arial", Font.BOLD, 14));
        lblTotalProductos.setForeground(Color.WHITE);
        panelInferior.add(lblTotalProductos);
        add(panelInferior, BorderLayout.SOUTH);

        // Cargar datos iniciales
        cargarDatosDesdeBD();
    }

    public void cargarDatosDesdeBD() {
        modelo.setRowCount(0);
        Map<String, Producto> productos = BaseDeDatos.obtenerTodosLosProductos();
        for (Producto p : productos.values()) {
            modelo.addRow(new Object[]{
                p.getCodigo(),
                p.getNombre(),
                p.getPrecio(),
                p.getCantidad()
            });
        }
        lblTotalProductos.setText("Total de productos: " + productos.size());
    }

    private void ajustarStock() {
        int filaSeleccionada = tablaInventario.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Seleccione un producto para ajustar su stock",
                "Advertencia",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String codigo = (String) modelo.getValueAt(filaSeleccionada, 0);
        String nombre = (String) modelo.getValueAt(filaSeleccionada, 1);
        int stockActual = (int) modelo.getValueAt(filaSeleccionada, 3);

        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        JLabel lblStockActual = new JLabel("Stock actual:");
        JTextField txtStockActual = new JTextField(String.valueOf(stockActual));
        txtStockActual.setEditable(false);
        JLabel lblNuevoStock = new JLabel("Nuevo stock:");
        JTextField txtNuevoStock = new JTextField();
        txtNuevoStock.setText(String.valueOf(stockActual));
        panel.add(lblStockActual);
        panel.add(txtStockActual);
        panel.add(lblNuevoStock);
        panel.add(txtNuevoStock);

        int resultado = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Ajustar stock de " + nombre,
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (resultado == JOptionPane.OK_OPTION) {
            try {
                int nuevoStock = Integer.parseInt(txtNuevoStock.getText().trim());
                if (nuevoStock < 0) throw new NumberFormatException();
                if (BaseDeDatos.actualizarStock(codigo, nuevoStock - stockActual)) {
                    modelo.setValueAt(nuevoStock, filaSeleccionada, 3);
                    JOptionPane.showMessageDialog(this, "Stock actualizado correctamente");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al actualizar el stock");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Ingrese un número entero positivo");
            }
        }
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
            JFrame frame = new JFrame("Inventario");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.add(new InventarioPanel());
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}