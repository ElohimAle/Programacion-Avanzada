package tarea02;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
 
public class ejemploJframe_03_d extends JFrame {
    static JLabel La, Lb;
    static JButton Bboton;
 
    public ejemploJframe_03_d() {
        this.setLayout(null);
        this.setBounds(10, 10, 300, 300);
 
        
        La = new JLabel("El triángulo de base 5 y de altura 2");
        La.setBounds(10, 10, 200, 30);
        this.add(La);
 
        
        Lb = new JLabel();
        Lb.setBounds(10, 100, 200, 30);
        this.add(Lb);
 
        
        Bboton = new JButton("Calcular");
        Bboton.setBounds(10, 50, 100, 30);
        Bboton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Lb.setText("El área del triángulo es 5");
            }
        });
        this.add(Bboton);
    }
 
    public static void main(String parametro[]) {
        ejemploJframe_03_d ventana = new ejemploJframe_03_d();
        ventana.setVisible(true);
    }
}