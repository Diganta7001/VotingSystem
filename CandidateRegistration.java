import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class CandidateRegistration extends JFrame {
    private final String url = "jdbc:mysql://localhost:3306/CollegeElection";
    private final String dbUsername = "root";
    private final String dbPassword = "Diganta7001@MySQL";
    private User user;
    private JCheckBox gsCheckBox, sportsCheckBox, vpCheckBox, eventsCheckBox;
    private String candidateName = "";
    private String candidateEmail = "";

    public CandidateRegistration(User user) {
        this.user = user;
        fetchCandidateDetails();

        if (isAlreadyRegistered()) {
            JOptionPane.showMessageDialog(this, "You are already registered!");
            dispose();
            return;
        }

        setTitle("Candidate Registration");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);


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
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;


        JLabel titleLabel = new JLabel("CANDIDATE REGISTRATION");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(200, 230, 255));
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(titleLabel, gbc);


        JLabel nameLabel = new JLabel("Name: " + candidateName);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        nameLabel.setForeground(Color.WHITE);
        gbc.gridy = 1;
        contentPanel.add(nameLabel, gbc);

        JLabel emailLabel = new JLabel("Email: " + candidateEmail);
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        emailLabel.setForeground(Color.WHITE);
        gbc.gridy = 2;
        contentPanel.add(emailLabel, gbc);


        JLabel positionsTitle = new JLabel("Select Positions to Contest:");
        positionsTitle.setFont(new Font("Arial", Font.BOLD, 20));
        positionsTitle.setForeground(new Color(200, 230, 255));
        gbc.gridy = 3;
        contentPanel.add(positionsTitle, gbc);


        JLabel warningLabel1 = new JLabel("(Be Careful While Selecting Your Positions");
        warningLabel1.setFont(new Font("Arial", Font.ITALIC, 14));
        warningLabel1.setForeground(new Color(255, 200, 200));
        gbc.gridy = 4;
        contentPanel.add(warningLabel1, gbc);

        JLabel warningLabel2 = new JLabel("As You Can Only Register Once)");
        warningLabel2.setFont(new Font("Arial", Font.ITALIC, 14));
        warningLabel2.setForeground(new Color(255, 200, 200));
        gbc.gridy = 5;
        contentPanel.add(warningLabel2, gbc);


        JPanel checkBoxPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        checkBoxPanel.setOpaque(false);
        checkBoxPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        gsCheckBox = createStyledCheckBox("General Secretary");
        sportsCheckBox = createStyledCheckBox("Sports Secretary");
        vpCheckBox = createStyledCheckBox("Vice President");
        eventsCheckBox = createStyledCheckBox("Event Secretary");

        checkBoxPanel.add(gsCheckBox);
        checkBoxPanel.add(sportsCheckBox);
        checkBoxPanel.add(vpCheckBox);
        checkBoxPanel.add(eventsCheckBox);

        gbc.gridy = 6;
        contentPanel.add(checkBoxPanel, gbc);


        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton submitButton = createDecoratedButton("SUBMIT", new Color(0, 150, 100), new Color(0, 200, 150));
        submitButton.addActionListener(e -> handleSubmission());

        JButton cancelButton = createDecoratedButton("CANCEL", new Color(150, 50, 50), new Color(200, 80, 80));
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

        gbc.gridy = 7;
        contentPanel.add(buttonPanel, gbc);

        mainPanel.add(contentPanel);
        setVisible(true);
    }

    private JCheckBox createStyledCheckBox(String text) {
        JCheckBox checkBox = new JCheckBox(text);
        checkBox.setFont(new Font("Arial", Font.PLAIN, 16));
        checkBox.setForeground(Color.WHITE);
        checkBox.setOpaque(false);
        checkBox.setFocusPainted(false);
        checkBox.setIcon(new StyledCheckBoxIcon());
        return checkBox;
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
        return button;
    }

    private void handleSubmission() {
        int response = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to submit your candidacy?",
                "Confirm Submission",
                JOptionPane.YES_NO_OPTION
        );
        if (response == JOptionPane.YES_OPTION) {
            registerCandidate();
        }
    }

    private void fetchCandidateDetails() {
        String query = "SELECT student_name, student_email FROM Students WHERE student_id = ?";
        try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, user.getStudentId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                candidateName = rs.getString("student_name");
                candidateEmail = rs.getString("student_email");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching candidate details: " + e.getMessage());
        }
    }

    private boolean isAlreadyRegistered() {
        String query = "SELECT * FROM candidates WHERE student_id = ?";
        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, user.getStudentId());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
            return true;
        }
    }

    private void registerCandidate() {
        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword)) {
            boolean anySelected = false;
            if (gsCheckBox.isSelected()) {
                insertPosition(connection, "General Secretary");
                anySelected = true;
            }
            if (sportsCheckBox.isSelected()) {
                insertPosition(connection, "Sports Secretary");
                anySelected = true;
            }
            if (vpCheckBox.isSelected()) {
                insertPosition(connection, "Vice President");
                anySelected = true;
            }
            if (eventsCheckBox.isSelected()) {
                insertPosition(connection, "Event Secretary");
                anySelected = true;
            }
            if (!anySelected) {
                JOptionPane.showMessageDialog(this, "Please select at least one position!");
                return;
            }
            JOptionPane.showMessageDialog(this, "Registration Successful!");
            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void insertPosition(Connection connection, String position) throws SQLException {
        String query = "INSERT INTO candidates (student_id, position, candidate_name) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, user.getStudentId());
            ps.setString(2, position);
            ps.setString(3, candidateName);
            ps.executeUpdate();
        }
    }

    static class StyledCheckBoxIcon implements Icon {
        private static final int SIZE = 18;

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            JCheckBox cb = (JCheckBox) c;
            if (cb.isSelected()) {
                g2.setColor(new Color(0, 160, 255));
                g2.fillRoundRect(x, y, SIZE, SIZE, 4, 4);
                g2.setColor(Color.WHITE);
                g2.drawLine(x+3, y+SIZE/2, x+SIZE/2-1, y+SIZE-4);
                g2.drawLine(x+SIZE/2-1, y+SIZE-4, x+SIZE-4, y+2);
            } else {
                g2.setColor(new Color(200, 200, 200, 100));
                g2.drawRoundRect(x, y, SIZE-1, SIZE-1, 4, 4);
            }
            g2.dispose();
        }

        @Override
        public int getIconWidth() { return SIZE; }

        @Override
        public int getIconHeight() { return SIZE; }
    }
}