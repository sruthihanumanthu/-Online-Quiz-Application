import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.Timer;
import javax.swing.border.*;

public class Quiz {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new QuizFrame().setVisible(true);
        });
    }
}

class QuizFrame extends JFrame {
    public QuizFrame() {
        setTitle("Quiz Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        add(new QuizPanel());
    }
}

class QuizPanel extends JPanel {
    private JLabel questionLabel, timerLabel, scoreLabel;
    private JRadioButton[] options;
    private ButtonGroup optionGroup;
    private JButton nextButton, submitButton, hintButton;
    private JPanel centerPanel, southPanel, northPanel;
    private JProgressBar progressBar;
    
    private ArrayList<Question> questions;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private Timer timer;
    private int timeLeft = 30;
    private int questionsAttempted = 0;
    
    private Color bgColor = new Color(240, 248, 255);
    private Color buttonColor = new Color(70, 130, 180);
    private Color textColor = new Color(25, 25, 112);
    
    public QuizPanel() {
        setLayout(new BorderLayout());
        setBackground(bgColor);
        
        initializeQuestions();
        createUI();
        displayQuestion(currentQuestionIndex);
    }
    
    private void createUI() {
        northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(bgColor);
        northPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        timerLabel = new JLabel("Time: " + timeLeft + "s", SwingConstants.LEFT);
        styleLabel(timerLabel, 16, textColor);
        
        scoreLabel = new JLabel("Score: 0", SwingConstants.RIGHT);
        styleLabel(scoreLabel, 16, textColor);
        
        progressBar = new JProgressBar(0, questions.size());
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setForeground(buttonColor);
        progressBar.setBackground(Color.WHITE);
        progressBar.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(bgColor);
        topPanel.add(timerLabel, BorderLayout.WEST);
        topPanel.add(scoreLabel, BorderLayout.EAST);
        
        northPanel.add(topPanel, BorderLayout.NORTH);
        northPanel.add(progressBar, BorderLayout.SOUTH);
        add(northPanel, BorderLayout.NORTH);
        
        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        centerPanel.setBackground(bgColor);
        
        questionLabel = new JLabel();
        styleLabel(questionLabel, 20, textColor);
        questionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        options = new JRadioButton[4];
        optionGroup = new ButtonGroup();
        
        for (int i = 0; i < 4; i++) {
            options[i] = new JRadioButton();
            styleRadioButton(options[i]);
            optionGroup.add(options[i]);
            options[i].addActionListener(e -> enableNextButton());
        }
        
        centerPanel.add(questionLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        for (JRadioButton option : options) {
            centerPanel.add(option);
            centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        add(centerPanel, BorderLayout.CENTER);
        
        southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        southPanel.setBackground(bgColor);
        
        hintButton = new JButton("Hint");
        styleButton(hintButton);
        hintButton.addActionListener(e -> showHint());
        
        nextButton = new JButton("Next");
        styleButton(nextButton);
        nextButton.setEnabled(false);
        nextButton.addActionListener(e -> nextQuestion());
        
        submitButton = new JButton("Submit Quiz");
        styleButton(submitButton);
        submitButton.setEnabled(false);
        submitButton.addActionListener(e -> submitQuiz());
        
        southPanel.add(hintButton);
        southPanel.add(nextButton);
        southPanel.add(submitButton);
        add(southPanel, BorderLayout.SOUTH);
        
        initializeTimer();
    }
    
    private void styleLabel(JLabel label, int size, Color color) {
        label.setFont(new Font("Segoe UI", Font.BOLD, size));
        label.setForeground(color);
    }
    
    private void styleRadioButton(JRadioButton button) {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
    }
    
    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(buttonColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(50, 100, 150)), 
            BorderFactory.createEmptyBorder(8, 20, 8, 20)));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private void initializeQuestions() {
        questions = new ArrayList<>();
        questions.add(new Question(
            "What is the capital of France?",
            new String[]{"London", "Paris", "Berlin", "Madrid"},
            1,
            "It's known as the 'City of Light'"
        ));
        questions.add(new Question(
            "Which planet is known as the Red Planet?",
            new String[]{"Venus", "Mars", "Jupiter", "Saturn"},
            1,
            "It's named after the Roman god of war"
        ));
        questions.add(new Question(
            "What is the largest mammal?",
            new String[]{"Elephant", "Blue Whale", "Giraffe", "Polar Bear"},
            1,
            "It lives in the ocean"
        ));
        questions.add(new Question(
            "In which year did World War II end?",
            new String[]{"1943", "1945", "1947", "1950"},
            1,
            "It ended after the atomic bombs were dropped"
        ));
        questions.add(new Question(
            "Who painted the Mona Lisa?",
            new String[]{"Vincent van Gogh", "Pablo Picasso", "Leonardo da Vinci", "Michelangelo"},
            2,
            "He was an Italian polymath"
        ));
    }
    
