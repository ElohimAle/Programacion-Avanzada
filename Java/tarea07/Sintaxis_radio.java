package tarea07;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Sintaxis_radio {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Marco_radio_sintaxis minarco = new Marco_radio_sintaxis();
            minarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        });
    }
}

class Marco_radio_sintaxis extends JFrame {
    private Lamina_radio_sintaxis milLamina;

    public Marco_radio_sintaxis() {
        setTitle("Selector de Opciones");
        setBounds(350, 300, 500, 300);
        milLamina = new Lamina_radio_sintaxis();
        add(milLamina);
        setVisible(true);
    }
}

class Lamina_radio_sintaxis extends JPanel {
    private JRadioButton botonAzul, botonRojo, botonVerde, botonMasculino;
    private ButtonGroup grupoColores, grupoGenero;

    public Lamina_radio_sintaxis() {
        setLayout(new GridLayout(0, 1)); // Layout para organizar verticalmente

        // Grupo 1: Colores
        JPanel panelColores = new JPanel();
        grupoColores = new ButtonGroup();
        
        botonAzul = new JRadioButton("Azul", false);
        botonRojo = new JRadioButton("Rojo", true); // Seleccionado por defecto
        botonVerde = new JRadioButton("Verde", false);

        grupoColores.add(botonAzul);
        grupoColores.add(botonRojo);
        grupoColores.add(botonVerde);

        panelColores.add(new JLabel("Colores:"));
        panelColores.add(botonAzul);
        panelColores.add(botonRojo);
        panelColores.add(botonVerde);

        // Grupo 2: Género
        JPanel panelGenero = new JPanel();
        grupoGenero = new ButtonGroup();
        
        botonMasculino = new JRadioButton("Masculino", false);
        JRadioButton botonFemenino = new JRadioButton("Femenino", false);

        grupoGenero.add(botonMasculino);
        grupoGenero.add(botonFemenino);

        panelGenero.add(new JLabel("Género:"));
        panelGenero.add(botonMasculino);
        panelGenero.add(botonFemenino);

        // Añadir paneles a la lámina principal
        add(panelColores);
        add(panelGenero);

        // Eventos para cambiar el fondo (ejemplo)
        botonAzul.addActionListener(e -> setBackground(Color.BLUE));
        botonRojo.addActionListener(e -> setBackground(Color.RED));
        botonVerde.addActionListener(e -> setBackground(Color.GREEN));
    }
}