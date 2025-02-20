package tarea10ntmaui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PersonUI extends JFrame {
    private JTextField nameField;
    private JTextArea displayArea;
    
    public PersonUI() {
        setTitle("Gestión de Personas con SQLite");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        nameField = new JTextField(20);
        JButton addButton = new JButton("Agregar Persona");
        JButton getAllButton = new JButton("Obtener Todas");
        displayArea = new JTextArea(10, 30);
        displayArea.setEditable(false);

        add(new JLabel("Nombre:"));
        add(nameField);
        add(addButton);
        add(getAllButton);
        add(new JScrollPane(displayArea));

        // Eventos de los botones
        addButton.addActionListener(e -> addPerson());
        getAllButton.addActionListener(e -> getAllPeople());

        setVisible(true);
    }

    private void addPerson() {
        String name = nameField.getText();
        if (!name.isEmpty()) {
            DatabaseOperations.addPerson(name);
            nameField.setText("");
            JOptionPane.showMessageDialog(this, "Persona agregada con éxito.");
        } else {
            JOptionPane.showMessageDialog(this, "Ingrese un nombre válido.");
        }
    }

    private void getAllPeople() {
        displayArea.setText(DatabaseOperations.getAllPeople());
    }

    public static void main(String[] args) {
        DatabaseManager.createDatabase();  // Asegura que la BD existe
        new PersonUI();
    }
}
