package tarea06;

import javax.swing.*;
import java.awt.*;

public class GridExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Ejemplo de GridLayout");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLayout(new GridLayout(7, 5, 5, 5)); 
        for (int i = 1; i <= 7; i++) {
            for (int j = 1; j <= 5; j++) {
                JButton button = new JButton("Fila " + i + " Columna " + j);
                frame.add(button); 
            }
        }
        frame.setVisible(true);
    }
}
