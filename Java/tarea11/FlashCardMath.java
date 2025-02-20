package tarea11;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class FlashCardMath extends JFrame {
    private JLabel problemLabel, triedLabel, correctLabel, timerLabel;
    private JTextField triedTextField, correctTextField, answerTextField, customTimeField;
    private JButton startButton, exitButton, submitButton;
    private JCheckBox additionCheck, subtractionCheck, multiplicationCheck, divisionCheck;
    private JRadioButton randomFactor, factor1, factor2, factor3, factor4;
    private ButtonGroup factorGroup, timerGroup;
    private JRadioButton timerOff, timerUp, timerDown;
    private Random random;
    private int numberTried, numberCorrect, correctAnswer, timeLeft;
    private Timer timer;
    
    public FlashCardMath() {
        setTitle("Flash Card Math");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        random = new Random();
        
        JPanel topPanel = new JPanel();
        triedLabel = new JLabel("Tried:");
        correctLabel = new JLabel("Correct:");
        timerLabel = new JLabel("Time Left: 30s");
        triedTextField = new JTextField(5);
        correctTextField = new JTextField(5);
        triedTextField.setEditable(false);
        correctTextField.setEditable(false);
        topPanel.add(triedLabel);
        topPanel.add(triedTextField);
        topPanel.add(correctLabel);
        topPanel.add(correctTextField);
        topPanel.add(timerLabel);
        
        problemLabel = new JLabel("Click Start", SwingConstants.CENTER);
        problemLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        JPanel middlePanel = new JPanel();
        answerTextField = new JTextField(5);
        submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> checkAnswer());
        middlePanel.add(problemLabel);
        middlePanel.add(answerTextField);
        middlePanel.add(submitButton);
        
        JPanel settingsPanel = new JPanel(new GridLayout(3, 1));
        JPanel typePanel = new JPanel(new GridLayout(4, 1));
        additionCheck = new JCheckBox("Addition", true);
        subtractionCheck = new JCheckBox("Subtraction", true);
        multiplicationCheck = new JCheckBox("Multiplication", true);
        divisionCheck = new JCheckBox("Division", true);
        typePanel.setBorder(BorderFactory.createTitledBorder("Type"));
        typePanel.add(additionCheck);
        typePanel.add(subtractionCheck);
        typePanel.add(multiplicationCheck);
        typePanel.add(divisionCheck);
        
        JPanel factorPanel = new JPanel(new GridLayout(5, 1));
        randomFactor = new JRadioButton("Random", true);
        factor1 = new JRadioButton("1-2");
        factor2 = new JRadioButton("3-4");
        factor3 = new JRadioButton("5-6");
        factor4 = new JRadioButton("7-8");
        factorGroup = new ButtonGroup();
        factorGroup.add(randomFactor);
        factorGroup.add(factor1);
        factorGroup.add(factor2);
        factorGroup.add(factor3);
        factorGroup.add(factor4);
        factorPanel.setBorder(BorderFactory.createTitledBorder("Factor"));
        factorPanel.add(randomFactor);
        factorPanel.add(factor1);
        factorPanel.add(factor2);
        factorPanel.add(factor3);
        factorPanel.add(factor4);
        
        JPanel timerPanel = new JPanel(new GridLayout(4, 1));
        timerOff = new JRadioButton("Off", true);
        timerUp = new JRadioButton("On-Count Up");
        timerDown = new JRadioButton("On-Count Down");
        customTimeField = new JTextField("30", 5);
        timerGroup = new ButtonGroup();
        timerGroup.add(timerOff);
        timerGroup.add(timerUp);
        timerGroup.add(timerDown);
        timerPanel.setBorder(BorderFactory.createTitledBorder("Timer"));
        timerPanel.add(timerOff);
        timerPanel.add(timerUp);
        timerPanel.add(timerDown);
        timerPanel.add(customTimeField);
        
        settingsPanel.add(typePanel);
        settingsPanel.add(factorPanel);
        settingsPanel.add(timerPanel);
        
        JPanel bottomPanel = new JPanel();
        startButton = new JButton("Start Practice");
        exitButton = new JButton("Exit");
        startButton.addActionListener(e -> startPractice());
        exitButton.addActionListener(e -> System.exit(0));
        bottomPanel.add(startButton);
        bottomPanel.add(exitButton);
        
        add(topPanel, BorderLayout.NORTH);
        add(middlePanel, BorderLayout.CENTER);
        add(settingsPanel, BorderLayout.WEST);
        add(bottomPanel, BorderLayout.SOUTH);
        
        setVisible(true);
    }
    
    private void startPractice() {
        numberTried = 0;
        numberCorrect = 0;
        triedTextField.setText("0");
        correctTextField.setText("0");
        answerTextField.setText("");
        answerTextField.setEnabled(true);
        submitButton.setEnabled(true);
        
        if (!timerOff.isSelected()) {
            timeLeft = Integer.parseInt(customTimeField.getText());
            timerLabel.setText("Time Left: " + timeLeft + "s");
            timer = new Timer(1000, e -> {
                timeLeft--;
                timerLabel.setText("Time Left: " + timeLeft + "s");
                if (timeLeft <= 0) {
                    timer.stop();
                    JOptionPane.showMessageDialog(this, "Time's up! Your score: " + numberCorrect + "/" + numberTried, "Game Over", JOptionPane.INFORMATION_MESSAGE);
                    resetGame();
                }
            });
            timer.start();
        }
        generateProblem();
    }
    private void resetGame() {
        problemLabel.setText("Click Start");
        answerTextField.setText("");
        answerTextField.setEnabled(false);
        submitButton.setEnabled(false);
    }
    
    private void generateProblem() {
        int num1 = random.nextInt(10) + 1;
        int num2 = random.nextInt(10) + 1;
        char operator = '+';
        
        if (additionCheck.isSelected()) {
            operator = '+';
            correctAnswer = num1 + num2;
        } else if (subtractionCheck.isSelected()) {
            operator = '-';
            correctAnswer = num1 - num2;
        } else if (multiplicationCheck.isSelected()) {
            operator = '*';
            correctAnswer = num1 * num2;
        } else if (divisionCheck.isSelected()) {
            operator = '/';
            correctAnswer = num1 / num2;
        }
        
        problemLabel.setText(num1 + " " + operator + " " + num2);
    }
    
    private void checkAnswer() {
        try {
            int userAnswer = Integer.parseInt(answerTextField.getText());
            numberTried++;
            if (userAnswer == correctAnswer) {
                numberCorrect++;
            }
            triedTextField.setText(String.valueOf(numberTried));
            correctTextField.setText(String.valueOf(numberCorrect));
            answerTextField.setText("");
            generateProblem();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
        new FlashCardMath();
    }
}
