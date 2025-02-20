package tarea07;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PruebaCombo {
    public static void main(String[] args) {
        MarcoCombo miMarco = new MarcoCombo();
        miMarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

class MarcoCombo extends JFrame {
    public MarcoCombo() {
        setVisible(true);
        setBounds(558, 308, 558, 408);
        LaminaCombo miLamina = new LaminaCombo();
        add(miLamina);
    }
}

class LaminaCombo extends JPanel {
    private JLabel texto;
    private JComboBox<String> micombo;

    public LaminaCombo() {
        setLayout(new BorderLayout());

        // Texto central
        texto = new JLabel("En un lugar de la mancha de cuyo nombre...");
        texto.setFont(new Font("Serif", Font.PLAIN, 18));
        add(texto, BorderLayout.CENTER);

        // Panel superior con JComboBox
        JPanel lamina_norte = new JPanel(); // Corrección: operador "=" añadido
        micombo = new JComboBox<>();
        micombo.addItem("Serif");
        micombo.addItem("SansSerif");
        micombo.addItem("Monospaced");
        micombo.addItem("Dialog");

        micombo.addActionListener(new Evento_combo());
        lamina_norte.add(micombo);
        add(lamina_norte, BorderLayout.NORTH);
    }

    private class Evento_combo implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Actualiza la fuente del texto con la selección del combo
            String fuenteSeleccionada = (String) micombo.getSelectedItem();
            texto.setFont(new Font(fuenteSeleccionada, Font.PLAIN, 18));
        }
    }
}
