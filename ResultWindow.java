import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Map;

public class ResultWindow extends JFrame {
    private final String url = "jdbc:mysql://localhost:3306/CollegeElection";
    private final String dbUsername = "root";
    private final String dbPassword = "Diganta7001@MySQL";
    private javax.swing.Timer resultTimer;
    private HomeWindow homeWindow;
    private JLabel timerLabel;

    public ResultWindow(User user, Map<String, String> votes) {
        setTitle("Your Votes - " + user.getStudentName());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

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
        setContentPane(mainPanel);


        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));


        JLabel title = new JLabel("YOUR VOTING RESULTS");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(new Color(200, 230, 255));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        contentPanel.add(title);


        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setOpaque(false);
        resultsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (votes.isEmpty()) {
            JLabel noVotesLabel = new JLabel("No votes recorded");
            noVotesLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            noVotesLabel.setForeground(Color.WHITE);
            resultsPanel.add(noVotesLabel);
        } else {
            for (Map.Entry<String, String> entry : votes.entrySet()) {
                String candidateName = getCandidateDetails(entry.getValue(), entry.getKey());
                JLabel voteLabel = new JLabel(
                        "<html><b style='color:#88C8FF; font-size:16px'>" + entry.getKey() +
                                ":</b> <span style='color:white; font-size:14px'>" + candidateName + "</span></html>"
                );
                voteLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
                resultsPanel.add(voteLabel);
            }
        }

        contentPanel.add(resultsPanel);
        contentPanel.add(Box.createVerticalStrut(40));


        timerLabel = new JLabel("Window closing in 10 seconds...");
        timerLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        timerLabel.setForeground(new Color(255, 255, 255, 180));
        contentPanel.add(timerLabel);

        mainPanel.add(contentPanel);
    }

    private String getCandidateDetails(String studentId, String position) {
        String query = "SELECT c.candidate_name, c.student_id " +
                "FROM candidates c " +
                "WHERE c.student_id = ? AND c.position = ?";

        try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, studentId);
            ps.setString(2, position);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("candidate_name") + " (" + rs.getString("student_id") + ")";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown Candidate";
    }

    public void setHomeWindowReference(HomeWindow homeWindow) {
        this.homeWindow = homeWindow;
    }

    public void startResultTimer() {
        resultTimer = new javax.swing.Timer(1000, e -> handleTimer());
        resultTimer.start();
    }

    private void handleTimer() {
        String text = timerLabel.getText().replaceAll("\\D+", "");
        int seconds = text.isEmpty() ? 10 : Integer.parseInt(text) - 1; // Changed from 30 to 10

        if (seconds <= 0) {
            resultTimer.stop();
            if (homeWindow != null) homeWindow.dispose();
            dispose();
            new Login().setVisible(true);
        } else {
            timerLabel.setText("Window closing in " + seconds + " seconds...");
        }
    }
}