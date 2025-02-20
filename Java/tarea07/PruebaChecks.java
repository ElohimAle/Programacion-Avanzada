package tarea07;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PruebaChecks {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Marcocheck minarco = new Marcocheck();
            minarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        });
    }
}

class Marcocheck extends JFrame {
    private Laminacheck milamina;

    public Marcocheck() {
        setTitle("Selector de Estilos");
        setBounds(559, 360, 556, 356);
        milamina = new Laminacheck();
        add(milamina);
        setVisible(true);
    }
}

class Laminacheck extends JPanel {
    private JLabel texto;
    private JCheckBox check1, check2;

    public Laminacheck() {
        setLayout(new BorderLayout());
        Font miletra = new Font("Serif", Font.PLAIN, 24); // Fuente base

        // Texto central
        texto = new JLabel("En un lugar de la mancha de cuyo nombre......");
        texto.setFont(miletra);
        
        JPanel laminatexto = new JPanel();
        laminatexto.add(texto);
        add(laminatexto, BorderLayout.CENTER);

        // Checkboxes para estilos
        check1 = new JCheckBox("Negrita");
        check2 = new JCheckBox("Cursiva");
        
        check1.addActionListener(new ManejaChecks());
        check2.addActionListener(new ManejaChecks());

        JPanel laminachecks = new JPanel();
        laminachecks.add(check1);
        laminachecks.add(check2);
        add(laminachecks, BorderLayout.SOUTH);
    }

    private class ManejaChecks implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int tipo = Font.PLAIN; // Estilo base
            
            if (check1.isSelected()) tipo += Font.BOLD;
            if (check2.isSelected()) tipo += Font.ITALIC;
            
            texto.setFont(new Font(texto.getFont().getName(), tipo, 24)); // Actualizar fuente
        }
    }
}