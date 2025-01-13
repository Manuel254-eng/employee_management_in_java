import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

public class AdminSignUpForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;

    public AdminSignUpForm() {
        setTitle("Admin Sign-Up");
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

        // Sign-Up Button
        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setBounds(150, 130, 100, 25);
        signUpButton.addActionListener(new SignUpAction());
        add(signUpButton);

        // Status Label
        statusLabel = new JLabel();
        statusLabel.setBounds(50, 170, 300, 25);
        add(statusLabel);
    }

    // Action to handle sign-up
    private class SignUpAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            // Hash the password before storing
            String hashedPassword = PasswordUtils.hashPassword(password);

            // Store the new admin in the database
            try {
                if (storeAdmin(username, hashedPassword)) {
                    statusLabel.setText("Sign-Up successful!");
                    // Redirect to login form
                    AdminLoginForm loginForm = new AdminLoginForm();
                    loginForm.setVisible(true);
                    dispose(); // Close the sign-up form
                } else {
                    statusLabel.setText("Error during sign-up.");
                }
            } catch (SQLIntegrityConstraintViolationException ex) {
                statusLabel.setText("Username already exists.");
            } catch (SQLException ex) {
                statusLabel.setText("Database error: " + ex.getMessage());
            }
        }

        // Store the new admin in the database
        private boolean storeAdmin(String username, String hashedPassword) throws SQLException {
            String query = "INSERT INTO admins (username, password) VALUES (?, ?)";

            try (Connection conn = Database.connect();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, username);
                stmt.setString(2, hashedPassword);

                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            } catch (SQLIntegrityConstraintViolationException ex) {
                throw ex; // Rethrow to be caught in the actionPerformed method
            } catch (SQLException ex) {
                throw ex; // Rethrow to be caught in the actionPerformed method
            }
        }
    }

    // Main method to launch the sign-up form
    public static void main(String[] args) {
        AdminSignUpForm signUpForm = new AdminSignUpForm();
        signUpForm.setVisible(true);
    }
}
