package tarea02;

import javax.swing.*;
import java.awt.event.*;
 
public class ejemploJframe_04_a extends JFrame implements ActionListener {
    // Declaración de componentes
    static JLabel La, Lb, Lc;
    static JButton Bcalcular, Bsalir;
    static JTextField Th, Tb, Ta;
 
    public ejemploJframe_04_a() {
        // Configuración de la ventana
        setLayout(null);
        setTitle("Eduardo Alvarez ejemploJframe_04_b");
 
        // Creación y configuración de etiquetas y campos de texto
        La = new JLabel("Introduce el valor de la Altura");
        La.setBounds(10, 10, 200, 30);
        Lb = new JLabel("Introduce el valor de la Base");
        Lb.setBounds(10, 40, 200, 30);
        Lc = new JLabel("El area es igual a -->");
        Lc.setBounds(10, 110, 200, 30);
 
        Th = new JTextField();
        Th.setBounds(220, 18, 100, 30);
        Tb = new JTextField();
        Tb.setBounds(220, 48, 100, 30);
        Ta = new JTextField();
        Ta.setBounds(220, 110, 100, 30);
        Ta.setEditable(false);
 
        // Creación y configuración de botones
        Bcalcular = new JButton("Calcular");
        Bcalcular.setBounds(10, 60, 100, 30);
        Bcalcular.addActionListener(this);
 
        Bsalir = new JButton("Salir");
        Bsalir.setBounds(120, 60, 100, 30);
        Bsalir.addActionListener(this);
 
        // Agregar componentes a la ventana
        add(La);
        add(Lb);
        add(Lc);
        add(Bcalcular);
        add(Bsalir);
        add(Th);
        add(Tb);
        add(Ta);
    }
 
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == Bcalcular) {
            try {
                // Obtener los valores de los campos de texto
                double altura = Double.parseDouble(Th.getText());
                double base = Double.parseDouble(Tb.getText());
 
                // Calcular el área
                double area = (base * altura) / 2;
 
                // Mostrar el resultado
                Ta.setText(Double.toString(area));
            } catch (NumberFormatException ex) {
                // Manejar el error si los valores no son números
                JOptionPane.showMessageDialog(this, "Por favor, ingresa valores numéricos válidos.");
            }
        } else if (e.getSource() == Bsalir) {
            System.exit(0);
        }
    }
 
    public static void main(String[] args) {
        ejemploJframe_04_a ventana = new ejemploJframe_04_a();
        ventana.setVisible(true);
    }
}