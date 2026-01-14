import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.UUID;

public class AttendanceTrackerInGit extends JFrame {

    private JTextField nameField;
    private JTextField courseField;
    private JTextField timeField;
    private JTextField signatureField;

    // Valid Perps courses
    private static final Set<String> VALID_COURSES = new HashSet<>(Arrays.asList(
            "BSIT", "BSCS", "BSIS", "BSCPE",
            "BSBA", "BSA", "BSAIS",
            "BSN", "BSPT", "BSRT", "BSMID",
            "BSED", "BEED",
            "BSHM", "BSTM",
            "CRIM", "BSCRIM",
            "AB PSYCHOLOGY", "BS PSYCHOLOGY"
    ));

    // Year normalization
    private static final Map<String, String> YEAR_MAP = new HashMap<>();
    static {
        YEAR_MAP.put("1ST", "1ST");
        YEAR_MAP.put("FIRST", "1ST");
        YEAR_MAP.put("2ND", "2ND");
        YEAR_MAP.put("SECOND", "2ND");
        YEAR_MAP.put("3RD", "3RD");
        YEAR_MAP.put("THIRD", "3RD");
        YEAR_MAP.put("4TH", "4TH");
        YEAR_MAP.put("FOURTH", "4TH");
    }

    public AttendanceTrackerInGit() {
        setTitle("Attendance Tracker");
        setSize(460, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Attendance Name:"), gbc);

        gbc.gridx = 1;
        nameField = new JTextField(20);
        panel.add(nameField, gbc);

        // Course + Year (free text)
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Course & Year:"), gbc);

        gbc.gridx = 1;
        courseField = new JTextField(20);
        courseField.setToolTipText("Example: BSIT 1ST YEAR or 1ST YEAR BSIT");
        panel.add(courseField, gbc);

        // Time
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Time In:"), gbc);

        gbc.gridx = 1;
        timeField = new JTextField(20);
        timeField.setEditable(false);
        timeField.setBackground(Color.WHITE);
        timeField.setText(LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        panel.add(timeField, gbc);

        // Signature
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("E-Signature:"), gbc);

        gbc.gridx = 1;
        signatureField = new JTextField(20);
        signatureField.setEditable(false);
        signatureField.setBackground(Color.WHITE);
        signatureField.setText(UUID.randomUUID().toString());
        panel.add(signatureField, gbc);

        // Button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JButton submitButton = new JButton("Submit Attendance");
        panel.add(submitButton, gbc);

        submitButton.addActionListener(this::handleSubmit);

        add(panel);
    }

    private void handleSubmit(ActionEvent e) {

        String name = nameField.getText().trim();
        String input = courseField.getText().trim().toUpperCase();

        if (name.isEmpty() || input.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        String foundCourse = null;
        String foundYear = null;

        // Find course (supports multi-word like AB PSYCHOLOGY)
        for (String course : VALID_COURSES) {
            if (input.contains(course)) {
                foundCourse = course;
                break;
            }
        }

        // Find year
        for (String key : YEAR_MAP.keySet()) {
            if (input.contains(key)) {
                foundYear = YEAR_MAP.get(key);
                break;
            }
        }

        if (foundCourse == null || foundYear == null) {
            showError("Invalid course or year.\nExample: BSIT 1ST YEAR");
            return;
        }

        // Auto-format
        String formattedCourseYear = foundCourse + " " + foundYear + " YEAR";

        JOptionPane.showMessageDialog(
                this,
                "Attendance Submitted Successfully!\n\n"
                        + "Name: " + name + "\n"
                        + "Course & Year: " + formattedCourseYear + "\n"
                        + "Time In: " + timeField.getText(),
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(
                this,
                msg,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AttendanceTrackerInGit().setVisible(true));
    }
}

