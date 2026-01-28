package Java;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

public class PrelimGradeCalculator extends JFrame {
    private JTextField attendanceField;
    private JTextField lab1Field;
    private JTextField lab2Field;
    private JTextField lab3Field;
    private JTextArea resultArea;
    
    public PrelimGradeCalculator() {
        setTitle("🌌 PRELIM GRADE CALCULATOR 🌠");
        setSize(550, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Set dark theme colors
        Color bgColor = new Color(20, 20, 30);
        Color cyanColor = new Color(0, 255, 255);
        Color purpleColor = new Color(50, 0, 80);
        Color grayBg = new Color(128, 128, 128);
        
        getContentPane().setBackground(bgColor);
        
        // Outer border panel (fixed size, doesn't expand)
        JPanel outerBorder = new JPanel(new BorderLayout());
        outerBorder.setBackground(bgColor);
        outerBorder.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(20, 20, 20, 20),
            BorderFactory.createLineBorder(cyanColor, 4)
        ));
        
        // Main panel (this expands with window)
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(bgColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("🌌 PRELIM GRADE CALCULATOR 🌠");
        titleLabel.setForeground(cyanColor);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(cyanColor, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Input fields
        attendanceField = createInputField(5);
        lab1Field = createInputField(100);
        lab2Field = createInputField(100);
        lab3Field = createInputField(100);
        
        mainPanel.add(createFieldPanel("Attendance: (0-5)", attendanceField, cyanColor, bgColor, grayBg));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(createFieldPanel("Lab Work 1: (0-100)", lab1Field, cyanColor, bgColor, grayBg));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(createFieldPanel("Lab Work 2: (0-100)", lab2Field, cyanColor, bgColor, grayBg));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(createFieldPanel("Lab Work 3: (0-100)", lab3Field, cyanColor, bgColor, grayBg));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(bgColor);
        
        JButton computeBtn = createStyledButton("CALCULATE", cyanColor, purpleColor);
        JButton clearBtn = createStyledButton("CLEAR ALL", cyanColor, purpleColor);
        
        computeBtn.addActionListener(e -> computeGrades());
        clearBtn.addActionListener(e -> clearAll());
        
        buttonPanel.add(computeBtn);
        buttonPanel.add(clearBtn);
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Result area
        resultArea = new JTextArea(15, 30);
        resultArea.setEditable(false);
        resultArea.setBackground(grayBg);
        resultArea.setForeground(Color.BLACK);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(cyanColor, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setPreferredSize(new Dimension(400, 250));
        mainPanel.add(scrollPane);
        
        outerBorder.add(mainPanel, BorderLayout.CENTER);
        add(outerBorder);
    }
    
    private JTextField createInputField(int max) {
        JTextField field = new JTextField(10);
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setBackground(new Color(128, 128, 128));
        field.setForeground(Color.BLACK);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 255, 255), 2),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        // Add document filter to limit input
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new NumberFilter(max));
        
