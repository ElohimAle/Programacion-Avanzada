package tarea02;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
 
public class ejemploJframe_03_a {
 
	    public static void main(String[] args) {
	        
	        JFrame frame = new JFrame("Interfaz Gráfica - Java");
	        frame.setSize(400, 300);
	        frame.setLayout(null);
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
	        
	        JLabel label = new JLabel("Presiona el botón:");
	        label.setBounds(50, 30, 150, 30);
	        frame.add(label);
 
	       
	        JButton button = new JButton("Calcular");
	        button.setBounds(50, 80, 100, 30);
	        frame.add(button);
 
	        
	        frame.setVisible(true);
	    }
	}