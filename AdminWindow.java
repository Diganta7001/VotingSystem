import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

class AdminWindow extends JFrame {
    private final String url = "jdbc:mysql://localhost:3306/CollegeElection";
    private final String dbUsername = "root";
    private final String dbPassword = "Diganta7001@MySQL";
    private String adminId;

    public AdminWindow(String adminId) {
        this.adminId = adminId;
        initializeUI();
        setupComponents();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }

    private void initializeUI() {
        setTitle("College Election System - Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(false);
    }

    private void setupComponents() {

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


        JLabel titleLabel = new JLabel("ADMIN DASHBOARD");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(200, 230, 255));
        gbc.gridy = 0;
        contentPanel.add(titleLabel, gbc);


        JLabel idLabel = new JLabel("Admin ID: " + adminId);
        idLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        idLabel.setForeground(Color.WHITE);
        gbc.gridy = 1;
        contentPanel.add(idLabel, gbc);


        JLabel statusLabel = new JLabel(getElectionStatus());
        statusLabel.setFont(new Font("Arial", Font.BOLD, 18));
        statusLabel.setForeground(new Color(255, 215, 0));
        gbc.gridy = 2;
        contentPanel.add(statusLabel, gbc);


        JPanel buttonPanel1 = new JPanel(new GridLayout(1, 2, 30, 20));
        buttonPanel1.setOpaque(false);
        buttonPanel1.setPreferredSize(new Dimension(600, 60));

        JButton startElectionButton = createDecoratedButton("START ELECTION",
                new Color(50, 150, 50), new Color(100, 200, 100));
        JButton stopElectionButton = createDecoratedButton("STOP ELECTION",
                new Color(180, 50, 50), new Color(220, 80, 80));

        buttonPanel1.add(startElectionButton);
        buttonPanel1.add(stopElectionButton);

        gbc.gridy = 3;
        contentPanel.add(buttonPanel1, gbc);


        JPanel buttonPanel2 = new JPanel(new GridLayout(1, 2, 30, 20));
        buttonPanel2.setOpaque(false);
        buttonPanel2.setPreferredSize(new Dimension(600, 60));

        JButton voteResultsButton = createDecoratedButton("VOTE RESULTS",
                new Color(0, 120, 200), new Color(0, 160, 255));
        JButton seeVotersButton = createDecoratedButton("VIEW VOTERS",
                new Color(150, 0, 150), new Color(180, 50, 180));

        buttonPanel2.add(voteResultsButton);
        buttonPanel2.add(seeVotersButton);

        gbc.gridy = 4;
        contentPanel.add(buttonPanel2, gbc);


        JPanel buttonPanel3 = new JPanel(new GridLayout(1, 2, 30, 20));
        buttonPanel3.setOpaque(false);
        buttonPanel3.setPreferredSize(new Dimension(600, 60));

        JButton resetElectionsButton = createDecoratedButton("RESET ELECTIONS",
                new Color(200, 100, 0), new Color(255, 140, 0));
        JButton logoutButton = createDecoratedButton("LOGOUT",
                new Color(150, 50, 50), new Color(200, 80, 80));

        buttonPanel3.add(resetElectionsButton);
        buttonPanel3.add(logoutButton);

        gbc.gridy = 5;
        contentPanel.add(buttonPanel3, gbc);


        mainPanel.add(contentPanel);


        startElectionButton.addActionListener(e -> {
            toggleElectionStatus(true);
            statusLabel.setText(getElectionStatus());
            statusLabel.setForeground(new Color(50, 200, 50));
        });

        stopElectionButton.addActionListener(e -> {
            toggleElectionStatus(false);
            statusLabel.setText(getElectionStatus());
            statusLabel.setForeground(new Color(255, 80, 80));
        });

        voteResultsButton.addActionListener(e -> showVoteResults());
        seeVotersButton.addActionListener(e -> showVoters());
        resetElectionsButton.addActionListener(e -> resetElections());
        logoutButton.addActionListener(e -> logout());
    }

    private JButton createDecoratedButton(String text, Color baseColor, Color hoverColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(baseColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(hoverColor, 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        button.setPreferredSize(new Dimension(280, 50));


        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor);
            }
        });

        return button;
    }

    private String getElectionStatus() {
        String query = "SELECT status FROM elections WHERE id = 1";
        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return "Current Status: " + (rs.getString("status").equalsIgnoreCase("active") ?
                        "ELECTION ACTIVE" : "ELECTION INACTIVE");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Current Status: UNKNOWN";
    }

    private void toggleElectionStatus(boolean start) {
        String status = start ? "active" : "inactive";
        String message = start ? "Election started successfully!" : "Election stopped successfully!";
        String query = "UPDATE elections SET status = ? WHERE id = 1";

        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, status);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, message);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update election status");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showVoteResults() {
        String query = "SELECT position, student_id, candidate_name, votes FROM candidates ORDER BY position, votes DESC";
        Map<String, StringBuilder> resultsByPosition = new LinkedHashMap<>();

        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String position = rs.getString("position");
                String studentId = rs.getString("student_id");
                String candidateName = rs.getString("candidate_name");
                int votes = rs.getInt("votes");

                resultsByPosition.putIfAbsent(position, new StringBuilder("Position: " + position + "\n"));
                resultsByPosition.get(position)
                        .append("Candidate: ").append(candidateName)
                        .append(" (ID: ").append(studentId)
                        .append(") - Votes: ").append(votes).append("\n");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error retrieving vote results: " + e.getMessage());
            return;
        }

        StringBuilder message = new StringBuilder();
        for (StringBuilder sb : resultsByPosition.values()) {
            message.append(sb.toString()).append("\n");
        }
        if (message.length() == 0) {
            message.append("No vote results available.");
        }

        JTextArea textArea = new JTextArea(message.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        JOptionPane.showMessageDialog(this, scrollPane, "Vote Results", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showVoters() {
        String query = "SELECT student_id, student_name FROM students WHERE has_voted = TRUE";
        StringBuilder votersList = new StringBuilder("Voters:\n");

        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String studentId = rs.getString("student_id");
                String studentName = rs.getString("student_name");
                votersList.append(studentName).append(" (ID: ").append(studentId).append(")\n");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error retrieving voters: " + e.getMessage());
            return;
        }

        if (votersList.toString().equals("Voters:\n")) {
            votersList.append("No voters found.");
        }

        JTextArea textArea = new JTextArea(votersList.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        JOptionPane.showMessageDialog(this, scrollPane, "Voters", JOptionPane.INFORMATION_MESSAGE);
    }

    private void resetElections() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to reset the elections?",
                "Confirm Reset", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String inputPassword = JOptionPane.showInputDialog(this, "Re-enter your admin password:");
        if (inputPassword == null) {
            return;
        }

        if (!validateAdminPassword(inputPassword)) {
            JOptionPane.showMessageDialog(this, "Invalid admin password. Reset aborted.");
            return;
        }

        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword)) {
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM candidates")) {
                ps.executeUpdate();
            }

            try (PreparedStatement ps = connection.prepareStatement("UPDATE students SET has_voted = NULL")) {
                ps.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "Elections have been reset successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error resetting elections: " + e.getMessage());
        }
    }

    private boolean validateAdminPassword(String inputPassword) {
        String query = "SELECT password FROM Admin WHERE admin_id = ?";
        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, adminId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String actualPassword = rs.getString("password");
                    return actualPassword.equals(inputPassword);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error during password validation: " + e.getMessage());
        }
        return false;
    }

    private void logout() {
        dispose();
        new Login();
    }
}