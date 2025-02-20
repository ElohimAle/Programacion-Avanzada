package tarea04;

import javax.swing.*;
import java.awt.*;

public class GlobalFontSizeApp {
    public static final int GLOBAL_FONT_SIZE = 28;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Extensiones de marcado en Java Swing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(3, 1));
        JLabel label1 = createCustomLabel("Hola, Mundo!");
        JLabel label2 = createCustomLabel("Esto es Java Swing");
        JLabel label3 = createCustomLabel("Reutilizando tamaño de fuente");
        frame.add(label1);
        frame.add(label2);
        frame.add(label3);
        frame.setVisible(true);
    }
    private static JLabel createCustomLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, GLOBAL_FONT_SIZE));
        return label;
    }
}