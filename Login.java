import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class Login extends JFrame {
    private JTextField idField;
    private JPasswordField logInPassword;
    private JButton logInButton, logInAsAdminButton, showResultsButton;
    private String authError;
    private final String url = "jdbc:mysql://localhost:3306/CollegeElection";
    private final String dbUsername = "root";
    private final String dbPassword = "Diganta7001@MySQL";

    public Login() {
        setTitle("College Election System");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(false);


        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(0, 30, 70),
                        getWidth(), getHeight(), new Color(100, 180, 255));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        add(mainPanel);


        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());
        contentPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;


        JLabel titleLabel = new JLabel("COLLEGE ELECTION SYSTEM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(200, 230, 255));
        gbc.gridy = 0;
        contentPanel.add(titleLabel, gbc);


        JLabel idLabel = new JLabel("Enter ID:");
        idLabel.setFont(new Font("Arial", Font.BOLD, 14));
        idLabel.setForeground(Color.WHITE);
        gbc.gridy = 1;
        contentPanel.add(idLabel, gbc);

        idField = new JTextField();
        idField.setFont(new Font("Arial", Font.PLAIN, 16));
        idField.setPreferredSize(new Dimension(400, 40));
        idField.setBackground(new Color(230, 240, 255));
        idField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 80, 150), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridy = 2;
        contentPanel.add(idField, gbc);


        JLabel passwordLabel = new JLabel("Enter Your Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passwordLabel.setForeground(Color.WHITE);
        gbc.gridy = 3;
        contentPanel.add(passwordLabel, gbc);

        logInPassword = new JPasswordField();
        logInPassword.setFont(new Font("Arial", Font.PLAIN, 16));
        logInPassword.setPreferredSize(new Dimension(400, 40));
        logInPassword.setBackground(new Color(230, 240, 255));
        logInPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 80, 150), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridy = 4;
        contentPanel.add(logInPassword, gbc);


        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        buttonPanel.setOpaque(false);
        buttonPanel.setPreferredSize(new Dimension(450, 120));

        logInButton = createDecoratedButton("LOGIN", new Color(0, 120, 200), new Color(0, 160, 255));
        logInAsAdminButton = createDecoratedButton("ADMIN LOGIN", new Color(0, 150, 100), new Color(0, 200, 150));
        showResultsButton = createDecoratedButton("SHOW RESULTS", new Color(180, 70, 70), new Color(220, 100, 100));
        JButton closeButton = createDecoratedButton("EXIT", new Color(150, 50, 50), new Color(200, 80, 80));

        buttonPanel.add(logInButton);
        buttonPanel.add(logInAsAdminButton);
        buttonPanel.add(showResultsButton);
        buttonPanel.add(closeButton);

        gbc.gridy = 5;
        contentPanel.add(buttonPanel, gbc);


        mainPanel.add(contentPanel);


        logInButton.addActionListener(e -> {
            String studentId = idField.getText().trim();
            String password = new String(logInPassword.getPassword());

            if (studentId.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter both ID and password.");
                return;
            }

            User user = authenticateUser(studentId, password);
            if (user != null) {
                dispose();
                new HomeWindow(user);
            } else {
                if ("already_voted".equals(authError)) {
                    JOptionPane.showMessageDialog(null, "You have already voted!");
                } else if ("invalid_credentials".equals(authError)) {
                    JOptionPane.showMessageDialog(null, "Invalid credentials.");
                }
            }
        });

        logInAsAdminButton.addActionListener(e -> {
            String adminId = idField.getText().trim();
            String password = new String(logInPassword.getPassword());

            if (adminId.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter both admin ID and password.");
                return;
            }

            if (authenticateAdmin(adminId, password)) {
                dispose();
                new AdminWindow(adminId);
            } else {
                JOptionPane.showMessageDialog(null, "Invalid admin credentials.");
            }
        });

        showResultsButton.addActionListener(e -> showResults());
        closeButton.addActionListener(e -> System.exit(0));

        setVisible(true);
    }

    private JButton createDecoratedButton(String text, Color baseColor, Color hoverColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(hoverColor);
                } else {
                    g2.setColor(baseColor);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 100));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
            }
        };

        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 45));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.repaint();
            }
        });

        return button;
    }

    private User authenticateUser(String student_Id, String password) {
        authError = null;
        String query = "SELECT student_id, student_name, student_email, has_voted FROM students WHERE student_id = ? AND password = ?";

        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, student_Id);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getBoolean("has_voted")) {
                    authError = "already_voted";
                    return null;
                }
                return new User(
                        rs.getString("student_id"),
                        rs.getString("student_name"),
                        rs.getString("student_email")
                );
            } else {
                authError = "invalid_credentials";
                return null;
            }
        } catch (SQLException e) {
            authError = "database_error";
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
            return null;
        }
    }

    private boolean authenticateAdmin(String adminId, String password) {
        String query = "SELECT * FROM admin WHERE admin_id = ? AND password = ?";

        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, adminId);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
            return false;
        }
    }

    private void showResults() {
        String status = "";
        String queryStatus = "SELECT status FROM elections WHERE id = 1";
        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
             PreparedStatement ps = connection.prepareStatement(queryStatus);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                status = rs.getString("status");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error checking election status: " + e.getMessage());
            return;
        }
        if ("active".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "Election is ongoing, Results are not available.");
            return;
        }

        String query = "SELECT position, candidate_name, votes FROM candidates ORDER BY position, votes DESC";
        Map<String, StringBuilder> resultsByPosition = new LinkedHashMap<>();
        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String position = rs.getString("position");
                String candidateName = rs.getString("candidate_name");
                int votes = rs.getInt("votes");

                resultsByPosition.putIfAbsent(position, new StringBuilder("Position: " + position + "\n"));
                resultsByPosition.get(position)
                        .append("Candidate: ").append(candidateName)
                        .append(" - Votes: ").append(votes).append("\n");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error retrieving vote results: " + e.getMessage());
            return;
        }

        StringBuilder message = new StringBuilder();
        for (Map.Entry<String, StringBuilder> entry : resultsByPosition.entrySet()) {
            String pos = entry.getKey();
            StringBuilder details = entry.getValue();
            String[] lines = details.toString().split("\n");
            if (lines.length >= 2) {
                String winnerLine = lines[1];
                details.append("Winner: ").append(winnerLine.substring(winnerLine.indexOf("Candidate: ") + "Candidate: ".length())).append("\n");
            }
            message.append(details.toString()).append("\n");
        }
        if (message.length() == 0) {
            message.append("No vote results available.");
        }
        JTextArea textArea = new JTextArea(message.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        JOptionPane.showMessageDialog(this, scrollPane, "Vote Results", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver loaded successfully!");
        } catch (ClassNotFoundException e) {
            System.out.println("Driver not found: " + e.getMessage());
        }
        SwingUtilities.invokeLater(() -> new Login());
    }
}