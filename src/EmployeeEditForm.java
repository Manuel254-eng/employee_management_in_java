import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class EmployeeEditForm extends JFrame {

    private JTextField nameField, contactField, positionField, departmentField, statusField;
    private JButton saveButton, cancelButton;
    private int employeeId; // Store the employee ID for updating
    private String employeeStatus; // Store the status of the employee

    public EmployeeEditForm(EmployeeDashboard dashboard, int employeeId) {
        this.employeeId = employeeId;

        setTitle("Edit Employee");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(6, 2));

        // Initialize form fields
        JLabel nameLabel = new JLabel("Full Name:");
        nameField = new JTextField();

        JLabel contactLabel = new JLabel("Contact:");
        contactField = new JTextField();

        JLabel positionLabel = new JLabel("Position:");
        positionField = new JTextField();

        JLabel departmentLabel = new JLabel("Department:");
        departmentField = new JTextField();

        JLabel statusLabel = new JLabel("Status:");
        statusField = new JTextField();

        // Save Button
        saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateEmployeeDetails();
                dashboard.fetchEmployeeData(); // Refresh the table after saving
                dispose(); // Close the edit form
            }
        });

        // Cancel Button
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose()); // Close the form without saving

        // Add components to the form
        add(nameLabel);
        add(nameField);
        add(contactLabel);
        add(contactField);
        add(positionLabel);
        add(positionField);
        add(departmentLabel);
        add(departmentField);
        add(statusLabel);
        add(statusField);
        add(saveButton);
        add(cancelButton);

        // Fetch the current employee details to populate the form
        fetchEmployeeDetails();
    }

    // Fetch the current employee details from the database
    private void fetchEmployeeDetails() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/employee_management", "root", "");
            String query = "SELECT * FROM employees WHERE id = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, employeeId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                // Set the values in the form fields
                nameField.setText(rs.getString("name"));
                contactField.setText(rs.getString("contact"));
                positionField.setText(rs.getString("position"));
                departmentField.setText(rs.getString("department"));
                statusField.setText(rs.getString("status"));
                employeeStatus = rs.getString("status"); // Store the employee status

                // Disable editing if the status is "Verified"
                if ("Verified".equals(employeeStatus)) {
                    nameField.setEditable(false);
                    contactField.setEditable(false);
                    positionField.setEditable(false);
                    departmentField.setEditable(false);
                    statusField.setEditable(false);
                    saveButton.setEnabled(false);
                    cancelButton.setEnabled(false);
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching employee details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Update employee details in the database
    private void updateEmployeeDetails() {
        if ("Verified".equals(employeeStatus)) {
            JOptionPane.showMessageDialog(this, "This employee is verified and cannot be edited.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        Connection conn = null;
        PreparedStatement pstmtUpdate = null;
        PreparedStatement pstmtHistory = null;
    
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/employee_management", "root", "");
    
            // Fetch the current employee details before editing
            String fetchQuery = "SELECT * FROM employees WHERE id = ?";
            pstmtUpdate = conn.prepareStatement(fetchQuery);
            pstmtUpdate.setInt(1, employeeId);
            ResultSet rs = pstmtUpdate.executeQuery();
    
            if (rs.next()) {
                // Insert the current employee data into employee_history with status 'edited'
                String historyQuery = "INSERT INTO employee_history (id, name, contact, position, department, status, deleted_at, deleted_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                pstmtHistory = conn.prepareStatement(historyQuery);
                pstmtHistory.setInt(1, rs.getInt("id"));
                pstmtHistory.setString(2, rs.getString("name"));
                pstmtHistory.setString(3, rs.getString("contact"));
                pstmtHistory.setString(4, rs.getString("position"));
                pstmtHistory.setString(5, rs.getString("department"));
                pstmtHistory.setString(6, rs.getString("status"));
                pstmtHistory.setTimestamp(7, new Timestamp(System.currentTimeMillis())); // Set current timestamp
                pstmtHistory.setString(8, "edited");
    
                pstmtHistory.executeUpdate();
    
                // Now, update the employee data
                String updateQuery = "UPDATE employees SET name = ?, contact = ?, position = ?, department = ?, status = ? WHERE id = ?";
                pstmtUpdate = conn.prepareStatement(updateQuery);
                pstmtUpdate.setString(1, nameField.getText());
                pstmtUpdate.setString(2, contactField.getText());
                pstmtUpdate.setString(3, positionField.getText());
                pstmtUpdate.setString(4, departmentField.getText());
                pstmtUpdate.setString(5, statusField.getText());
                pstmtUpdate.setInt(6, employeeId);
    
                int rowsUpdated = pstmtUpdate.executeUpdate();
    
                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(this, "Employee details updated successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update employee details.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
    
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating employee details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (pstmtUpdate != null) pstmtUpdate.close();
                if (pstmtHistory != null) pstmtHistory.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    

    public static void main(String[] args) {
        // For testing purposes, pass an employee ID (replace with actual ID)
        EmployeeEditForm editForm = new EmployeeEditForm(new EmployeeDashboard(), 1);
        editForm.setVisible(true);
    }
}
