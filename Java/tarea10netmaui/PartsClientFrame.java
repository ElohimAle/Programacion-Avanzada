package tarea10netmaui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Part {
    public String partID;
    public String partName;
    public String partType;
    public String partAvailableDate; 
    public List<String> suppliers;

    public Part() {}

    @Override
    public String toString() {
        return partName;
    }
}

class PartsManager {

    private static final String BASE_ADDRESS = "http://YOUR_SERVICE_URL";
    private static final String URL = BASE_ADDRESS + "/api/";
    private static String authorizationKey = null;
    private static HttpClient client = null;

    public static HttpClient getClient() throws Exception {
        if (client == null) {
            client = HttpClient.newHttpClient();
        }
        if (authorizationKey == null || authorizationKey.isEmpty()) {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "login"))
                    .header("Accept", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() != 200) {
                throw new RuntimeException("Login failed with status: " + resp.statusCode());
            }

            authorizationKey = stripQuotes(resp.body().trim());
        }
        return client;
    }

    public static List<Part> getAll() throws Exception {
        HttpClient client = getClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "parts"))
                .header("Authorization", authorizationKey)
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to get parts, status: " + response.statusCode());
        }
        return parseParts(response.body());
    }

    public static Part add(String partName, String partType, String supplier) throws Exception {
        HttpClient client = getClient();
        Part part = new Part();
        part.partID = ""; 
        part.partName = partName;
        part.partType = partType;
        part.partAvailableDate = LocalDate.now().toString();
        part.suppliers = new ArrayList<>();
        part.suppliers.add(supplier);

        String json = partToJson(part);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "parts"))
                .header("Authorization", authorizationKey)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("Failed to add part, status: " + response.statusCode());
        }
        return parsePart(response.body());
    }

    public static void update(Part part) throws Exception {
        HttpClient client = getClient();
        String json = partToJson(part);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "parts/" + part.partID))
                .header("Authorization", authorizationKey)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("Failed to update part, status: " + response.statusCode());
        }
    }

    public static void delete(String partID) throws Exception {
        HttpClient client = getClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "parts/" + partID))
                .header("Authorization", authorizationKey)
                .header("Accept", "application/json")
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("Failed to delete part, status: " + response.statusCode());
        }
    }

    private static String stripQuotes(String s) {
        if (s.startsWith("\"") && s.endsWith("\"") && s.length() >= 2) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

    private static String partToJson(Part p) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"partID\":\"").append(p.partID == null ? "" : escapeJson(p.partID)).append("\",");
        sb.append("\"partName\":\"").append(p.partName == null ? "" : escapeJson(p.partName)).append("\",");
        sb.append("\"partType\":\"").append(p.partType == null ? "" : escapeJson(p.partType)).append("\",");
        sb.append("\"partAvailableDate\":\"").append(p.partAvailableDate == null ? "" : escapeJson(p.partAvailableDate)).append("\",");
        sb.append("\"suppliers\":[");
        if (p.suppliers != null) {
            for (int i = 0; i < p.suppliers.size(); i++) {
                sb.append("\"").append(escapeJson(p.suppliers.get(i))).append("\"");
                if (i < p.suppliers.size() - 1)
                    sb.append(",");
            }
        }
        sb.append("]");
        sb.append("}");
        return sb.toString();
    }

    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static List<Part> parseParts(String json) {
        List<Part> parts = new ArrayList<>();
        json = json.trim();
        if (json.startsWith("[")) {
            json = json.substring(1);
        }
        if (json.endsWith("]")) {
            json = json.substring(0, json.length() - 1);
        }

        String[] items = json.split("\\},\\{");
        for (int i = 0; i < items.length; i++) {
            String item = items[i];
            if (!item.startsWith("{")) {
                item = "{" + item;
            }
            if (!item.endsWith("}")) {
                item = item + "}";
            }
            Part p = parsePart(item);
            parts.add(p);
        }
        return parts;
    }

    private static Part parsePart(String json) {
        Part p = new Part();
        p.partID = getJsonStringValue(json, "partID");
        p.partName = getJsonStringValue(json, "partName");
        p.partType = getJsonStringValue(json, "partType");
        p.partAvailableDate = getJsonStringValue(json, "partAvailableDate");
        p.suppliers = getJsonArrayValue(json, "suppliers");
        return p;
    }

    private static String getJsonStringValue(String json, String key) {
        String pattern = "\"" + key + "\"";
        int index = json.indexOf(pattern);
        if (index == -1) return "";
        int colon = json.indexOf(":", index);
        if (colon == -1) return "";
        int firstQuote = json.indexOf("\"", colon);
        if (firstQuote == -1) return "";
        int secondQuote = json.indexOf("\"", firstQuote + 1);
        if (secondQuote == -1) return "";
        return json.substring(firstQuote + 1, secondQuote);
    }

    private static List<String> getJsonArrayValue(String json, String key) {
        List<String> list = new ArrayList<>();
        String pattern = "\"" + key + "\"";
        int index = json.indexOf(pattern);
        if (index == -1) return list;
        int colon = json.indexOf(":", index);
        if (colon == -1) return list;
        int startBracket = json.indexOf("[", colon);
        int endBracket = json.indexOf("]", startBracket);
        if (startBracket == -1 || endBracket == -1) return list;
        String arrayContent = json.substring(startBracket + 1, endBracket).trim();
        if (arrayContent.isEmpty()) return list;
        String[] items = arrayContent.split(",");
        for (String item : items) {
            item = item.trim();
            if (item.startsWith("\"") && item.endsWith("\"")) {
                item = item.substring(1, item.length() - 1);
            }
            list.add(item);
        }
        return list;
    }
}

