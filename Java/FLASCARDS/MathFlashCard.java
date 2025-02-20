package FLASCARDS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class MathFlashCard extends JFrame {
    private JLabel lblTried, lblCorrect, lblQuestion, lblTimer;
    private JTextField txtAnswer;
    private JButton btnStopPractice, btnExit;
    private JCheckBox chkAddition, chkSubtraction, chkMultiplication, chkDivision;
    private JRadioButton rbtnRandom, rbtn1, rbtn2, rbtn3, rbtn7, rbtn9;
    private JRadioButton rbtnTimerOff, rbtnTimerUp, rbtnTimerDown;
    private ButtonGroup groupFactor, groupTimer;
    private Timer timer;
    private int tried = 0, correct = 0, timeLeft = 60;
    private Random random = new Random();
    private int num1, num2, answer;

    public MathFlashCard() {
        setTitle("Flash Card Math");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel superior con intentos y correctos
        JPanel panelTop = new JPanel();
        panelTop.setLayout(new GridLayout(1, 3));

        lblTried = new JLabel("Tried: 0", SwingConstants.CENTER);
        lblCorrect = new JLabel("Correct: 0", SwingConstants.CENTER);
        lblTimer = new JLabel("0:60", SwingConstants.CENTER);

        lblTried.setOpaque(true);
        lblCorrect.setOpaque(true);
        lblTimer.setOpaque(true);
        lblTried.setBackground(Color.RED);
        lblCorrect.setBackground(Color.RED);
        lblTimer.setBackground(Color.RED);
        lblTried.setForeground(Color.WHITE);
        lblCorrect.setForeground(Color.WHITE);
        lblTimer.setForeground(Color.WHITE);

        panelTop.add(lblTried);
        panelTop.add(lblCorrect);
        panelTop.add(lblTimer);
        add(panelTop, BorderLayout.NORTH);

        // Panel de pregunta
        lblQuestion = new JLabel("7 + 2 = ?", SwingConstants.CENTER);
        lblQuestion.setFont(new Font("Arial", Font.BOLD, 28));
        lblQuestion.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(lblQuestion, BorderLayout.CENTER);

        // Panel de configuración
        JPanel panelConfig = new JPanel(new GridLayout(1, 3));

        JPanel panelType = new JPanel(new GridLayout(4, 1));
        panelType.setBorder(BorderFactory.createTitledBorder("Type:"));
        chkAddition = new JCheckBox("Addition", true);
        chkSubtraction = new JCheckBox("Subtraction");
        chkMultiplication = new JCheckBox("Multiplication");
        chkDivision = new JCheckBox("Division");
        panelType.add(chkAddition);
        panelType.add(chkSubtraction);
        panelType.add(chkMultiplication);
        panelType.add(chkDivision);
        panelConfig.add(panelType);

        JPanel panelFactor = new JPanel(new GridLayout(3, 2));
        panelFactor.setBorder(BorderFactory.createTitledBorder("Factor:"));
        rbtnRandom = new JRadioButton("Random", true);
        rbtn1 = new JRadioButton("1");
        rbtn2 = new JRadioButton("2");
        rbtn3 = new JRadioButton("3");
        rbtn7 = new JRadioButton("7");
        rbtn9 = new JRadioButton("9");
        groupFactor = new ButtonGroup();
        groupFactor.add(rbtnRandom);
        groupFactor.add(rbtn1);
        groupFactor.add(rbtn2);
        groupFactor.add(rbtn3);
        groupFactor.add(rbtn7);
        groupFactor.add(rbtn9);
        panelFactor.add(rbtnRandom);
        panelFactor.add(rbtn1);
        panelFactor.add(rbtn2);
        panelFactor.add(rbtn3);
        panelFactor.add(rbtn7);
        panelFactor.add(rbtn9);
        panelConfig.add(panelFactor);

        JPanel panelTimer = new JPanel(new GridLayout(3, 1));
        panelTimer.setBorder(BorderFactory.createTitledBorder("Timer:"));
        rbtnTimerOff = new JRadioButton("Off", true);
        rbtnTimerUp = new JRadioButton("On-Count Up");
        rbtnTimerDown = new JRadioButton("On-Count Down");
        groupTimer = new ButtonGroup();
        groupTimer.add(rbtnTimerOff);
        groupTimer.add(rbtnTimerUp);
        groupTimer.add(rbtnTimerDown);
        panelTimer.add(rbtnTimerOff);
        panelTimer.add(rbtnTimerUp);
        panelTimer.add(rbtnTimerDown);
        panelConfig.add(panelTimer);

        add(panelConfig, BorderLayout.WEST);

        // Panel inferior con botones
        JPanel panelBottom = new JPanel();
        btnStopPractice = new JButton("Stop Practice");
        btnExit = new JButton("Exit");
        txtAnswer = new JTextField(5);
        panelBottom.add(btnStopPractice);
        panelBottom.add(btnExit);
        panelBottom.add(txtAnswer);
        add(panelBottom, BorderLayout.SOUTH);

        btnExit.addActionListener(e -> System.exit(0));
        txtAnswer.addActionListener(e -> checkAnswer());

        timer = new Timer(1000, e -> updateTimer());
    }

    private void updateTimer() {
        if (timeLeft > 0) {
            timeLeft--;
            lblTimer.setText("0:" + timeLeft);
        } else {
            timer.stop();
        }
    }

    private void checkAnswer() {
        tried++;
        int userAnswer = Integer.parseInt(txtAnswer.getText());
        if (userAnswer == answer) {
            correct++;
        }
        lblTried.setText("Tried: " + tried);
        lblCorrect.setText("Correct: " + correct);
        txtAnswer.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MathFlashCard().setVisible(true));
    }
}
