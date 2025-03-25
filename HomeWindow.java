import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.awt.event.*;

public class HomeWindow extends JFrame {
    private final String url = "jdbc:mysql://localhost:3306/CollegeElection";
    private final String dbUsername = "root";
    private final String dbPassword = "Diganta7001@MySQL";
    private User user;
    private javax.swing.Timer totalTimer;
    private int totalTimeLeft = 150;

    public HomeWindow(User user) {
        this.user = user;
        initializeUI();
        setupComponents();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }

    private void initializeUI() {
        setTitle("College Election System - Home");
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


        JLabel titleLabel = new JLabel("STUDENT DASHBOARD");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(200, 230, 255));
        gbc.gridy = 0;
        contentPanel.add(titleLabel, gbc);


        String[] studentInfo = getStudentInfo();
        JLabel nameLabel = createInfoLabel("Student Name: " + studentInfo[0]);
        JLabel emailLabel = createInfoLabel("Student Email: " + studentInfo[1]);

        gbc.gridy = 1;
        contentPanel.add(nameLabel, gbc);
        gbc.gridy = 2;
        contentPanel.add(emailLabel, gbc);


        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 20, 20));
        buttonPanel.setOpaque(false);
        buttonPanel.setPreferredSize(new Dimension(700, 60));

        JButton registerButton = createDecoratedButton("REGISTER",
                new Color(0, 120, 200), new Color(0, 160, 255));
        JButton voteButton = createDecoratedButton("VOTE NOW",
                new Color(0, 150, 100), new Color(0, 200, 150));
        JButton logoutButton = createDecoratedButton("LOGOUT",
                new Color(150, 50, 50), new Color(200, 80, 80));

        buttonPanel.add(registerButton);
        buttonPanel.add(voteButton);
        buttonPanel.add(logoutButton);

        gbc.gridy = 3;
        contentPanel.add(buttonPanel, gbc);


        JPanel positionsPanel = new JPanel();
        positionsPanel.setLayout(new BoxLayout(positionsPanel, BoxLayout.Y_AXIS));
        positionsPanel.setOpaque(false);
        positionsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 230, 255)),
                "Your Registered Positions",
                0, 0,
                new Font("Arial", Font.BOLD, 16),
                new Color(200, 230, 255)));

        showRegisteredPositions(positionsPanel);

        JScrollPane scrollPane = new JScrollPane(positionsPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setPreferredSize(new Dimension(500, 200));

        gbc.gridy = 4;
        contentPanel.add(scrollPane, gbc);


        mainPanel.add(contentPanel);


        registerButton.addActionListener(e -> handleRegistration());
        voteButton.addActionListener(e -> handleVoting());
        logoutButton.addActionListener(e -> logout());
    }

    private String[] getStudentInfo() {
        String[] info = new String[2];
        String query = "SELECT student_name, student_email FROM Students WHERE student_id = ?";
        try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, user.getStudentId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                info[0] = rs.getString("student_name");
                info[1] = rs.getString("student_email");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching student info: " + e.getMessage());
        }
        return info;
    }

    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 18));
        label.setForeground(Color.WHITE);
        return label;
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
        button.setPreferredSize(new Dimension(220, 45));

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


    private void showRegisteredPositions(JPanel panel) {
        panel.removeAll();
        String query = "SELECT position FROM candidates WHERE student_id = ?";
        try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, user.getStudentId());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                JLabel positionLabel = new JLabel("• " + rs.getString("position"));
                positionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
                positionLabel.setForeground(Color.WHITE);
                panel.add(positionLabel);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading positions: " + e.getMessage());
        }
        panel.revalidate();
        panel.repaint();
    }

    private void handleRegistration() {
        if (hasExistingRegistrations()) {
            JOptionPane.showMessageDialog(this, "You are already registered!");
        } else if (isElectionActive()) {
            JOptionPane.showMessageDialog(this, "Registration Closed - Election Ongoing");
        } else {
            new CandidateRegistration(user);
            refreshPositions();
        }
    }

    private void handleVoting() {
        if (isElectionActive()) {
            if (markUserAsVoted()) {
                new VotingWindow(user, this);
                startTotalTimer();
            } else {
                JOptionPane.showMessageDialog(this, "Voting failed to start!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Election has not started yet");
        }
    }

    private void refreshPositions() {
        Component[] components = getContentPane().getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                Component[] subComps = panel.getComponents();
                for (Component subComp : subComps) {
                    if (subComp instanceof JScrollPane) {
                        JScrollPane scrollPane = (JScrollPane) subComp;
                        JViewport viewport = scrollPane.getViewport();
                        if (viewport.getView() instanceof JPanel) {
                            showRegisteredPositions((JPanel) viewport.getView());
                        }
                    }
                }
            }
        }
    }

    private boolean hasExistingRegistrations() {
        String query = "SELECT * FROM candidates WHERE student_id = ?";
        try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, user.getStudentId());
            return ps.executeQuery().next();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
            return true;
        }
    }

    private boolean isElectionActive() {
        String query = "SELECT status FROM elections WHERE id = 1";
        try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);
             PreparedStatement ps = conn.prepareStatement(query)) {

            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getString("status").equalsIgnoreCase("active");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
            return false;
        }
    }

    private boolean markUserAsVoted() {
        String query = "UPDATE students SET has_voted = TRUE WHERE student_id = ?";
        try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, user.getStudentId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
            return false;
        }
    }

    private void startTotalTimer() {
        totalTimer = new javax.swing.Timer(1000, e -> {
            totalTimeLeft--;
            if (totalTimeLeft <= 0) {
                stopTotalTimer();
                forceLogout();
            }
        });
        totalTimer.start();
    }

    public void stopTotalTimer() {
        if (totalTimer != null && totalTimer.isRunning()) {
            totalTimer.stop();
        }
    }

    private void forceLogout() {
        dispose();
        new Login();
    }

    private void logout() {
        stopTotalTimer();
        dispose();
        new Login();
    }
}