package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:tienda.db";
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
        }
        return connection;
    }

    // Métodos para inicializar la base de datos y usuarios
    public static void initializeDatabase() {
        createTables();
        insertAdminUserIfNotExists();
    }

    private static void createTables() {
        String[] sqls = {
            "CREATE TABLE IF NOT EXISTS usuarios (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "username TEXT UNIQUE NOT NULL," +
            "password TEXT NOT NULL," +
            "nombre TEXT NOT NULL," +
            "rol TEXT NOT NULL)",

            "CREATE TABLE IF NOT EXISTS turnos (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "usuario_id INTEGER NOT NULL," +
            "fecha_apertura TEXT NOT NULL," +
            "fecha_cierre TEXT," +
            "efectivo_inicial REAL NOT NULL," +
            "efectivo_final REAL," +
            "FOREIGN KEY (usuario_id) REFERENCES usuarios(id))",

            "CREATE TABLE IF NOT EXISTS productos (" +
            "codigo TEXT PRIMARY KEY," +
            "nombre TEXT NOT NULL," +
            "precio REAL NOT NULL," +
            "cantidad INTEGER NOT NULL," +
            "categoria TEXT)",

            "CREATE TABLE IF NOT EXISTS facturas (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "turno_id INTEGER NOT NULL," +
            "fecha TEXT NOT NULL," +
            "total REAL NOT NULL," +
            "metodo_pago TEXT NOT NULL," +
            "FOREIGN KEY (turno_id) REFERENCES turnos(id))",

            "CREATE TABLE IF NOT EXISTS detalle_factura (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "factura_id INTEGER NOT NULL," +
            "producto_codigo TEXT NOT NULL," +
            "cantidad INTEGER NOT NULL," +
            "precio_unitario REAL NOT NULL," +
            "FOREIGN KEY (factura_id) REFERENCES facturas(id)," +
            "FOREIGN KEY (producto_codigo) REFERENCES productos(codigo))"
        };

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            for (String sql : sqls) {
                stmt.execute(sql);
            }
        } catch (SQLException e) {
            System.err.println("Error al crear tablas: " + e.getMessage());
        }
    }

    private static void insertAdminUserIfNotExists() {
        String checkSql = "SELECT COUNT(*) FROM usuarios WHERE username = 'admin'";
        String insertSql = "INSERT INTO usuarios (username, password, nombre, rol) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {

            if (rs.getInt(1) == 0) {
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setString(1, "admin");
                    pstmt.setString(2, "1234"); // En producción, usar hash!
                    pstmt.setString(3, "Administrador");
                    pstmt.setString(4, "admin");
                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar usuario admin: " + e.getMessage());
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar conexión: " + e.getMessage());
        }
    }
}