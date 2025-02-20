package tarea10ntmaui;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseOperations {
    private static final String DB_URL = "jdbc:sqlite:people.db";

    public static void addPerson(String name) {
        String sql = "INSERT INTO people (name) VALUES (?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getAllPeople() {
        StringBuilder result = new StringBuilder();
        String sql = "SELECT * FROM people";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.append("ID: ").append(rs.getInt("id"))
                      .append(", Nombre: ").append(rs.getString("name"))
                      .append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}

