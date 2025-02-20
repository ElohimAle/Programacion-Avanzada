package tarea10netmaui;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ConsumoRestFrame extends JFrame {

    private JTextArea textArea;
    private JButton btnGet, btnPost, btnPut, btnDelete;
    private HttpClient client;

    public ConsumoRestFrame() {
        initUI();
        client = HttpClient.newHttpClient();
    }

    private void initUI() {
        setTitle("Consumo de un servicio REST con HttpClient");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panelBotones = new JPanel();
        btnGet = new JButton("GET");
        btnPost = new JButton("POST");
        btnPut = new JButton("PUT");
        btnDelete = new JButton("DELETE");

        panelBotones.add(btnGet);
        panelBotones.add(btnPost);
        panelBotones.add(btnPut);
        panelBotones.add(btnDelete);

        add(panelBotones, BorderLayout.NORTH);

        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        btnGet.addActionListener(e -> realizarGet());
        btnPost.addActionListener(e -> realizarPost());
        btnPut.addActionListener(e -> realizarPut());
        btnDelete.addActionListener(e -> realizarDelete());
    }

    private void realizarGet() {
        String url = "https://jsonplaceholder.typicode.com/posts/1"; 
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        textArea.setText("Realizando solicitud GET...\n");

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(HttpResponse::body)
            .thenAccept(responseBody -> SwingUtilities.invokeLater(() -> {
                textArea.append("Respuesta GET:\n" + responseBody + "\n");
            }))
            .exceptionally(ex -> {
                SwingUtilities.invokeLater(() -> {
                    textArea.append("Error en GET: " + ex.getMessage() + "\n");
                });
                return null;
            });
    }

    // Método para realizar una solicitud POST (creación de recurso)
    private void realizarPost() {
        String url = "https://jsonplaceholder.typicode.com/posts"; // URL de ejemplo para POST
        // Datos JSON a enviar
        String json = "{\"title\": \"foo\", \"body\": \"bar\", \"userId\": 1}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        textArea.setText("Realizando solicitud POST...\n");

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(HttpResponse::body)
            .thenAccept(responseBody -> SwingUtilities.invokeLater(() -> {
                textArea.append("Respuesta POST:\n" + responseBody + "\n");
            }))
            .exceptionally(ex -> {
                SwingUtilities.invokeLater(() -> {
                    textArea.append("Error en POST: " + ex.getMessage() + "\n");
                });
                return null;
            });
    }

    // Método para realizar una solicitud PUT (actualización de recurso)
    private void realizarPut() {
        String url = "https://jsonplaceholder.typicode.com/posts/1"; // URL de ejemplo para PUT
        // Datos JSON actualizados
        String json = "{\"id\": 1, \"title\": \"updated title\", \"body\": \"updated body\", \"userId\": 1}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        textArea.setText("Realizando solicitud PUT...\n");

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(HttpResponse::body)
            .thenAccept(responseBody -> SwingUtilities.invokeLater(() -> {
                textArea.append("Respuesta PUT:\n" + responseBody + "\n");
            }))
            .exceptionally(ex -> {
                SwingUtilities.invokeLater(() -> {
                    textArea.append("Error en PUT: " + ex.getMessage() + "\n");
                });
                return null;
            });
    }

    // Método para realizar una solicitud DELETE (eliminación de recurso)
    private void realizarDelete() {
        String url = "https://jsonplaceholder.typicode.com/posts/1"; // URL de ejemplo para DELETE

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .DELETE()
                .build();

        textArea.setText("Realizando solicitud DELETE...\n");

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(HttpResponse::body)
            .thenAccept(responseBody -> SwingUtilities.invokeLater(() -> {
                textArea.append("Respuesta DELETE:\n" + responseBody + "\n");
            }))
            .exceptionally(ex -> {
                SwingUtilities.invokeLater(() -> {
                    textArea.append("Error en DELETE: " + ex.getMessage() + "\n");
                });
                return null;
            });
    }

    // Método main para iniciar la aplicación
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ConsumoRestFrame frame = new ConsumoRestFrame();
            frame.setVisible(true);
        });
    }
}