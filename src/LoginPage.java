import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private Connection connection;

    public LoginPage() {
        setTitle("Login Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
        setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        panel.setBackground(new Color(240, 240, 240));
        panel.setLayout(new GridLayout(4, 1, 10, 10));

        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");

        Font labelFont = new Font("Arial", Font.BOLD, 14);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(labelFont);
        panel.add(usernameLabel);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(labelFont);
        panel.add(passwordLabel);
        panel.add(passwordField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        loginButton.setFont(labelFont);
        loginButton.setBackground(new Color(50, 120, 200));
        loginButton.setForeground(Color.WHITE);
        buttonPanel.add(loginButton);

        registerButton.setFont(labelFont);
        registerButton.setBackground(new Color(0, 100, 0));
        registerButton.setForeground(Color.WHITE);
        buttonPanel.add(registerButton);

        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                char[] passwordChars = passwordField.getPassword();
                String password = new String(passwordChars);

                if (authenticateUser(username, password)) {
                    JOptionPane.showMessageDialog(null, "Login successful");
                    dispose();

                    SwingUtilities.invokeLater(() -> {
                        new ChartExample();
                    });
                } else {
                    JOptionPane.showMessageDialog(null, "Login failed");
                }
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog registrationDialog = new JDialog();
                registrationDialog.setTitle("Register");
                registrationDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                registrationDialog.setSize(300, 150);
                registrationDialog.setLayout(new GridLayout(3, 2, 10, 10));

                JTextField newUsernameField = new JTextField(20);
                JPasswordField newPasswordField = new JPasswordField(20);
                JButton registerButtonDialog = new JButton("Register");

                registrationDialog.add(new JLabel("New Username:"));
                registrationDialog.add(newUsernameField);
                registrationDialog.add(new JLabel("New Password:"));
                registrationDialog.add(newPasswordField);

                registerButtonDialog.setFont(labelFont);
                registerButtonDialog.setBackground(new Color(0, 100, 0));
                registerButtonDialog.setForeground(Color.WHITE);
                registrationDialog.add(registerButtonDialog);

                registerButtonDialog.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String newUsername = newUsernameField.getText();
                        char[] newPasswordChars = newPasswordField.getPassword();
                        String newPassword = new String(newPasswordChars);

                        if (registerUser(newUsername, newPassword)) {
                            JOptionPane.showMessageDialog(null, "Registration complete");
                            registrationDialog.dispose();
                        } else {
                            JOptionPane.showMessageDialog(null, "Registration failed");
                        }
                    }
                });

                registrationDialog.setVisible(true);
            }
        });
    }

    private boolean authenticateUser(String username, String password) {

        String dbUrl = "jdbc:mysql://localhost:3306/miniproject";
        String dbUser = "root";
        String dbPassword = "Pranav@2004";

        try {
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

            String query = "SELECT * FROM login WHERE username = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return false;
    }

    private boolean registerUser(String username, String password) {

        String dbUrl = "jdbc:mysql://localhost:3306/miniproject";
        String dbUser = "root";
        String dbPassword = "Pranav@2004";

        try {
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

            String query = "INSERT INTO login (username, password) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginPage loginPage = new LoginPage();
            loginPage.setVisible(true);
        });
    }
}