        return field;
    }
    
    private JPanel createFieldPanel(String labelText, JTextField field, Color cyan, Color bg, Color grayBg) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(bg);
        panel.setMaximumSize(new Dimension(450, 35));
        
        JLabel label = new JLabel(labelText);
        label.setForeground(cyan);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        
        panel.add(label, BorderLayout.WEST);
        panel.add(field, BorderLayout.EAST);
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color cyan, Color purple) {
        JButton button = new JButton(text);
        button.setBackground(purple);
        button.setForeground(new Color(20, 20, 30));
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(cyan, 2),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(70, 0, 100));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(purple);
            }
        });
        
        return button;
    }
    
    private void computeGrades() {
        try {
            int attendanceRaw = Integer.parseInt(attendanceField.getText().trim());
            int lab1 = Integer.parseInt(lab1Field.getText().trim());
            int lab2 = Integer.parseInt(lab2Field.getText().trim());
            int lab3 = Integer.parseInt(lab3Field.getText().trim());
            
            // Clamp values
            attendanceRaw = Math.max(0, Math.min(5, attendanceRaw));
            
            double attendancePercent = attendanceRaw * 20.0;
            double labAverage = (lab1 + lab2 + lab3) / 3.0;
            double classStanding = (attendancePercent * 0.4) + (labAverage * 0.6);
            
            StringBuilder output = new StringBuilder();
            output.append("--- RESULTS ---\n\n");
            output.append(String.format("Attendance: %d / 5\n", attendanceRaw));
            output.append(String.format("Attendance Percentage: %.0f%%\n\n", attendancePercent));
            output.append(String.format("Lab Average: %.2f\n", labAverage));
            output.append(String.format("Class Standing: %.2f\n\n", classStanding));
            
            // Attendance logic
            if (attendanceRaw == 1) {
                output.append("⚠ FAILED DUE TO LOW ATTENDANCE (Only 1 day attended)\n\n");
                output.append("Remark: FAILED\n\n");
                output.append("Required Prelim Exam for 75: FAILED DUE TO LOW ATTENDANCE\n\n");
                output.append("Required Prelim Exam for 100: FAILED DUE TO LOW ATTENDANCE");
                resultArea.setText(output.toString());
                return;
            } else if (attendanceRaw == 2 || attendanceRaw == 3) {
                output.append("⚠ WARNING: Attendance is low. High chance to fail.\n\n");
            }
            
            // Lab warning
            if (labAverage < 75) {
                output.append("⚠ WARNING: Your lab work average is low. You still have a chance to fail due to poor lab performance.\n\n");
            }
            
            // Required Prelim Exam
            double required75 = (75 - classStanding * 0.7) / 0.3;
            double required100 = (100 - classStanding * 0.7) / 0.3;
            
            String required75Display;
            if (required75 <= 0) {
                required75Display = "0% – Already Passed";
            } else if (required75 < 75) {
                required75Display = String.format("%.2f%% – Already Passed the 75 - Just do your best", required75);
            } else if (required75 > 100) {
                required75Display = "Already Failed";
            } else {
                required75Display = String.format("%.2f%%", required75);
            }
            
            String required100Display;
            if (required100 <= 0) {
                required100Display = "0% – Already Passed";
            } else if (required100 > 100) {
                required100Display = "Not Achievable";
            } else {
                required100Display = String.format("%.2f%%", required100);
            }
            
            String remark = classStanding >= 75 ? "PASSED" : "FAILED";
            
            output.append(String.format("Remark: %s\n\n", remark));
            output.append(String.format("Required Prelim Exam for 75: %s\n\n", required75Display));
            output.append(String.format("Required Prelim Exam for 100: %s", required100Display));
            
            resultArea.setText(output.toString());
            
        } catch (NumberFormatException e) {
            resultArea.setText("⚠ Please enter all valid numbers!");
        }
    }
    
    private void clearAll() {
        attendanceField.setText("");
        lab1Field.setText("");
        lab2Field.setText("");
        lab3Field.setText("");
        resultArea.setText("");
    }
    
    // Document filter to restrict input to integers within range
    private class NumberFilter extends DocumentFilter {
        private int max;
        
        public NumberFilter(int max) {
            this.max = max;
        }
        
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null) return;
            if (isValid(fb, offset, string, 0)) {
                super.insertString(fb, offset, string, attr);
            }
        }
        
        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null) return;
            if (isValid(fb, offset, text, length)) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
        
        private boolean isValid(FilterBypass fb, int offset, String text, int length) throws BadLocationException {
            String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
            String newText = currentText.substring(0, offset) + text + currentText.substring(offset + length);
            
            if (newText.isEmpty()) return true;
            
            try {
                int value = Integer.parseInt(newText);
                return value >= 0 && value <= max;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            PrelimGradeCalculator calculator = new PrelimGradeCalculator();
            calculator.setVisible(true);
        });
    }
}