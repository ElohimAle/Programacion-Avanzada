package tarea06;

import javax.swing.*;
import java.awt.*;

public class CustomViewExample {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Especificación del Tamaño de una Vista");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLayout(null); 
        JLabel label = new JLabel("Hello");
        label.setOpaque(true);
        label.setBackground(Color.LIGHT_GRAY);
        label.setFont(new Font("Arial", Font.PLAIN, 20));
        label.setBounds(100, 50, 200, 50); 
        JButton button = new JButton("Iniciar sesión");
        button.setBounds(100, 150, 200, 50);
        frame.add(label);
        frame.add(button);
        frame.setVisible(true);
    }
}