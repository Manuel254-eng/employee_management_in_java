import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class EmployeeAddForm extends JFrame {

    private JTextField nameField, contactField, positionField, departmentField;
    private JButton submitButton;
    private EmployeeDashboard dashboard;  // Reference to EmployeeDashboard

    // Constructor accepting EmployeeDashboard reference
    public EmployeeAddForm(EmployeeDashboard dashboard) {
        this.dashboard = dashboard;  // Set the reference to the dashboard
        setTitle("Add Employee");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(6, 2, 10, 10));

        // Add form fields
        add(new JLabel("Full Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Contact:"));
        contactField = new JTextField();
        add(contactField);

        add(new JLabel("Position:"));
        positionField = new JTextField();
        add(positionField);

        add(new JLabel("Department:"));
        departmentField = new JTextField();
        add(departmentField);

        // Submit Button
        submitButton = new JButton("Add Employee");
        submitButton.addActionListener(new SubmitAction());
        add(submitButton);
    }

    // Action to handle form submission
    private class SubmitAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = nameField.getText();
            String contact = contactField.getText();
            String position = positionField.getText();
            String department = departmentField.getText();

            if (name.isEmpty() || contact.isEmpty() || position.isEmpty() || department.isEmpty()) {
                JOptionPane.showMessageDialog(EmployeeAddForm.this, "All fields are required", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                // Add employee to the database
                addEmployeeToDatabase(name, contact, position, department);
            }
        }
    }

    // Add employee to the database
    private void addEmployeeToDatabase(String name, String contact, String position, String department) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            // Set up connection to the database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/employee_management", "root", "");

            // SQL query to insert the new employee
            String query = "INSERT INTO employees (name, contact, position, department, status) VALUES (?, ?, ?, ?, 'Pending Verification')";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, contact);
            stmt.setString(3, position);
            stmt.setString(4, department);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Employee added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Close the add form

                // Refresh the employee table in the dashboard
                dashboard.fetchEmployeeData(); // Refresh data in the table
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add employee.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding employee: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        EmployeeDashboard dashboard = new EmployeeDashboard(); // Create dashboard instance
        EmployeeAddForm addForm = new EmployeeAddForm(dashboard); // Pass dashboard reference
        addForm.setVisible(true);
    }
}
