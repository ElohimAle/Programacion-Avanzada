package tarea03layaout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FlowLayoutAreaCalculator {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Layout FlowLayout Center");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 100);
        frame.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // Crear los componentes
        JLabel labelAltura = new JLabel("Altura -->");
        JTextField textAltura = new JTextField(5);
        JLabel labelBase = new JLabel("Base -->");
        JTextField textBase = new JTextField(5);
        JButton btnCalcular = new JButton("Calcular");
        JLabel labelArea = new JLabel("El área -->");
        JTextField textArea = new JTextField(5);
        textArea.setEditable(false);
        JButton btnSalir = new JButton("Salir");

        // Agregar funcionalidad al botón "Calcular"
        btnCalcular.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double altura = Double.parseDouble(textAltura.getText());
                    double base = Double.parseDouble(textBase.getText());
                    double area = altura * base;
                    textArea.setText(String.valueOf(area));
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Por favor, ingresa valores numéricos válidos.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Agregar funcionalidad al botón "Salir"
        btnSalir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // Agregar los componentes al JFrame
        frame.add(labelAltura);
        frame.add(textAltura);
        frame.add(labelBase);
        frame.add(textBase);
        frame.add(btnCalcular);
        frame.add(labelArea);
        frame.add(textArea);
        frame.add(btnSalir);

        // Hacer visible el JFrame
        frame.setVisible(true);
    }
}
