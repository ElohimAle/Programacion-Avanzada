package PDV;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import funciones.BaseDeDatos;
import funciones.Factura;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class FacturasPanel extends JPanel {
    private JTable tablaFacturas;
    private DefaultTableModel modeloTabla;
    private JButton btnActualizar;
    private JLabel lblTotalFacturas;
    private JLabel lblMontoTotal;

    public static FacturasPanel facturasPanel;

    public FacturasPanel() {
        facturasPanel = this; // Inicializar estáticamente
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel superior - Título y botones
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(Color.decode("#FECDCD"));
        JLabel lblTitulo = new JLabel("Historial de Facturas", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        panelSuperior.add(lblTitulo, BorderLayout.WEST);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        panelBotones.setBackground(Color.decode("#FECDCD"));

        btnActualizar = new JButton("ACTUALIZAR");
        estilizarBoton(btnActualizar);
        btnActualizar.addActionListener(e -> cargarFacturas());

        JButton btnImprimir = new JButton("IMPRIMIR FACTURA");
        estilizarBoton(btnImprimir);
        btnImprimir.addActionListener(e -> {
            int filaSeleccionada = tablaFacturas.getSelectedRow();
            if (filaSeleccionada == -1) {
                JOptionPane.showMessageDialog(this,
                        "Seleccione una factura para imprimir",
                        "Advertencia",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                int idFactura = Integer.parseInt(modeloTabla.getValueAt(filaSeleccionada, 0).toString());
                List<Factura> facturas = BaseDeDatos.obtenerTodasLasFacturas();

                for (Factura factura : facturas) {
                    if (factura.getId() == idFactura) {
                        generarPDF(factura); // Generar PDF de la factura
                        break;
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error al obtener ID de factura: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        panelBotones.add(btnActualizar);
        panelBotones.add(btnImprimir);
        panelSuperior.add(panelBotones, BorderLayout.EAST);
        add(panelSuperior, BorderLayout.NORTH);

        // Columnas para el reporte de facturas
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

        JScrollPane scrollPane = new JScrollPane(tablaFacturas);
        add(scrollPane, BorderLayout.CENTER);

        // Panel inferior - Estadísticas
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
    }

    public void actualizarFacturas() {
        cargarFacturas();
    }

    private void cargarFacturas() {
        modeloTabla.setRowCount(0);
        List<Factura> facturas = BaseDeDatos.obtenerTodasLasFacturas();
        double montoTotal = 0;

        for (Factura factura : facturas) {
            StringBuilder productosStr = new StringBuilder("<html>");
            for (Factura.ProductoCantidad pc : factura.getProducts()) {
                productosStr.append("- ")
                          .append(pc.getProducto().getNombre())
                          .append(" x").append(pc.getCantidad())
                          .append(" - $")
                          .append(String.format("%.2f", pc.getSubtotal()))
                          .append("<br>");
            }
            productosStr.append("</html>");

            Object[] fila = {
                factura.getId(),
                factura.getFecha(),
                productosStr.toString(),
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

    public void generarPDF(Factura factura) {
        try {
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // Usar fuente integrada
            PDFont font = PDType0Font.load(document, getClass().getClassLoader()
                    .getResourceAsStream("resources/helvetica-bold.ttf"));

            contentStream.setFont(font, 14);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, 700);

            contentStream.showText("=== FACTURA #" + factura.getId() + " ===");
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Fecha: " + factura.getFecha());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Detalles de la Venta:");

            float yPos = 660;
            for (Factura.ProductoCantidad pc : factura.getProducts()) {
                contentStream.endText();
                contentStream.beginText();
                contentStream.newLineAtOffset(50, yPos -= 20);
                contentStream.showText("- " + pc.getProducto().getNombre()
                        + " x" + pc.getCantidad()
                        + " - $" + String.format("%.2f", pc.getSubtotal()));
            }

            contentStream.endText();

            contentStream.beginText();
            contentStream.newLineAtOffset(50, yPos -= 40);
            contentStream.showText("TOTAL: $" + String.format("%.2f", factura.getTotal()));
            contentStream.endText();

            contentStream.close();

            String nombreArchivo = "factura_" + factura.getId() + ".pdf";
            File file = new File(nombreArchivo);
            System.out.println("Archivo guardado en: " + file.getAbsolutePath());

            document.save(file);
            document.close();

            JOptionPane.showMessageDialog(this,
                    "Factura guardada como '" + nombreArchivo + "'",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace(); // Imprime la traza completa
            JOptionPane.showMessageDialog(this,
                    "Error al generar el PDF: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Historial de Facturas");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.add(new FacturasPanel());
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}