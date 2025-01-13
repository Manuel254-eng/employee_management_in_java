import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminLoginForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;

    public AdminLoginForm() {
        setTitle("Admin Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // Username Label and Field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(50, 50, 100, 25);
        add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(150, 50, 200, 25);
        add(usernameField);

        // Password Label and Field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 90, 100, 25);
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 90, 200, 25);
        add(passwordField);

        // Login Button
        JButton loginButton = new JButton("Login");
        loginButton.setBounds(150, 130, 100, 25);
        loginButton.addActionListener(new LoginAction());
        add(loginButton);

        // Sign-Up Link
        JLabel signUpLabel = new JLabel("<html><a href=''>Sign Up</a></html>");
        signUpLabel.setBounds(150, 170, 100, 25);
        signUpLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                AdminSignUpForm signUpForm = new AdminSignUpForm();
                signUpForm.setVisible(true);
                dispose(); // Close the login form
            }
        });
        add(signUpLabel);

        // Status Label
        statusLabel = new JLabel();
        statusLabel.setBounds(50, 200, 300, 25);
        add(statusLabel);
    }

    // Action to handle login
    private class LoginAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            // Validate credentials from the database
            if (validateCredentials(username, password)) {
                statusLabel.setText("Login successful!");
                // Open the EmployeeDashboard form
                EmployeeDashboard dashboard = new EmployeeDashboard();
                dashboard.setVisible(true);
                dispose(); // Close the login form
            } else {
                statusLabel.setText("Invalid username or password.");
            }
        }

        // Validate credentials from the database
        private boolean validateCredentials(String username, String password) {
            String query = "SELECT * FROM admins WHERE username = ?";

            try (Connection conn = Database.connect();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, username);

                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    return PasswordUtils.verifyPassword(password, storedPassword);
                } else {
                    return false;
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                statusLabel.setText("Database connection error.");
                return false;
            }
        }
    }

    // Main method to launch the login form
    public static void main(String[] args) {
        AdminLoginForm loginForm = new AdminLoginForm();
        loginForm.setVisible(true);
    }
}
