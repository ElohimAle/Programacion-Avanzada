package PDV;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.InputStream;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import funciones.BaseDeDatos;
import funciones.Factura;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

public class FacturasPanel extends JPanel {
    private JTable tablaFacturas;
    private DefaultTableModel modeloTabla;
    private JButton btnActualizar;
    private JLabel lblTotalFacturas;
    private JLabel lblMontoTotal;
    private JTextArea txtPrevisualizacion;
    private JButton btnDescargar;

    public static FacturasPanel facturasPanel;

    public FacturasPanel() {
        facturasPanel = this;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // PANEL SUPERIOR - Título y botones
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(Color.decode("#FECDCD"));
        JLabel lblTitulo = new JLabel("Historial de Facturas", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        panelSuperior.add(lblTitulo, BorderLayout.WEST);

        btnActualizar = new JButton("ACTUALIZAR");
        estilizarBoton(btnActualizar);
        btnActualizar.addActionListener(e -> cargarFacturas());

        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        panelBoton.setBackground(Color.decode("#FECDCD"));
        panelBoton.add(btnActualizar);
        panelSuperior.add(panelBoton, BorderLayout.EAST);
        add(panelSuperior, BorderLayout.NORTH);

        // COLUMNAS DE LA TABLA
        String[] columnas = {"ID", "Fecha", "Productos", "Total"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Integer.class : String.class;
            }
        };

        tablaFacturas = new JTable(modeloTabla);
        tablaFacturas.setRowHeight(60);
        tablaFacturas.setAutoCreateRowSorter(true);
        tablaFacturas.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        tablaFacturas.setFont(new Font("Arial", Font.PLAIN, 12));
        tablaFacturas.setGridColor(Color.LIGHT_GRAY);

        // RENDERIZADOR PARA CELDAS CON MÚLTIPLES LÍNEAS
        tablaFacturas.setDefaultRenderer(String.class, new MultiLineCellRenderer());

        JScrollPane scrollPane = new JScrollPane(tablaFacturas);

        // PANEL DERECHO - Previsualización de factura
        JPanel panelDerecho = new JPanel(new BorderLayout());
        panelDerecho.setBackground(Color.WHITE);
        panelDerecho.setBorder(BorderFactory.createTitledBorder("Previsualización"));

        txtPrevisualizacion = new JTextArea();
        txtPrevisualizacion.setEditable(false);
        txtPrevisualizacion.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtPrevisualizacion.setLineWrap(true);
        txtPrevisualizacion.setWrapStyleWord(true);
        JScrollPane scrollPreview = new JScrollPane(txtPrevisualizacion);
        panelDerecho.add(scrollPreview, BorderLayout.CENTER);

        btnDescargar = new JButton("Descargar PDF");
        estilizarBoton(btnDescargar);
        btnDescargar.addActionListener(e -> {
            int filaSeleccionada = tablaFacturas.getSelectedRow();
            if (filaSeleccionada >= 0) {
                try {
                    int idFactura = Integer.parseInt(modeloTabla.getValueAt(filaSeleccionada, 0).toString());
                    List<Factura> facturas = BaseDeDatos.obtenerTodasLasFacturas();

                    for (Factura factura : facturas) {
                        if (factura.getId() == idFactura) {
                            generarPDF(factura); // Genera el PDF
                            break;
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                        "Error al obtener ID de factura: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Seleccione una factura para descargar",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        panelDerecho.add(btnDescargar, BorderLayout.SOUTH);

        // ⬇️ Nuevo panel que combina tabla y previsualización ⬇️
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.add(scrollPane, BorderLayout.CENTER);

        panelDerecho.setPreferredSize(new Dimension(430, getHeight()));
        panelCentral.add(panelDerecho, BorderLayout.EAST);

        add(panelCentral, BorderLayout.CENTER);
        add(panelDerecho, BorderLayout.EAST);

        // PANEL INFERIOR - Estadísticas
        JPanel panelInferior = new JPanel(new GridLayout(1, 2));
        panelInferior.setBackground(Color.decode("#9A4343"));
        panelInferior.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        lblTotalFacturas = new JLabel("Total Facturas: 0", SwingConstants.CENTER);
        lblMontoTotal = new JLabel("Monto Total: $0.00", SwingConstants.CENTER);
        estilizarLabel(lblTotalFacturas);
        estilizarLabel(lblMontoTotal);

        panelInferior.add(lblTotalFacturas);
        panelInferior.add(lblMontoTotal);
        add(panelInferior, BorderLayout.SOUTH);

        // Cargar datos iniciales
        cargarFacturas();

        // Mostrar previsualización al seleccionar factura
        tablaFacturas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int filaSeleccionada = tablaFacturas.getSelectedRow();
                if (filaSeleccionada >= 0) {
                    mostrarPrevisualizacion(filaSeleccionada);
                }
            }
        });
    }

    public void actualizarFacturas() {
        cargarFacturas();
    }

    private void cargarFacturas() {
        modeloTabla.setRowCount(0);
        List<Factura> facturas = BaseDeDatos.obtenerTodasLasFacturas();
        double montoTotal = 0;

        for (Factura factura : facturas) {
            StringBuilder productosStr = new StringBuilder();
            boolean firstProduct = true;
            
            for (Factura.ProductoCantidad pc : factura.getProducts()) {
                if (!firstProduct) {
                    productosStr.append(", ");
                }
                productosStr.append(pc.getProducto().getNombre());
                firstProduct = false;
            }

            Object[] fila = {
                factura.getId(),
                factura.getFecha(),
                productosStr.toString(), // Solo nombres separados por comas
                "$" + String.format("%.2f", factura.getTotal())
            };
            modeloTabla.addRow(fila);
            montoTotal += factura.getTotal();
        }

        lblTotalFacturas.setText("Total Facturas: " + facturas.size());
        lblMontoTotal.setText("Monto Total: $" + String.format("%.2f", montoTotal));
    }

    private void estilizarBoton(JButton boton) {
        boton.setBackground(Color.decode("#9A4343"));
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("Arial", Font.BOLD, 14));
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setContentAreaFilled(true);
    }

    private void estilizarLabel(JLabel label) {
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(Color.WHITE);
    }

    // Método para mostrar previsualización de la factura
    private void mostrarPrevisualizacion(int fila) {
        int idFactura = Integer.parseInt(modeloTabla.getValueAt(fila, 0).toString());
        List<Factura> facturas = BaseDeDatos.obtenerTodasLasFacturas();

        for (Factura factura : facturas) {
            if (factura.getId() == idFactura) {
                StringBuilder preview = new StringBuilder();
                preview.append("===============================\n");
                preview.append("     TIENDA ABARROTES - FACTURA\n");
                preview.append("===============================\n\n");
                preview.append("Factura #: ").append(factura.getId()).append("\n");
                preview.append("Fecha: ").append(factura.getFecha()).append("\n");
                preview.append("Cajero: ").append(Sesion.getNombreUsuario()).append("\n\n");

                preview.append("Productos:\n");
                preview.append("-----------------------------------------------------\n");
                preview.append("Producto         Precio Unitario    Cantidad   Subtotal\n");
                preview.append("-----------------------------------------------------\n");

                for (Factura.ProductoCantidad pc : factura.getProducts()) {
                    preview.append(String.format("%-15s $%-15.2f x%-8d $%-10.2f%n",
                            pc.getProducto().getNombre(),
                            pc.getProducto().getPrecio(),
                            pc.getCantidad(),
                            pc.getSubtotal()));
                }

                preview.append("-----------------------------------------------------\n");
                preview.append("TOTAL: $").append(String.format("%.2f", factura.getTotal())).append("\n");
                txtPrevisualizacion.setText(preview.toString());
                break;
            }
        }
    }

    // Método para generar PDF usando Apache PDFBox
    public void generarPDF(Factura factura) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // Cargar fuente desde recursos
            InputStream fontStream = getClass().getClassLoader().getResourceAsStream("resources/helvetica-bold.ttf");
            if (fontStream == null) {
                throw new Exception("No se encontró el archivo de fuente 'helvetica-bold.ttf'");
            }

            PDFont font = PDType0Font.load(document, fontStream);

            // Encabezado
            contentStream.beginText();
            contentStream.setFont(font, 14);
            contentStream.newLineAtOffset(50, 700);
            contentStream.showText("=====================================================");
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("             TIENDA ABARROTES - FACTURA");
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("=====================================================");
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Factura #: " + factura.getId());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Fecha: " + factura.getFecha());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Cajero: " + Sesion.getNombreUsuario());
            contentStream.endText();

            float yPos = 600;

            contentStream.beginText();
            contentStream.setFont(font, 12);
            contentStream.newLineAtOffset(50, yPos -= 20);
            contentStream.showText("Productos:");
            contentStream.endText();

            // Línea divisoria
            contentStream.setLineWidth(1f);
            contentStream.moveTo(50, yPos -= 10);
            contentStream.lineTo(500, yPos);
            contentStream.stroke();

            contentStream.beginText();
            contentStream.newLineAtOffset(50, yPos -= 20);
            contentStream.showText("Producto         Precio Unitario    Cantidad   Subtotal");
            contentStream.endText();

            contentStream.setLineWidth(1f);
            contentStream.moveTo(50, yPos -= 10);
            contentStream.lineTo(500, yPos);
            contentStream.stroke();

            // Detalles de productos vendidos
            for (Factura.ProductoCantidad pc : factura.getProducts()) {
                contentStream.beginText();
                contentStream.setFont(font, 12);
                contentStream.newLineAtOffset(50, yPos -= 20);
                String linea = String.format("%-15s $%-15.2f x%-8d $%-10.2f",
                        pc.getProducto().getNombre(),
                        pc.getProducto().getPrecio(),
                        pc.getCantidad(),
                        pc.getSubtotal());
                contentStream.showText(linea);
                contentStream.endText();
            }

            // Línea final
            contentStream.setLineWidth(1f);
            contentStream.moveTo(50, yPos -= 10);
            contentStream.lineTo(500, yPos);
            contentStream.stroke();

            // Total final
            contentStream.beginText();
            contentStream.newLineAtOffset(50, yPos -= 20);
            contentStream.showText("TOTAL: $" + String.format("%.2f", factura.getTotal()));
            contentStream.endText();

            contentStream.close();

            // Guardar documento
            String nombreArchivo = "factura_" + factura.getId() + ".pdf";
            document.save(nombreArchivo);
            document.close();

            JOptionPane.showMessageDialog(this,
                    "Factura guardada como '" + nombreArchivo + "'",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al generar el PDF: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Renderizador personalizado para celdas multilinea
    private static class MultiLineCellRenderer extends JLabel implements TableCellRenderer {
        public MultiLineCellRenderer() {
            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(table.getBackground());
            }

            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
}