package tarea02;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
 
public class ejemploJframe_04_b extends JFrame {
    static JLabel La, Lb, Lc;
    static JButton Bcalcular, Bsalir;
    static JTextField Th, Tb, Ta;
 
    public ejemploJframe_04_b() {
        this.setLayout(null);
        this.setTitle("eduardo alvarez ejemploJframe_04_b");
        this.setBounds(10, 10, 350, 300);
 
        La = new JLabel("Introduzca el valor de la Altura");
        La.setBounds(10, 10, 200, 30);
        Lb = new JLabel("Introduzca el valor de la Base");
        Lb.setBounds(10, 40, 200, 30);
        Lc = new JLabel("El area es igual a -->");
        Lc.setBounds(10, 200, 200, 30);
 
        Th = new JTextField();
        Th.setBounds(220, 10, 100, 30);
        Tb = new JTextField();
        Tb.setBounds(220, 40, 100, 30);
        Ta = new JTextField();
        Ta.setBounds(220, 110, 100, 30);
        Ta.setEditable(false);
 
        Bcalcular = new JButton("Calcular");
        Bcalcular.setBounds(100, 150, 150, 30);
        Bcalcular.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    double altura = Double.parseDouble(Th.getText());
                    double base = Double.parseDouble(Tb.getText());
                    double area = altura * base;
                    Ta.setText(String.valueOf(area));
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Por favor, ingrese números válidos");
                }
            }
        });
 
        Bsalir = new JButton("Salir");
        Bsalir.setBounds(120, 60, 100, 30);
        Bsalir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
 
        this.add(La);
        this.add(Lb);
        this.add(Lc);
        this.add(Bcalcular);
        this.add(Bsalir);
        this.add(Th);
        this.add(Tb);
        this.add(Ta);
    }
 
    public static void main(String parametro[]) {
        ejemploJframe_04_b ventana = new ejemploJframe_04_b();
        ventana.setVisible(true);
    }
}