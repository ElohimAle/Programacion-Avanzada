package tarea07;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Fejemplo_radio {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Marco_radio minarco = new Marco_radio();
            minarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        });
    }
}

class Marco_radio extends JFrame {
    private Lamina_radio milamina;

    public Marco_radio() {
        setTitle("Selector de Tamaño de Fuente");
        setBounds(550, 500, 500, 550);
        milamina = new Lamina_radio();
        add(milamina);
        setVisible(true);
    }
}

class Lamina_radio extends JPanel {
    private JLabel texto;
    private JPanel lamina_botones;
    private ButtonGroup migrupo;

    public Lamina_radio() {
        setLayout(new BorderLayout());

        // Texto central
        texto = new JLabel("En un lugar de la mancha cuyo nombre......");
        texto.setFont(new Font("Serif", Font.PLAIN, 12));
        add(texto, BorderLayout.CENTER);

        // Panel para botones de radio
        lamina_botones = new JPanel();
        migrupo = new ButtonGroup();

        // Crear botones de radio con tamaños predefinidos
        colocarBotones("Pequeño", false, 10);
        colocarBotones("Mediano", true, 12); // Seleccionado por defecto
        colocarBotones("Grande", false, 18);
        colocarBotones("Muy grande", false, 26);

        add(lamina_botones, BorderLayout.SOUTH);
    }

    // Método para crear botones de radio y asignarles eventos
    public void colocarBotones(String nombre, boolean seleccionado, final int tamagno) {
        JRadioButton boton = new JRadioButton(nombre, seleccionado);
        migrupo.add(boton);
        lamina_botones.add(boton);

        // Evento para cambiar el tamaño del texto
        boton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                texto.setFont(new Font(texto.getFont().getName(), Font.PLAIN, tamagno));
            }
        });
    }
}