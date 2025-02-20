package tarea12;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

public class LoanAssistant extends JFrame {

    private JLabel balanceLabel, interestLabel, monthsLabel, paymentLabel, analysisLabel;
    private JTextField balanceTextField, interestTextField, monthsTextField, paymentTextField;
    private JTextArea analysisTextArea;
    private JButton computeButton, newLoanButton, monthsButton, paymentButton, exitButton;
    private boolean computePayment;
    private Color lightYellow = new Color(255, 255, 128);

    public static void main(String args[]) {
        new LoanAssistant().setVisible(true);
    }

    public LoanAssistant() {
        setTitle("Asistente de Préstamos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints gridConstraints;
        Font myFont = new Font("Arial", Font.PLAIN, 16);

        balanceLabel = new JLabel("Saldo del Préstamo:");
        balanceTextField = new JTextField();
        interestLabel = new JLabel("Tasa de Interés:");
        interestTextField = new JTextField();
        monthsLabel = new JLabel("Número de Pagos:");
        monthsTextField = new JTextField();
        paymentLabel = new JLabel("Pago Mensual:");
        paymentTextField = new JTextField();
        analysisLabel = new JLabel("Análisis del Préstamo:");
        analysisTextArea = new JTextArea(10, 30);
        computeButton = new JButton("Calcular Pago Mensual");
        newLoanButton = new JButton("Nuevo Análisis de Préstamo");
        monthsButton = new JButton("X");
        paymentButton = new JButton("X");
        exitButton = new JButton("Salir");

        analysisTextArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        analysisTextArea.setFont(new Font("Courier New", Font.PLAIN, 14));
        analysisTextArea.setEditable(false);
        analysisTextArea.setBackground(Color.WHITE);

        balanceTextField.setPreferredSize(new Dimension(120, 30));
        interestTextField.setPreferredSize(new Dimension(120, 30));
        monthsTextField.setPreferredSize(new Dimension(120, 30));
        paymentTextField.setPreferredSize(new Dimension(120, 30));

        gridConstraints = new GridBagConstraints();
        gridConstraints.gridx = 0;
        gridConstraints.gridy = 0;
        gridConstraints.anchor = GridBagConstraints.WEST;
        gridConstraints.insets = new Insets(10, 10, 0, 0);
        getContentPane().add(balanceLabel, gridConstraints);

        gridConstraints = new GridBagConstraints();
        gridConstraints.gridx = 1;
        gridConstraints.gridy = 0;
        gridConstraints.insets = new Insets(10, 10, 0, 10);
        getContentPane().add(balanceTextField, gridConstraints);

        gridConstraints = new GridBagConstraints();
        gridConstraints.gridx = 0;
        gridConstraints.gridy = 1;
        gridConstraints.anchor = GridBagConstraints.WEST;
        getContentPane().add(interestLabel, gridConstraints);

        gridConstraints = new GridBagConstraints();
        gridConstraints.gridx = 1;
        gridConstraints.gridy = 1;
        gridConstraints.insets = new Insets(10, 10, 0, 10);
        getContentPane().add(interestTextField, gridConstraints);

        gridConstraints = new GridBagConstraints();
        gridConstraints.gridx = 0;
        gridConstraints.gridy = 2;
        gridConstraints.anchor = GridBagConstraints.WEST;
        getContentPane().add(monthsLabel, gridConstraints);

        gridConstraints = new GridBagConstraints();
        gridConstraints.gridx = 1;
        gridConstraints.gridy = 2;
        gridConstraints.insets = new Insets(10, 10, 0, 10);
        getContentPane().add(monthsTextField, gridConstraints);

        gridConstraints = new GridBagConstraints();
        gridConstraints.gridx = 0;
        gridConstraints.gridy = 3;
        gridConstraints.anchor = GridBagConstraints.WEST;
        getContentPane().add(paymentLabel, gridConstraints);

        gridConstraints = new GridBagConstraints();
        gridConstraints.gridx = 1;
        gridConstraints.gridy = 3;
        gridConstraints.insets = new Insets(10, 10, 0, 10);
        getContentPane().add(paymentTextField, gridConstraints);

        gridConstraints = new GridBagConstraints();
        gridConstraints.gridx = 0;
        gridConstraints.gridy = 4;
        gridConstraints.gridwidth = 2;
        gridConstraints.insets = new Insets(10, 0, 0, 0);
        getContentPane().add(computeButton, gridConstraints);

        gridConstraints = new GridBagConstraints();
        gridConstraints.gridx = 0;
        gridConstraints.gridy = 5;
        gridConstraints.gridwidth = 2;
        getContentPane().add(newLoanButton, gridConstraints);
        newLoanButton.setEnabled(false);

        gridConstraints = new GridBagConstraints();
        gridConstraints.gridx = 2;
        gridConstraints.gridy = 2;
        getContentPane().add(monthsButton, gridConstraints);
        monthsButton.setVisible(false);

        gridConstraints = new GridBagConstraints();
        gridConstraints.gridx = 2;
        gridConstraints.gridy = 3;
        getContentPane().add(paymentButton, gridConstraints);
        paymentButton.setVisible(false);

        gridConstraints = new GridBagConstraints();
        gridConstraints.gridx = 3;
        gridConstraints.gridy = 0;
        gridConstraints.anchor = GridBagConstraints.WEST;
        getContentPane().add(analysisLabel, gridConstraints);

        gridConstraints = new GridBagConstraints();
        gridConstraints.gridx = 3;
        gridConstraints.gridy = 1;
        gridConstraints.gridheight = 4;
        gridConstraints.fill = GridBagConstraints.BOTH;
        JScrollPane scrollPane = new JScrollPane(analysisTextArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        getContentPane().add(scrollPane, gridConstraints);

        gridConstraints = new GridBagConstraints();
        gridConstraints.gridx = 3;
        gridConstraints.gridy = 5;
        getContentPane().add(exitButton, gridConstraints);

        computeButton.addActionListener(this::computeButtonActionPerformed);
        newLoanButton.addActionListener(this::newLoanButtonActionPerformed);
        exitButton.addActionListener(e -> System.exit(0));
        monthsButton.addActionListener(this::monthsButtonActionPerformed);
        paymentButton.addActionListener(this::paymentButtonActionPerformed);

        paymentButton.doClick(); 
        pack();
        setMinimumSize(new Dimension(600, 400));
        setLocationRelativeTo(null);
    }

    private void computeButtonActionPerformed(ActionEvent e) {
        try {
            double balance, interest, payment;
            int months;
            double monthlyInterest;

            if (!validateDecimalNumber(balanceTextField)) return;
            balance = Double.parseDouble(balanceTextField.getText());

            if (!validateDecimalNumber(interestTextField)) return;
            interest = Double.parseDouble(interestTextField.getText());
            monthlyInterest = interest / 1200;

            if (computePayment) {
                if (!validateDecimalNumber(monthsTextField)) return;
                months = Integer.parseInt(monthsTextField.getText());

                if (interest == 0) {
                    payment = balance / months;
                } else {
                    double multiplier = Math.pow(1 + monthlyInterest, months);
                    payment = balance * monthlyInterest * multiplier / (multiplier - 1);
                }

                paymentTextField.setText(new DecimalFormat("0.00").format(payment));
            } else {
                if (!validateDecimalNumber(paymentTextField)) return;
                payment = Double.parseDouble(paymentTextField.getText());

                if (interest == 0) {
                    months = (int) (balance / payment);
                } else {
                    months = (int) ((Math.log(payment) - Math.log(payment - balance * monthlyInterest)) / Math.log(1 + monthlyInterest));
                }

                monthsTextField.setText(String.valueOf(months));
            }

            double loanBalance = balance;
            double finalPayment;
            for (int paymentNumber = 1; paymentNumber <= months - 1; paymentNumber++) {
                loanBalance += loanBalance * monthlyInterest - payment;
            }

            finalPayment = loanBalance;
            if (finalPayment > payment) {
                loanBalance += loanBalance * monthlyInterest - payment;
                finalPayment = loanBalance;
                months++;
                monthsTextField.setText(String.valueOf(months));
            }

            analysisTextArea.setText("Saldo del Préstamo: $" + new DecimalFormat("0.00").format(balance));
            analysisTextArea.append("\nTasa de Interés: " + new DecimalFormat("0.00").format(interest) + "%");
            analysisTextArea.append("\n\n" + (months - 1) + " Pagos de $" + new DecimalFormat("0.00").format(payment));
            analysisTextArea.append("\nPago Final de: $" + new DecimalFormat("0.00").format(finalPayment));
            analysisTextArea.append("\nPagos Totales: $" + new DecimalFormat("0.00").format((months - 1) * payment + finalPayment));
            analysisTextArea.append("\nInterés Pagado: $" + new DecimalFormat("0.00").format((months - 1) * payment + finalPayment - balance));
            computeButton.setEnabled(false);
            newLoanButton.setEnabled(true);
            newLoanButton.requestFocus();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Entrada no válida. Por favor, introduzca valores numéricos.");
        }
    }

    private void newLoanButtonActionPerformed(ActionEvent e) {
        if (computePayment) {
            paymentTextField.setText("");
        } else {
            monthsTextField.setText("");
        }
        analysisTextArea.setText("");
        computeButton.setEnabled(true);
        newLoanButton.setEnabled(false);
        balanceTextField.requestFocus();
    }

    private void monthsButtonActionPerformed(ActionEvent e) {
        computePayment = false;
        paymentButton.setVisible(true);
        monthsButton.setVisible(false);
        monthsTextField.setEditable(false);
        monthsTextField.setBackground(lightYellow);
        paymentTextField.setEditable(true);
        paymentTextField.setBackground(Color.WHITE);
        computeButton.setText("Calcular Número de Pagos");
        balanceTextField.requestFocus();
    }

    private void paymentButtonActionPerformed(ActionEvent e) {
        computePayment = true;
        paymentButton.setVisible(false);
        monthsButton.setVisible(true);
        monthsTextField.setEditable(true);
        monthsTextField.setBackground(Color.WHITE);
        paymentTextField.setEditable(false);
        paymentTextField.setBackground(lightYellow);
        computeButton.setText("Calcular Pago Mensual");
        balanceTextField.requestFocus();
    }

    private boolean validateDecimalNumber(JTextField tf) {
        String s = tf.getText().trim();
        boolean hasDecimal = false;
        boolean valid = true;
        if (s.length() == 0) {
            valid = false;
        } else {
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (c >= '0' && c <= '9') {
                    continue;
                } else if (c == '.' && !hasDecimal) {
                    hasDecimal = true;
                } else {
                    valid = false;
                }
            }
        }
        tf.setText(s);
        if (!valid) {
            tf.requestFocus();
        }
        return valid;
    }
}