    private void initializeTimer() {
        timer = new Timer(1000, e -> {
            timeLeft--;
            timerLabel.setText("Time: " + timeLeft + "s");
            
            if (timeLeft <= 5) {
                timerLabel.setForeground(Color.RED);
            }
            
            if (timeLeft <= 0) {
                timer.stop();
                JOptionPane.showMessageDialog(this, "Time's up! Moving to next question.");
                nextQuestion();
            }
        });
        timer.start();
    }
    
    private void displayQuestion(int index) {
        if (index < questions.size()) {
            Question currentQuestion = questions.get(index);
            questionLabel.setText("<html><div style='width:750px;'>" + 
                (index + 1) + ". " + currentQuestion.getQuestionText() + "</div></html>");
            
            String[] optionsText = currentQuestion.getOptions();
            for (int i = 0; i < 4; i++) {
                options[i].setText(optionsText[i]);
                options[i].setSelected(false);
            }
            
            timeLeft = 30;
            timerLabel.setText("Time: " + timeLeft + "s");
            timerLabel.setForeground(textColor);
            timer.restart();
            
            nextButton.setEnabled(false);
            submitButton.setEnabled(false);
            
            progressBar.setValue(index);
            progressBar.setString(index + "/" + questions.size());
        } else {
            submitQuiz();
        }
    }
    
    private void showHint() {
        if (currentQuestionIndex < questions.size()) {
            Question currentQuestion = questions.get(currentQuestionIndex);
            JOptionPane.showMessageDialog(this, 
                "Hint: " + currentQuestion.getHint(),
                "Question Hint",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void enableNextButton() {
        nextButton.setEnabled(true);
        if (currentQuestionIndex == questions.size() - 1) {
            submitButton.setEnabled(true);
        }
    }
    
    private void nextQuestion() {
        checkAnswer();
        currentQuestionIndex++;
        questionsAttempted++;
        if (currentQuestionIndex < questions.size()) {
            displayQuestion(currentQuestionIndex);
        } else {
            submitQuiz();
        }
    }
    
    private void checkAnswer() {
        if (currentQuestionIndex < questions.size()) {
            Question currentQuestion = questions.get(currentQuestionIndex);
            for (int i = 0; i < 4; i++) {
                if (options[i].isSelected() && i == currentQuestion.getCorrectAnswerIndex()) {
                    score++;
                    scoreLabel.setText("Score: " + score);
                    break;
                }
            }
        }
    }
    
    private void submitQuiz() {
        timer.stop();
        
        // Check answer for the last question if not checked yet
        if (currentQuestionIndex < questions.size()) {
            checkAnswer();
        }
        
        double percentage = (double) score / questions.size() * 100;
        
        String result = "<html><div style='text-align:center;'><h2>Quiz Completed!</h2>" +
            "<p>Your Score: <b>" + score + "</b> out of <b>" + questions.size() + "</b></p>" +
            "<p>Percentage: <b>" + String.format("%.1f", percentage) + "%</b></p>" +
            "<p>Questions Attempted: <b>" + questionsAttempted + "</b></p></div></html>";
        
        JOptionPane.showMessageDialog(this, result, "Quiz Results", JOptionPane.INFORMATION_MESSAGE);
        
        storeQuizHistory(score, percentage);
        
        int choice = JOptionPane.showConfirmDialog(this, 
            "Would you like to take the quiz again?", "Quiz Over", JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            currentQuestionIndex = 0;
            score = 0;
            questionsAttempted = 0;
            timeLeft = 30;
            scoreLabel.setText("Score: 0");
            displayQuestion(currentQuestionIndex);
            timer.start();
        } else {
            System.exit(0);
        }
    }
    
    private void storeQuizHistory(int score, double percentage) {
        try (FileWriter fw = new FileWriter("quiz_history.txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            
            out.println("Date: " + new Date() + 
                       " | Score: " + score + "/" + questions.size() +
                       " (" + String.format("%.1f", percentage) + "%)" +
                       " | Attempted: " + questionsAttempted + "/" + questions.size());
        } catch (IOException e) {
            System.err.println("Error saving quiz history: " + e.getMessage());
        }
    }
}

class Question {
    private String questionText;
    private String[] options;
    private int correctAnswerIndex;
    private String hint;
    
    public Question(String questionText, String[] options, int correctAnswerIndex, String hint) {
        this.questionText = questionText;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
        this.hint = hint;
    }
    
    public String getQuestionText() {
        return questionText;
    }
    
    public String[] getOptions() {
        return options;
    }
    
    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }
    
    public String getHint() {
        return hint;
    }
}