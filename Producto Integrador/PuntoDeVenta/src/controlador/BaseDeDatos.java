package controlador;

import java.io.File;
import java.sql.*;
import java.util.*;

import modelo.Factura;
import modelo.Producto;

public class BaseDeDatos {
    private static final String URL = "jdbc:sqlite:" + resolverRutaBaseDatos();

    private static String resolverRutaBaseDatos() {
        File actual = new File(System.getProperty("user.dir")).getAbsoluteFile();
        while (actual != null) {
            File dentroDelProyecto = new File(actual, "Producto Integrador/PuntoDeVenta/pdv.db");
            if (dentroDelProyecto.exists()) {
                return dentroDelProyecto.getPath();
            }

            File directa = new File(actual, "pdv.db");
            if (directa.exists()) {
                return directa.getPath();
            }

            actual = actual.getParentFile();
        }
        return "pdv.db";
    }

    public static void inicializarBaseDatos() {
        String sqlProductos = "CREATE TABLE IF NOT EXISTS productos (" +
                            "codigo TEXT PRIMARY KEY," +
                            "nombre TEXT NOT NULL," +
                            "precio REAL NOT NULL," +
                            "cantidad INTEGER NOT NULL DEFAULT 0," +
                            "stock_minimo INTEGER NOT NULL DEFAULT 0," +
                            "imagen TEXT NOT NULL DEFAULT '')";
        String sqlFacturas = "CREATE TABLE IF NOT EXISTS facturas (" +
                           "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                           "fecha TEXT NOT NULL," +
                           "total REAL NOT NULL," +
                           "descuento REAL NOT NULL DEFAULT 0," +
                           "total_pagar REAL NOT NULL DEFAULT 0)";
        String sqlFacturaProductos = "CREATE TABLE IF NOT EXISTS factura_productos (" +
                                   "id_factura INTEGER," +
                                   "codigo_producto TEXT," +
                                   "cantidad INTEGER," +
                                   "FOREIGN KEY(id_factura) REFERENCES facturas(id)," +
                                   "FOREIGN KEY(codigo_producto) REFERENCES productos(codigo))";
        String sqlCompras = "CREATE TABLE IF NOT EXISTS compras (" +
                          "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                          "codigo_producto TEXT," +
                          "producto TEXT NOT NULL," +
                          "cantidad INTEGER NOT NULL," +
                          "precio_unitario REAL NOT NULL," +
                          "distribuidor TEXT NOT NULL," +
                          "fecha TEXT NOT NULL)";
        String sqlDevoluciones = "CREATE TABLE IF NOT EXISTS devoluciones (" +
                                 "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                 "factura_id INTEGER NOT NULL," +
                                 "codigo_producto TEXT NOT NULL," +
                                 "cantidad INTEGER NOT NULL," +
                                 "fecha TEXT NOT NULL," +
                                 "FOREIGN KEY(factura_id) REFERENCES facturas(id)," +
                                 "FOREIGN KEY(codigo_producto) REFERENCES productos(codigo))";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlProductos);
            stmt.execute(sqlFacturas);
            stmt.execute(sqlFacturaProductos);
            stmt.execute(sqlCompras);
            stmt.execute(sqlDevoluciones);

