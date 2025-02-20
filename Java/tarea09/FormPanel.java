package tarea09;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class FormPanel extends JPanel {

	private JLabel nameLabel;
	private JLabel ocupationLabel;
	private JTextField nameField;
	private JTextField ocupationField;
	private JButton okButton;
	
	public FormPanel() {
		
		Dimension dimension = getPreferredSize();
		dimension.width = 250;
		setPreferredSize(dimension);
		
		nameLabel = new JLabel("Name: ");
		ocupationLabel = new JLabel("Ocupation: ");
		nameField = new JTextField(10);
		ocupationField = new JTextField(10);
		
		okButton = new JButton("OK");
		
		TitledBorder innerBorder = BorderFactory.createTitledBorder("Add person.");
		Border outerBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
		
	}
	
}