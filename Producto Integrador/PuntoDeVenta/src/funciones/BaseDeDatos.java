package funciones;

import java.sql.*;
import java.util.*;

public class BaseDeDatos {
    private static final String URL = "jdbc:sqlite:pdv.db";

    public static void inicializarBaseDatos() {
        String sqlProductos = "CREATE TABLE IF NOT EXISTS productos (" +
                            "codigo TEXT PRIMARY KEY," +
                            "nombre TEXT NOT NULL," +
                            "precio REAL NOT NULL," +
                            "cantidad INTEGER NOT NULL DEFAULT 0)";
        String sqlFacturas = "CREATE TABLE IF NOT EXISTS facturas (" +
                           "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                           "fecha TEXT NOT NULL," +
                           "total REAL NOT NULL)";
        String sqlFacturaProductos = "CREATE TABLE IF NOT EXISTS factura_productos (" +
                                   "id_factura INTEGER," +
                                   "codigo_producto TEXT," +
                                   "cantidad INTEGER," +
                                   "FOREIGN KEY(id_factura) REFERENCES facturas(id)," +
                                   "FOREIGN KEY(codigo_producto) REFERENCES productos(codigo))";
        String sqlCompras = "CREATE TABLE IF NOT EXISTS compras (" +
                          "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                          "producto TEXT NOT NULL," +
                          "cantidad INTEGER NOT NULL," +
                          "precio_unitario REAL NOT NULL," +
                          "distribuidor TEXT NOT NULL," +
                          "fecha TEXT NOT NULL)";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlProductos);
            stmt.execute(sqlFacturas);
            stmt.execute(sqlFacturaProductos);
            stmt.execute(sqlCompras);
        } catch (SQLException e) {
            System.err.println("Error al inicializar BD: " + e.getMessage());
        }
    }

    public static boolean guardarProducto(Producto producto) {
        String sql = "INSERT OR REPLACE INTO productos (codigo, nombre, precio, cantidad) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, producto.getCodigo());
            pstmt.setString(2, producto.getNombre());
            pstmt.setDouble(3, producto.getPrecio());
            pstmt.setInt(4, producto.getCantidad());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al guardar producto: " + e.getMessage());
            return false;
        }
    }

    public static Map<String, Producto> obtenerTodosLosProductos() {
        Map<String, Producto> productos = new HashMap<>();
        String sql = "SELECT codigo, nombre, precio, cantidad FROM productos";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Producto p = new Producto(
                    rs.getString("codigo"),
                    rs.getString("nombre"),
                    rs.getDouble("precio"),
                    rs.getInt("cantidad")
                );
                productos.put(p.getCodigo(), p);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener productos: " + e.getMessage());
        }
        return productos;
    }

    public static Producto obtenerProductoPorCodigo(String codigo) {
        String sql = "SELECT nombre, precio, cantidad FROM productos WHERE codigo = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, codigo);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Producto(codigo,
                                    rs.getString("nombre"),
                                    rs.getDouble("precio"),
                                    rs.getInt("cantidad"));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener producto: " + e.getMessage());
        }
        return null;
    }

    public static boolean eliminarProducto(String codigo) {
        String sql = "DELETE FROM productos WHERE codigo = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, codigo);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
            return false;
        }
    }

    public static boolean actualizarStock(String codigo, int cantidad) {
        String sql = "UPDATE productos SET cantidad = cantidad + ? WHERE codigo = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cantidad);
            pstmt.setString(2, codigo);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar stock: " + e.getMessage());
            return false;
        }
    }

    // ================= MÉTODOS PARA VENTAS/FACTURAS =================
    public static int getNextFacturaId() {
        String sql = "SELECT seq FROM sqlite_sequence WHERE name='facturas'";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt("seq") + 1 : 1;
        } catch (SQLException e) {
            System.err.println("Error al obtener ID de factura: " + e.getMessage());
            return 1;
        }
    }

    public static boolean registrarVenta(Factura factura) {
        String sqlFactura = "INSERT INTO facturas (fecha, total) VALUES (?, ?)";
        String sqlProductos = "INSERT INTO factura_productos (id_factura, codigo_producto, cantidad) VALUES (?, ?, ?)";
        String sqlActualizarStock = "UPDATE productos SET cantidad = cantidad - ? WHERE codigo = ?";
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(URL);
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(sqlFactura, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, factura.getFecha());
                pstmt.setDouble(2, factura.getTotal());
                pstmt.executeUpdate();

                ResultSet rs = pstmt.getGeneratedKeys();
                int idFactura = rs.next() ? rs.getInt(1) : 0;

                try (PreparedStatement pstmtProductos = conn.prepareStatement(sqlProductos)) {
                    for (Factura.ProductoCantidad pc : factura.getProducts()) {
                        pstmtProductos.setInt(1, idFactura);
                        pstmtProductos.setString(2, pc.getProducto().getCodigo());
                        pstmtProductos.setInt(3, pc.getCantidad());
                        pstmtProductos.addBatch();
                    }
                    pstmtProductos.executeBatch();
                }

                try (PreparedStatement pstmtStock = conn.prepareStatement(sqlActualizarStock)) {
                    for (Factura.ProductoCantidad pc : factura.getProducts()) {
                        pstmtStock.setInt(1, pc.getCantidad());
                        pstmtStock.setString(2, pc.getProducto().getCodigo());
                        pstmtStock.addBatch();
                    }
                    pstmtStock.executeBatch();
                }

                conn.commit();
                return true;
            }
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Error al hacer rollback: " + ex.getMessage());
            }
            System.err.println("Error al registrar venta: " + e.getMessage());
            return false;
        } finally {
            try {
                if (conn != null && !conn.isClosed()) conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }

    public static List<Factura> obtenerTodasLasFacturas() {
        List<Factura> facturas = new ArrayList<>();
        String sqlFacturas = "SELECT id, fecha, total FROM facturas ORDER BY fecha DESC";
        String sqlProductos = "SELECT codigo_producto, cantidad FROM factura_productos WHERE id_factura = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rsFacturas = stmt.executeQuery(sqlFacturas)) {
            while (rsFacturas.next()) {
                int id = rsFacturas.getInt("id");
                String fecha = rsFacturas.getString("fecha");
                double total = rsFacturas.getDouble("total");
                List<Factura.ProductoCantidad> productos = new ArrayList<>();

                try (PreparedStatement pstmt = conn.prepareStatement(sqlProductos)) {
                    pstmt.setInt(1, id);
                    ResultSet rsProductos = pstmt.executeQuery();
                    while (rsProductos.next()) {
                        String codigo = rsProductos.getString("codigo_producto");
                        int cantidad = rsProductos.getInt("cantidad");
                        Producto producto = obtenerProductoPorCodigo(codigo);
                        if (producto != null) {
                            productos.add(new Factura.ProductoCantidad(producto, cantidad));
                        }
                    }
                }

                facturas.add(new Factura(id, productos, total, fecha));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener facturas: " + e.getMessage());
        }
        return facturas;
    }

    // ================= MÉTODOS PARA COMPRAS =================
    public static boolean registrarCompra(String producto, int cantidad, double precioUnitario, String distribuidor) {
        String sql = "INSERT INTO compras (producto, cantidad, precio_unitario, distribuidor, fecha) " +
                    "VALUES (?, ?, ?, ?, datetime('now'))";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, producto);
            pstmt.setInt(2, cantidad);
            pstmt.setDouble(3, precioUnitario);
            pstmt.setString(4, distribuidor);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al registrar compra: " + e.getMessage());
            return false;
        }
    }

    public static List<Object[]> obtenerHistorialCompras() {
        List<Object[]> compras = new ArrayList<>();
        String sql = "SELECT id, strftime('%Y-%m-%d %H:%M', fecha) as fecha, producto, " +
                     "cantidad, precio_unitario, (cantidad * precio_unitario) as total, distribuidor " +
                     "FROM compras ORDER BY fecha DESC";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                compras.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("fecha"),
                    rs.getString("producto"),
                    rs.getInt("cantidad"),
                    rs.getDouble("precio_unitario"),
                    rs.getDouble("total"),
                    rs.getString("distribuidor")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener historial de compras: " + e.getMessage());
        }
        return compras;
    }

    public static List<String> obtenerDistribuidores() {
        List<String> distribuidores = new ArrayList<>();
        String sql = "SELECT DISTINCT distribuidor FROM compras ORDER BY distribuidor";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                distribuidores.add(rs.getString("distribuidor"));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener distribuidores: " + e.getMessage());
        }
        if (distribuidores.isEmpty()) {
            distribuidores.add("Distribuidor Principal");
        }
        return distribuidores;
    }
}