            ensureColumnExists(conn, "productos", "stock_minimo", "stock_minimo INTEGER NOT NULL DEFAULT 0");
            ensureColumnExists(conn, "productos", "imagen", "imagen TEXT NOT NULL DEFAULT ''");
            ensureColumnExists(conn, "facturas", "descuento", "descuento REAL NOT NULL DEFAULT 0");
            ensureColumnExists(conn, "facturas", "total_pagar", "total_pagar REAL NOT NULL DEFAULT 0");
            ensureColumnExists(conn, "compras", "codigo_producto", "codigo_producto TEXT");
        } catch (SQLException e) {
            System.err.println("Error al inicializar BD: " + e.getMessage());
        }
    }

    private static void ensureColumnExists(Connection conn, String tableName, String columnName, String columnDefinition) throws SQLException {
        if (!columnExists(conn, tableName, columnName)) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("ALTER TABLE " + tableName + " ADD COLUMN " + columnDefinition);
            }
        }
    }

    private static boolean columnExists(Connection conn, String tableName, String columnName) throws SQLException {
        String sql = "PRAGMA table_info(" + tableName + ")";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                if (columnName.equalsIgnoreCase(rs.getString("name"))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean guardarProducto(Producto producto) {
        String sql = "INSERT OR REPLACE INTO productos (codigo, nombre, precio, cantidad, stock_minimo, imagen) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, producto.getCodigo());
            pstmt.setString(2, producto.getNombre());
            pstmt.setDouble(3, producto.getPrecio());
            pstmt.setInt(4, producto.getCantidad());
            pstmt.setInt(5, producto.getStockMinimo());
            pstmt.setString(6, producto.getImagen());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al guardar producto: " + e.getMessage());
            return false;
        }
    }

    public static Map<String, Producto> obtenerTodosLosProductos() {
        Map<String, Producto> productos = new HashMap<>();
        String sql = "SELECT codigo, nombre, precio, cantidad, stock_minimo, imagen FROM productos";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Producto p = new Producto(
                    rs.getString("codigo"),
                    rs.getString("nombre"),
                    rs.getDouble("precio"),
                    rs.getInt("cantidad"),
                    rs.getInt("stock_minimo"),
                    rs.getString("imagen")
                );
                productos.put(p.getCodigo(), p);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener productos: " + e.getMessage());
        }
        return productos;
    }

    public static Producto obtenerProductoPorCodigo(String codigo) {
        String sql = "SELECT nombre, precio, cantidad, stock_minimo, imagen FROM productos WHERE codigo = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, codigo);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Producto(codigo,
                                    rs.getString("nombre"),
                                    rs.getDouble("precio"),
                                    rs.getInt("cantidad"),
                                    rs.getInt("stock_minimo"),
                                    rs.getString("imagen"));
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

    public static boolean actualizarStockMinimo(String codigo, int stockMinimo) {
        String sql = "UPDATE productos SET stock_minimo = ? WHERE codigo = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, stockMinimo);
            pstmt.setString(2, codigo);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar stock minimo: " + e.getMessage());
            return false;
        }
    }

    public static List<Producto> buscarProductos(String texto) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT codigo, nombre, precio, cantidad, stock_minimo, imagen FROM productos " +
                     "WHERE lower(codigo) LIKE ? OR lower(nombre) LIKE ? ORDER BY nombre LIMIT 50";
        String patron = "%" + (texto == null ? "" : texto.toLowerCase()) + "%";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, patron);
            pstmt.setString(2, patron);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                productos.add(new Producto(
                    rs.getString("codigo"),
                    rs.getString("nombre"),
                    rs.getDouble("precio"),
                    rs.getInt("cantidad"),
                    rs.getInt("stock_minimo"),
                    rs.getString("imagen")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar productos: " + e.getMessage());
        }
        return productos;
    }

    public static Map<String, Producto> obtenerProductosBajoStock() {
        Map<String, Producto> productos = new HashMap<>();
        String sql = "SELECT codigo, nombre, precio, cantidad, stock_minimo, imagen FROM productos WHERE cantidad <= stock_minimo";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Producto p = new Producto(
                    rs.getString("codigo"),
                    rs.getString("nombre"),
                    rs.getDouble("precio"),
                    rs.getInt("cantidad"),
                    rs.getInt("stock_minimo"),
                    rs.getString("imagen")
                );
                productos.put(p.getCodigo(), p);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener productos de bajo stock: " + e.getMessage());
        }
        return productos;
    }

    public static int getProductCount() {
        String sql = "SELECT COUNT(*) AS total FROM productos";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt("total") : 0;
        } catch (SQLException e) {
            System.err.println("Error al contar productos: " + e.getMessage());
            return 0;
        }
    }

    public static void generarProductosDemo(int minimo) {
        int count = getProductCount();
        if (count >= minimo) {
            return;
        }

        String[] nombresDemo = {
            "Coca Cola", "Pepsi", "Fanta", "Sprite", "Agua Mineral", "Jugo de Naranja",
            "Leche Entera", "Pan de Molde", "Queso Amarillo", "Mantequilla", "Huevos", "Arroz",
            "Frijoles", "Pasta Spaghetti", "Harina de Trigo", "Azúcar", "Café Molido", "Té Verde",
            "Chocolate", "Galletas", "Cereal", "Yogurt Natural", "Salsa Catsup", "Mostaza",
            "Mayonesa", "Aceite de Oliva", "Sal Marina", "Pimienta", "Enlatado de Atún", "Sardinas",
            "Pollo Entero", "Carne de Res", "Filete de Pescado", "Helado", "Tortillas", "Salsa Picante",
            "Jabón Líquido", "Shampoo", "Pasta Dental", "Detergente", "Servilletas", "Papel Higiénico",
            "Fruta Mixta", "Verduras Frescas", "Cerveza", "Vino Tinto", "Bebida Energética", "Café Instantáneo",
            "Jugo de Manzana", "Leche de Almendra", "Barra de Cereal", "Snacks Salados", "Jamón", "Queso Crema",
            "Galletas Dulces", "Caramelo", "Miel", "Puré de Tomate", "Gaseosa Zero", "Agua de Coco",
            "Sopa Instantánea", "Croissants", "Panadería", "Refresco Limonada", "Aceite Vegetal", "Vinagre",
            "Papas Fritas", "Salsa BBQ", "Queso Mozzarella", "Postre", "Café Descafeinado", "Cheetos",
            "Agua Saborizada", "Crema", "Yogurt Griego", "Ensalada Lista", "Puré de Papas", "Atún en Agua",
            "Mermelada", "Cereal Infantil", "Nueces", "Leche Condensada", "Salsa de Soja", "Cereal Integral",
            "Chocolate Blanco", "Pan Integral", "Queso Parmesano", "Avena", "Café en Grano", "Té Negro",
            "Pasta Integral", "Espagueti", "Galletas Saladas", "Bebida de Soya", "Salsa Alfredo", "Crema Agria",
            "Jugo de Uva", "Bebida de Té", "Galletas de Chocolate", "Pan Dulce", "Helado de Vainilla", "Vino Blanco",
            "Bebida Isotónica", "Salsa Worcestershire", "Mostaza Dijon", "Catsup Picante", "Aceite de Coco"
        };

        for (int i = count + 1; i <= minimo; i++) {
            String codigo = String.format("P%04d", i);
            String nombre = nombresDemo[(i - 1) % nombresDemo.length];
            double precio = 10.0 + (i % 15) * 2.0;
            int cantidad = 20;
            int stockMinimo = 5;
            Producto producto = new Producto(codigo, nombre, precio, cantidad, stockMinimo, "/resources/producto.png");
            guardarProducto(producto);
        }
    }

    public static boolean registrarDevolucion(int facturaId, String codigoProducto, int cantidad) {
        String sqlDevolucion = "INSERT INTO devoluciones (factura_id, codigo_producto, cantidad, fecha) VALUES (?, ?, ?, datetime('now'))";
        String sqlActualizarStock = "UPDATE productos SET cantidad = cantidad + ? WHERE codigo = ?";
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(URL);
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(sqlDevolucion)) {
                pstmt.setInt(1, facturaId);
                pstmt.setString(2, codigoProducto);
                pstmt.setInt(3, cantidad);
                pstmt.executeUpdate();
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sqlActualizarStock)) {
                pstmt.setInt(1, cantidad);
                pstmt.setString(2, codigoProducto);
                pstmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Error al hacer rollback en devolución: " + ex.getMessage());
            }
            System.err.println("Error al registrar devolución: " + e.getMessage());
            return false;
        } finally {
            try {
                if (conn != null && !conn.isClosed()) conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
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
        String sqlFactura = "INSERT INTO facturas (fecha, total, descuento, total_pagar) VALUES (?, ?, ?, ?)";
        String sqlProductos = "INSERT INTO factura_productos (id_factura, codigo_producto, cantidad) VALUES (?, ?, ?)";
        String sqlActualizarStock = "UPDATE productos SET cantidad = cantidad - ? WHERE codigo = ?";
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(URL);
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(sqlFactura, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, factura.getFecha());
                pstmt.setDouble(2, factura.getTotal());
                pstmt.setDouble(3, factura.getDescuento());
                pstmt.setDouble(4, factura.getTotalPagar());
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
        String sqlFacturas = "SELECT id, fecha, total, descuento, total_pagar FROM facturas ORDER BY fecha DESC";
        String sqlProductos = "SELECT codigo_producto, cantidad FROM factura_productos WHERE id_factura = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rsFacturas = stmt.executeQuery(sqlFacturas)) {
            while (rsFacturas.next()) {
                int id = rsFacturas.getInt("id");
                String fecha = rsFacturas.getString("fecha");
                double total = rsFacturas.getDouble("total");
                double descuento = rsFacturas.getDouble("descuento");
                double totalPagar = rsFacturas.getDouble("total_pagar");
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

                facturas.add(new Factura(id, productos, total, descuento, totalPagar, fecha));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener facturas: " + e.getMessage());
        }
        return facturas;
    }

    public static List<Factura> obtenerFacturasPorRango(String fechaInicio, String fechaFin) {
        if (fechaInicio == null || fechaInicio.isBlank() || fechaFin == null || fechaFin.isBlank()) {
            return obtenerTodasLasFacturas();
        }

        List<Factura> facturas = new ArrayList<>();
        String sqlFacturas = "SELECT id, fecha, total, descuento, total_pagar FROM facturas " +
                            "WHERE fecha BETWEEN ? AND ? ORDER BY fecha DESC";
        String sqlProductos = "SELECT codigo_producto, cantidad FROM factura_productos WHERE id_factura = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmtFacturas = conn.prepareStatement(sqlFacturas)) {
            pstmtFacturas.setString(1, fechaInicio + " 00:00:00");
            pstmtFacturas.setString(2, fechaFin + " 23:59:59");
            ResultSet rsFacturas = pstmtFacturas.executeQuery();

            while (rsFacturas.next()) {
                int id = rsFacturas.getInt("id");
                String fecha = rsFacturas.getString("fecha");
                double total = rsFacturas.getDouble("total");
                double descuento = rsFacturas.getDouble("descuento");
                double totalPagar = rsFacturas.getDouble("total_pagar");
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

                facturas.add(new Factura(id, productos, total, descuento, totalPagar, fecha));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener facturas por rango: " + e.getMessage());
        }
        return facturas;
    }

    // ================= MÉTODOS PARA COMPRAS =================
    public static boolean registrarCompra(String producto, int cantidad, double precioUnitario, String distribuidor) {
        return registrarCompra(null, producto, cantidad, precioUnitario, distribuidor);
    }

    public static boolean registrarCompra(String codigoProducto, String producto, int cantidad, double precioUnitario, String distribuidor) {
        String sqlCompra = "INSERT INTO compras (codigo_producto, producto, cantidad, precio_unitario, distribuidor, fecha) " +
                           "VALUES (?, ?, ?, ?, ?, datetime('now'))";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(sqlCompra)) {
                pstmt.setString(1, codigoProducto);
                pstmt.setString(2, producto);
                pstmt.setInt(3, cantidad);
                pstmt.setDouble(4, precioUnitario);
                pstmt.setString(5, distribuidor);
                pstmt.executeUpdate();
            }

            if (codigoProducto != null && !codigoProducto.isBlank()) {
                try (PreparedStatement pstmtStock = conn.prepareStatement("UPDATE productos SET cantidad = cantidad + ? WHERE codigo = ?")) {
                    pstmtStock.setInt(1, cantidad);
                    pstmtStock.setString(2, codigoProducto);
                    pstmtStock.executeUpdate();
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Error al hacer rollback en compra: " + ex.getMessage());
            }
            System.err.println("Error al registrar compra: " + e.getMessage());
            return false;
        } finally {
            try {
                if (conn != null && !conn.isClosed()) conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexion: " + e.getMessage());
            }
        }
    }

    public static List<Object[]> obtenerHistorialCompras() {
        List<Object[]> compras = new ArrayList<>();
        String sql = "SELECT id, strftime('%Y-%m-%d %H:%M', fecha) as fecha, codigo_producto, producto, " +
                     "cantidad, precio_unitario, (cantidad * precio_unitario) as total, distribuidor " +
                     "FROM compras ORDER BY fecha DESC";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                compras.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("fecha"),
                    rs.getString("codigo_producto"),
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

    public static boolean eliminarCompra(int idCompra) {
        String selectSql = "SELECT codigo_producto, cantidad FROM compras WHERE id = ?";
        String deleteSql = "DELETE FROM compras WHERE id = ?";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
            conn.setAutoCommit(false);

            String codigoProducto = null;
            int cantidad = 0;
            try (PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
                pstmt.setInt(1, idCompra);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    codigoProducto = rs.getString("codigo_producto");
                    cantidad = rs.getInt("cantidad");
                } else {
                    return false;
                }
            }

            try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
                pstmt.setInt(1, idCompra);
                pstmt.executeUpdate();
            }

            if (codigoProducto != null && !codigoProducto.isBlank()) {
                try (PreparedStatement pstmtStock = conn.prepareStatement("UPDATE productos SET cantidad = max(0, cantidad - ?) WHERE codigo = ?")) {
                    pstmtStock.setInt(1, cantidad);
                    pstmtStock.setString(2, codigoProducto);
                    pstmtStock.executeUpdate();
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Error al hacer rollback al eliminar compra: " + ex.getMessage());
            }
            System.err.println("Error al eliminar compra: " + e.getMessage());
            return false;
        } finally {
            try {
                if (conn != null && !conn.isClosed()) conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexion: " + e.getMessage());
            }
        }
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
