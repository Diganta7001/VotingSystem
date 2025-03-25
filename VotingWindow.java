import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;

public class VotingWindow extends JFrame {
    private final String url = "jdbc:mysql://localhost:3306/CollegeElection";
    private final String dbUsername = "root";
    private final String dbPassword = "Diganta7001@MySQL";
    private User user;
    private HomeWindow homeWindow;
    private Map<String, ButtonGroup> positionGroups = new HashMap<>();
    private JLabel timerLabel;
    private int votingTimeLeft = 60;
    private javax.swing.Timer votingTimer;
    private JScrollPane votingScrollPane;

    public VotingWindow(User user, HomeWindow homeWindow) {
        this.user = user;
        this.homeWindow = homeWindow;
        initializeUI();
        loadCandidates();
        startVotingTimer();

        setUndecorated(true);
        GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice().setFullScreenWindow(this);
        setVisible(true);
    }

    private void initializeUI() {
        setTitle("Voting Portal - " + user.getStudentId());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);


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
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);


        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        timerLabel = new JLabel("Time Left: 01:00");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        timerLabel.setForeground(new Color(255, 255, 255, 200));
        headerPanel.add(timerLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);


        JPanel votingPanel = new JPanel();
        votingPanel.setLayout(new BoxLayout(votingPanel, BoxLayout.Y_AXIS));
        votingPanel.setOpaque(false);
        votingPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        votingScrollPane = new JScrollPane(votingPanel);
        votingScrollPane.setOpaque(false);
        votingScrollPane.getViewport().setOpaque(false);
        votingScrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainPanel.add(votingScrollPane, BorderLayout.CENTER);


        JButton submitButton = createDecoratedButton("SUBMIT VOTES",
                new Color(0, 150, 100), new Color(0, 200, 150));
        submitButton.addActionListener(e -> submitVotes());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 40, 0));
        buttonPanel.add(submitButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadCandidates() {
        JPanel votingPanel = (JPanel) votingScrollPane.getViewport().getView();
        votingPanel.removeAll();

        String query = "SELECT DISTINCT position FROM candidates";
        try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String position = rs.getString("position");
                JPanel positionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 15));
                positionPanel.setOpaque(false);

                JLabel positionLabel = new JLabel(position + ":");
                positionLabel.setFont(new Font("Arial", Font.BOLD, 18));
                positionLabel.setForeground(new Color(200, 230, 255));
                positionPanel.add(positionLabel);

                ButtonGroup group = new ButtonGroup();
                addCandidateOptions(positionPanel, group, position);
                positionGroups.put(position, group);

                votingPanel.add(positionPanel);
                votingPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading positions: " + e.getMessage());
        }
        votingPanel.revalidate();
        votingPanel.repaint();
    }

    private void addCandidateOptions(JPanel panel, ButtonGroup group, String position) {
        String query = "SELECT student_id, candidate_name FROM candidates WHERE position = ?";
        try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, position);
            ResultSet rs = ps.executeQuery();

            JRadioButton noneRb = createStyledRadioButton("None");
            noneRb.setActionCommand("NONE");
            group.add(noneRb);
            panel.add(noneRb);

            while (rs.next()) {
                String studentId = rs.getString("student_id");
                String candidateName = rs.getString("candidate_name");

                JRadioButton rb = createStyledRadioButton(candidateName);
                rb.setActionCommand(studentId);
                group.add(rb);
                panel.add(rb);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading candidates: " + e.getMessage());
        }
    }

    private JRadioButton createStyledRadioButton(String text) {
        JRadioButton radioButton = new JRadioButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (isSelected()) {
                    g2.setColor(new Color(0, 160, 255));
                    g2.fillOval(2, 5, 16, 16);
                } else {
                    g2.setColor(new Color(200, 200, 200, 100));
                    g2.drawOval(2, 5, 16, 16);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        radioButton.setFont(new Font("Arial", Font.PLAIN, 16));
        radioButton.setForeground(Color.WHITE);
        radioButton.setOpaque(false);
        radioButton.setFocusPainted(false);
        radioButton.setContentAreaFilled(false);
        radioButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return radioButton;
    }

    private JButton createDecoratedButton(String text, Color baseColor, Color hoverColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? hoverColor : baseColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(255, 255, 255, 100));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 25, 25);
            }
        };
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(250, 50));
        return button;
    }

    private void startVotingTimer() {
        votingTimer = new javax.swing.Timer(1000, e -> {
            votingTimeLeft--;
            updateTimerDisplay();
            if (votingTimeLeft <= 0) {
                votingTimer.stop();
                submitVotes();
            }
        });
        votingTimer.start();
    }

    private void updateTimerDisplay() {
        String minutes = String.format("%02d", votingTimeLeft / 60);
        String seconds = String.format("%02d", votingTimeLeft % 60);
        timerLabel.setText("Time Left: " + minutes + ":" + seconds);
    }

    private void submitVotes() {
        votingTimer.stop();
        Map<String, String> votes = collectVotes();
        storeVotes(votes);

        SwingUtilities.invokeLater(() -> {
            ResultWindow resultWindow = new ResultWindow(user, votes);
            resultWindow.setHomeWindowReference(homeWindow);
            resultWindow.startResultTimer();
            resultWindow.setVisible(true);
        });

        dispose();
        homeWindow.setVisible(false);
    }

    private Map<String, String> collectVotes() {
        Map<String, String> votes = new HashMap<>();
        for (Map.Entry<String, ButtonGroup> entry : positionGroups.entrySet()) {
            ButtonModel selection = entry.getValue().getSelection();
            if (selection != null && !selection.getActionCommand().equals("NONE")) {
                votes.put(entry.getKey(), selection.getActionCommand());
            }
        }
        return votes;
    }

    private void storeVotes(Map<String, String> votes) {
        String updateQuery = "UPDATE candidates SET votes = votes + 1 WHERE student_id = ? AND position = ?";
        try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword)) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(updateQuery)) {
                for (Map.Entry<String, String> entry : votes.entrySet()) {
                    ps.setString(1, entry.getValue());
                    ps.setString(2, entry.getKey());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            conn.commit();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saving votes: " + e.getMessage());
        }
    }
}