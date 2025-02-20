package tarea10ntmaui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:people.db";

    public static void createDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            if (conn != null) {
                Statement stmt = conn.createStatement();
                String sql = "CREATE TABLE IF NOT EXISTS people ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "name TEXT UNIQUE NOT NULL)";
                stmt.execute(sql);
                System.out.println("Base de datos creada con éxito.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