public class PartsClientFrame extends JFrame {

    private JTable partsTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton, addButton, updateButton, deleteButton;

    public PartsClientFrame() {
        setTitle("Parts Client");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        loadParts();
    }

    private void initComponents() {
        tableModel = new DefaultTableModel(new Object[]{"Part ID", "Name", "Type", "Available Date", "Suppliers"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        partsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(partsTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        refreshButton = new JButton("Refresh");
        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");

        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> loadParts());
        addButton.addActionListener(e -> addPart());
        updateButton.addActionListener(e -> updatePart());
        deleteButton.addActionListener(e -> deletePart());
    }


    private void loadParts() {
        SwingWorker<List<Part>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Part> doInBackground() throws Exception {
                return PartsManager.getAll();
            }

            @Override
            protected void done() {
                try {
                    List<Part> parts = get();
                    tableModel.setRowCount(0);
                    for (Part p : parts) {
                        String suppliersStr = String.join(", ", p.suppliers);
                        tableModel.addRow(new Object[]{p.partID, p.partName, p.partType, p.partAvailableDate, suppliersStr});
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(PartsClientFrame.this,
                            "Error loading parts: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void addPart() {
        JTextField nameField = new JTextField();
        JTextField typeField = new JTextField();
        JTextField supplierField = new JTextField();
        Object[] message = {
            "Name:", nameField,
            "Type:", typeField,
            "Supplier:", supplierField
        };
        int option = JOptionPane.showConfirmDialog(this, message, "Add New Part", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String type = typeField.getText().trim();
            String supplier = supplierField.getText().trim();
            if (name.isEmpty() || type.isEmpty() || supplier.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be filled.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            SwingWorker<Part, Void> worker = new SwingWorker<>() {
                @Override
                protected Part doInBackground() throws Exception {
                    return PartsManager.add(name, type, supplier);
                }
                @Override
                protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(PartsClientFrame.this, "Part added successfully!");
                        loadParts();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(PartsClientFrame.this,
                                "Error adding part: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }

    private void updatePart() {
        int selectedRow = partsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a part to update.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String partID = (String) tableModel.getValueAt(selectedRow, 0);
        String currentName = (String) tableModel.getValueAt(selectedRow, 1);
        String currentType = (String) tableModel.getValueAt(selectedRow, 2);
        String currentDate = (String) tableModel.getValueAt(selectedRow, 3);
        String currentSuppliers = (String) tableModel.getValueAt(selectedRow, 4);

        JTextField nameField = new JTextField(currentName);
        JTextField typeField = new JTextField(currentType);
        JTextField suppliersField = new JTextField(currentSuppliers);
        Object[] message = {
            "Name:", nameField,
            "Type:", typeField,
            "Suppliers (comma separated):", suppliersField
        };
        int option = JOptionPane.showConfirmDialog(this, message, "Update Part", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String newName = nameField.getText().trim();
            String newType = typeField.getText().trim();
            String suppliersText = suppliersField.getText().trim();
            if (newName.isEmpty() || newType.isEmpty() || suppliersText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be filled.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Part updatedPart = new Part();
            updatedPart.partID = partID;
            updatedPart.partName = newName;
            updatedPart.partType = newType;
            updatedPart.partAvailableDate = currentDate; 
            updatedPart.suppliers = Arrays.asList(suppliersText.split("\\s*,\\s*"));

            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    PartsManager.update(updatedPart);
                    return null;
                }
                @Override
                protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(PartsClientFrame.this, "Part updated successfully!");
                        loadParts();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(PartsClientFrame.this,
                                "Error updating part: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }

    private void deletePart() {
        int selectedRow = partsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a part to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String partID = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete part " + partID + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    PartsManager.delete(partID);
                    return null;
                }
                @Override
                protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(PartsClientFrame.this, "Part deleted successfully!");
                        loadParts();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(PartsClientFrame.this,
                                "Error deleting part: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PartsClientFrame().setVisible(true);
        });
    }
}