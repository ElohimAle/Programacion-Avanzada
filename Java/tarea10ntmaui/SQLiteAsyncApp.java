package tarea10ntmaui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SQLiteAsyncApp extends JFrame {
    private JTextField nameField;
    private JTextArea outputArea;
    private JButton addButton, getButton;
    private Connection conn;

    public SQLiteAsyncApp() {
        setTitle("SQLite Async with JFrame");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        nameField = new JTextField(20);
        outputArea = new JTextArea(10, 30);
        addButton = new JButton("Add Person");
        getButton = new JButton("Get People");

        add(nameField);
        add(addButton);
        add(getButton);
        add(new JScrollPane(outputArea));

        // Event listeners
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                if (!name.isEmpty()) {
                    addPersonAsync(name);
                }
            }
        });

        getButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getPeopleAsync();
            }
        });

        initDBAsync();
    }

    private void initDBAsync() {
        CompletableFuture.runAsync(() -> {
            try {
                conn = DriverManager.getConnection("jdbc:sqlite:people.db");
                Statement stmt = conn.createStatement();
                stmt.execute("CREATE TABLE IF NOT EXISTS people (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE);");
                stmt.close();
                System.out.println("Database initialized");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void addPersonAsync(String name) {
        CompletableFuture.runAsync(() -> {
            try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO people (name) VALUES (?);");) {
                stmt.setString(1, name);
                stmt.executeUpdate();
                SwingUtilities.invokeLater(() -> outputArea.setText("Person added: " + name));
            } catch (SQLException e) {
                SwingUtilities.invokeLater(() -> outputArea.setText("Error adding person: " + e.getMessage()));
            }
        });
    }

    private void getPeopleAsync() {
        CompletableFuture.runAsync(() -> {
            List<String> people = new ArrayList<>();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM people;");) {
                while (rs.next()) {
                    people.add(rs.getInt("id") + ". " + rs.getString("name"));
                }
                SwingUtilities.invokeLater(() -> outputArea.setText(String.join("\n", people)));
            } catch (SQLException e) {
                SwingUtilities.invokeLater(() -> outputArea.setText("Error retrieving people: " + e.getMessage()));
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SQLiteAsyncApp().setVisible(true));
    }
}
