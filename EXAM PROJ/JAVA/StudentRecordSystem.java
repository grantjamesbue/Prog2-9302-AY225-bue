import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;

/*
 Programmer: Grant james D. Bue | 25-0873-849s
*/

public class StudentRecordSystem extends JFrame {

    JTextField txtID, txtFirst, txtLast, txtLab1, txtLab2, txtLab3, txtPrelim, txtAttendance;
    JTable table;
    DefaultTableModel model;

    public StudentRecordSystem() {
        setTitle("Student Record System - YOUR FULL NAME - YOUR STUDENT ID");
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== TOP PANEL =====
        JPanel topPanel = new JPanel(new GridLayout(4, 4, 10, 5));
        topPanel.setBorder(BorderFactory.createTitledBorder("Student Information"));

        txtID = new JTextField();
        txtFirst = new JTextField();
        txtLast = new JTextField();
        txtLab1 = new JTextField();
        txtLab2 = new JTextField();
        txtLab3 = new JTextField();
        txtPrelim = new JTextField();
        txtAttendance = new JTextField();

        topPanel.add(new JLabel("Student ID:"));
        topPanel.add(txtID);
        topPanel.add(new JLabel("First Name:"));
        topPanel.add(txtFirst);

        topPanel.add(new JLabel("Last Name:"));
        topPanel.add(txtLast);
        topPanel.add(new JLabel("Lab Work 1:"));
        topPanel.add(txtLab1);

        topPanel.add(new JLabel("Lab Work 2:"));
        topPanel.add(txtLab2);
        topPanel.add(new JLabel("Lab Work 3:"));
        topPanel.add(txtLab3);

        topPanel.add(new JLabel("Prelim Exam:"));
        topPanel.add(txtPrelim);
        topPanel.add(new JLabel("Attendance:"));
        topPanel.add(txtAttendance);

        add(topPanel, BorderLayout.NORTH);

        // ===== TABLE =====
        String[] columns = {
                "Student ID", "First Name", "Last Name",
                "Lab Work 1", "Lab Work 2", "Lab Work 3",
                "Prelim Exam", "Attendance Grade"
        };

        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ===== BUTTONS =====
        JPanel btnPanel = new JPanel();

        JButton btnAdd = new JButton("Add Record");
        JButton btnDelete = new JButton("Delete Record");

        btnPanel.add(btnAdd);
        btnPanel.add(btnDelete);

        add(btnPanel, BorderLayout.SOUTH);

        // ===== BUTTON ACTIONS =====
        btnAdd.addActionListener(e -> addRecord());
        btnDelete.addActionListener(e -> deleteRecord());

        loadCSV("../MOCK_DATA.csv");
    }

    void loadCSV(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            br.readLine(); // skip header

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                model.addRow(data);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading CSV file.");
        }
    }

    void addRecord() {
        model.addRow(new String[]{
                txtID.getText(),
                txtFirst.getText(),
                txtLast.getText(),
                txtLab1.getText(),
                txtLab2.getText(),
                txtLab3.getText(),
                txtPrelim.getText(),
                txtAttendance.getText()
        });

        clearFields();
    }

    void deleteRecord() {
        int row = table.getSelectedRow();
        if (row != -1) {
            model.removeRow(row);
        }
    }

    void clearFields() {
        txtID.setText("");
        txtFirst.setText("");
        txtLast.setText("");
        txtLab1.setText("");
        txtLab2.setText("");
        txtLab3.setText("");
        txtPrelim.setText("");
        txtAttendance.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentRecordSystem().setVisible(true));
    }
}
