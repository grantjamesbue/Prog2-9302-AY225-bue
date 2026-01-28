package Java;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.LineBorder;
import javax.swing.text.*;

public class PrelimLabWork3 extends JFrame implements ActionListener {

    JTextField txtAttendance, txtLab1, txtLab2, txtLab3;
    JTextPane txtResult;
    JScrollPane scroll;
    JButton btnCompute, btnClear;

    public PrelimLabWork3() {
        setTitle("Prelim Grade Calculator");
        setSize(550, 650);
        setMinimumSize(new Dimension(500, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Background panel with gradient
        JPanel bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(10, 10, 40),
                        0, getHeight(), new Color(50, 0, 80)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bgPanel.setLayout(new GridBagLayout());

        JPanel pnlMain = new JPanel(new GridBagLayout());
        pnlMain.setBackground(new Color(0, 0, 0, 150));
        pnlMain.setBorder(new LineBorder(Color.CYAN, 3, true));
        pnlMain.setPreferredSize(new Dimension(400, 580));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font titleFont = new Font("Orbitron", Font.BOLD, 16);
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font textFont = new Font("Arial", Font.PLAIN, 14);
        Color neonText = Color.CYAN;

        // Title panel
        JPanel titlePanel = new JPanel(new GridBagLayout());
        titlePanel.setBackground(new Color(0, 0, 0, 150));
        titlePanel.setBorder(new LineBorder(Color.CYAN, 2, true));
        titlePanel.setPreferredSize(new Dimension(350, 50));

        JLabel lblTitle = new JLabel("🌌 PRELIM GRADE CALCULATOR 🌠", JLabel.CENTER);
        lblTitle.setFont(titleFont);
        lblTitle.setForeground(neonText);
        titlePanel.add(lblTitle);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        pnlMain.add(titlePanel, gbc);
        gbc.gridwidth = 1;

        // Attendance
        gbc.gridy = 1; gbc.gridx = 0;
        JLabel lblAttendance = new JLabel("Attendance:");
        lblAttendance.setFont(labelFont);
        lblAttendance.setForeground(neonText);
        pnlMain.add(lblAttendance, gbc);

        gbc.gridx = 1;
        txtAttendance = createTextField(textFont);
        pnlMain.add(txtAttendance, gbc);

        // Lab fields
        addField(pnlMain, gbc, 2, "Lab Work 1:", txtLab1 = createTextField(textFont), labelFont, neonText);
        addField(pnlMain, gbc, 3, "Lab Work 2:", txtLab2 = createTextField(textFont), labelFont, neonText);
        addField(pnlMain, gbc, 4, "Lab Work 3:", txtLab3 = createTextField(textFont), labelFont, neonText);

        // Buttons
        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        pnlButtons.setOpaque(false);

        btnCompute = new JButton("Compute");
        styleButton(btnCompute);
        btnCompute.addActionListener(this);

        btnClear = new JButton("Clear");
        styleButton(btnClear);
        btnClear.addActionListener(this);

        pnlButtons.add(btnCompute);
        pnlButtons.add(btnClear);
        pnlMain.add(pnlButtons, gbc);

        // Result box using JTextPane
        gbc.gridy = 6; gbc.gridx = 0; gbc.gridwidth = 2;

        txtResult = new JTextPane();
        txtResult.setEditable(false);
        txtResult.setBackground(Color.GRAY); // gray always
        txtResult.setForeground(Color.BLACK);
        txtResult.setFont(textFont);
        txtResult.setMargin(new Insets(10, 10, 10, 10));

        scroll = new JScrollPane(txtResult);
        scroll.setBorder(new LineBorder(neonText, 2, true));
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scroll.setPreferredSize(new Dimension(350, 300));
        scroll.setMinimumSize(new Dimension(350, 300));
        scroll.setMaximumSize(new Dimension(350, 300));

        pnlMain.add(scroll, gbc);

        bgPanel.add(pnlMain);
        setContentPane(bgPanel);
        setVisible(true);
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int y,
                          String label, JTextField field,
                          Font labelFont, Color color) {
        gbc.gridy = y; gbc.gridx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(labelFont);
        lbl.setForeground(color);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private JTextField createTextField(Font font) {
        JTextField tf = new JTextField(8);
        tf.setFont(font);
        tf.setHorizontalAlignment(JTextField.CENTER);
        tf.setBackground(new Color(0, 0, 0, 100));
        tf.setForeground(Color.CYAN);
        tf.setBorder(new LineBorder(Color.CYAN, 2));
        return tf;
    }

    private void styleButton(JButton btn) {
        btn.setBackground(new Color(50, 0, 80));
        btn.setForeground(Color.CYAN);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(Color.CYAN, 2));
        btn.setOpaque(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnCompute) {
            computeGrades();
        } else if (e.getSource() == btnClear) {
            txtAttendance.setText("");
            txtLab1.setText("");
            txtLab2.setText("");
            txtLab3.setText("");
            txtResult.setText("");
        }
    }

    private void computeGrades() {
        try {
            double attendance = Double.parseDouble(txtAttendance.getText());
            double lab1 = Double.parseDouble(txtLab1.getText());
            double lab2 = Double.parseDouble(txtLab2.getText());
            double lab3 = Double.parseDouble(txtLab3.getText());

            // Input validation: max 100
            if (attendance > 100 || lab1 > 100 || lab2 > 100 || lab3 > 100) {
                txtResult.setText("⚠ ERROR: Grades cannot exceed 100!\nPlease enter valid numbers between 0–100.");
                return;
            }

            double labAverage = (lab1 + lab2 + lab3) / 3;
            double classStanding = (attendance * 0.4) + (labAverage * 0.6);

            txtResult.setText("");

            StyledDocument doc = txtResult.getStyledDocument();
            Style regular = txtResult.addStyle("regular", null);
            Style bold = txtResult.addStyle("bold", null);
            StyleConstants.setBold(bold, true);

            // --- RESULTS ---
            doc.insertString(doc.getLength(), "--- RESULTS ---\n", bold);
            doc.insertString(doc.getLength(), String.format("Lab Work 1: %.2f\n", lab1), regular);
            doc.insertString(doc.getLength(), String.format("Lab Work 2: %.2f\n", lab2), regular);
            doc.insertString(doc.getLength(), String.format("Lab Work 3: %.2f\n", lab3), regular);
            doc.insertString(doc.getLength(), String.format("Lab Average: %.2f\n", labAverage), regular);
            doc.insertString(doc.getLength(), String.format("Attendance: %.2f\n", attendance), regular);
            doc.insertString(doc.getLength(), String.format("Class Standing: %.2f\n\n", classStanding), regular);

            // Warnings
            if (attendance < 75) {
                doc.insertString(doc.getLength(), "⚠ WARNING: Attendance below 75%! ⚠\n", bold);
                doc.insertString(doc.getLength(), "High possibility to FAIL due to low attendance.\n\n", regular);
            }
            if (labAverage < 75) {
                doc.insertString(doc.getLength(), "⚠ WARNING: Lab Average below 75! ⚠\n", bold);
                doc.insertString(doc.getLength(), "High possibility to FAIL due to low lab scores.\n\n", regular);
            }

            // Required Prelim Scores & Remark
            double required75 = (75 - classStanding * 0.7) / 0.3;
            double required100 = (100 - classStanding * 0.7) / 0.3;

            String msg75 = (classStanding >= 75 || required75 <= 0)
                    ? "Already above 75 – only the exam is left. Just do it and you’re safe!"

                    : (required75 > 100) ? "Impossible to achieve" : String.format("%.2f", required75);

            String msg100 = (required100 > 100)
                    ? "Impossible to achieve"
                    : (required100 <= 0) ? "Already Passed – maintain your score"
                    : String.format("%.2f", required100);

            String remark = (classStanding >= 75) ? "PASSED" : "FAILED";

            doc.insertString(doc.getLength(), "Remark: " + remark + "\n\n", bold);
            doc.insertString(doc.getLength(), "Required Prelim Score for 75: ", bold);
            doc.insertString(doc.getLength(), msg75 + "\n\n", regular);
            doc.insertString(doc.getLength(), "Required Prelim Score for 100: ", bold);
            doc.insertString(doc.getLength(), msg100 + "\n", regular);

        } catch (NumberFormatException ex) {
            txtResult.setText("⚠ ERROR: Please enter numbers only (0–100).");
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new PrelimLabWork3();
    }
}
