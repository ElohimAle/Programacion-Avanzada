package tarea02;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
 
public class ejemploJframe_03_c extends JFrame implements ActionListener {
    static JLabel La, Lb;
    static JButton Bboton;
 
    public ejemploJframe_03_c() {
        this.setLayout(null);
        this.setBounds(10, 10, 300, 300);
 
        
        La = new JLabel("El triángulo de base 5 y de altura 2");
        La.setBounds(10, 10, 200, 30);
        this.add(La);
 
        
        Lb = new JLabel();
        Lb.setBounds(10, 100, 200, 30);
        this.add(Lb);
 
        
        Bboton = new JButton();
        Bboton.setText("Calcular");
        Bboton.setBounds(10, 50, 100, 30);
        Bboton.addActionListener(this);
        this.add(Bboton);
    }
 
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == Bboton) {
            Lb.setText("El área del triángulo es 5");
        }
    }
 
    public static void main(String parametro[]) {
        ejemploJframe_03_c ventana = new ejemploJframe_03_c();
        ventana.setVisible(true);
    }
}
