package tarea04;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControlEventos extends JFrame {

    private JLabel counterLabel; 
    private JButton counterButton; 
    private int count = 0; 

    public ControlEventos() {
        setTitle("Control de Eventos");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        counterLabel = new JLabel("Current count: 0");
        counterLabel.setBounds(50, 50, 200, 30);

        counterButton = new JButton("Increase Count");
        counterButton.setBounds(50, 100, 150, 30); 

        counterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                count++;
                counterLabel.setText("Current count: " + count);
            }
        });
        add(counterLabel);
        add(counterButton);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ControlEventos ventana = new ControlEventos();
            ventana.setVisible(true);
        });
    }
}