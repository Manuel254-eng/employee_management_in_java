import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class EmployeeDashboard extends JFrame {

    private JTable employeeTable;
    private JScrollPane scrollPane;
    private JButton addEmployeeButton;
    private DefaultTableModel model;

    public EmployeeDashboard() {
        setTitle("Employee Dashboard");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create JTable to display employee data
        String[] columnNames = {"ID", "Full Name", "Contact", "Position", "Department", "Status", "Edit", "Delete"};
        model = new DefaultTableModel(columnNames, 0);

        // Fetch employee data from the database and populate the table
        fetchEmployeeData();

        employeeTable = new JTable(model);
        scrollPane = new JScrollPane(employeeTable);

        // Create button columns for "Edit" and "Delete"
        TableColumn editColumn = employeeTable.getColumnModel().getColumn(6);  // "Edit" column
        editColumn.setCellRenderer(new ButtonRenderer("Edit"));
        editColumn.setCellEditor(new ButtonEditor(new JCheckBox(), "Edit"));

        TableColumn deleteColumn = employeeTable.getColumnModel().getColumn(7);  // "Delete" column
        deleteColumn.setCellRenderer(new ButtonRenderer("Delete"));
        deleteColumn.setCellEditor(new ButtonEditor(new JCheckBox(), "Delete"));

        add(scrollPane, BorderLayout.CENTER);

        // Add employee button
        addEmployeeButton = new JButton("Add Employee");
        addEmployeeButton.setBounds(50, 300, 150, 30);
        addEmployeeButton.addActionListener(e -> {
            EmployeeAddForm addForm = new EmployeeAddForm(this);
            addForm.setVisible(true);
        });
        add(addEmployeeButton, BorderLayout.SOUTH);
    }

    // Fetch employees from the database
    public void fetchEmployeeData() {
        model.setRowCount(0);  // Clear existing data in the table
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // Set up connection to the database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/employee_management", "root", "");

            // Create SQL query to fetch employee details
            String query = "SELECT * FROM employees";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            // Add data to the table model
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("contact"),
                    rs.getString("position"),
                    rs.getString("department"),
                    rs.getString("status"),
                    "Edit", 
                    "Delete" // Placeholder for "Delete" button
                };
                model.addRow(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching employee data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        EmployeeDashboard dashboard = new EmployeeDashboard();
        dashboard.setVisible(true);
    }

    // Custom button renderer for "Edit" and "Delete" buttons
    class ButtonRenderer extends JButton implements TableCellRenderer {
        private String action;

        public ButtonRenderer(String action) {
            this.action = action;
            setText(action);
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                        boolean hasFocus, int row, int column) {
            return this;
        }
    }

    // Custom button editor for "Edit" and "Delete" actions
    class ButtonEditor extends DefaultCellEditor {
        private String action;

        public ButtonEditor(JCheckBox checkBox, String action) {
            super(checkBox);
            this.action = action;
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                                                     int column) {
            JButton button = new JButton(action);
            button.addActionListener(e -> {
                if (action.equals("Edit")) {
                    int employeeId = (int) table.getValueAt(row, 0);
                    EmployeeEditForm editForm = new EmployeeEditForm(EmployeeDashboard.this, employeeId);
                    editForm.setVisible(true);
                } else if (action.equals("Delete")) {
                    int employeeId = (int) table.getValueAt(row, 0);
                    String status = (String) table.getValueAt(row, 5); // Get the status of the employee

                    // Open confirmation dialog
                    int confirmed = JOptionPane.showConfirmDialog(EmployeeDashboard.this,
                            "Are you sure you want to delete this employee?",
                            "Delete Employee", JOptionPane.YES_NO_OPTION);

                    if (confirmed == JOptionPane.YES_OPTION) {
                        // Disable the proceed button if employee status is "Verified"
                        if ("Verified".equals(status)) {
                            JOptionPane.showMessageDialog(EmployeeDashboard.this, "Employee is verified, cannot delete.");
                        } else {
                            deleteEmployee(employeeId);  // Proceed with deletion
                            fetchEmployeeData();  // Refresh data after deletion
                        }
                    }
                }
            });
            return button;
        }

        public Object getCellEditorValue() {
            return action;
        }
    }

    // Delete employee logic
    private void deleteEmployee(int employeeId) {
        Connection conn = null;
        PreparedStatement pstmtDelete = null;
        PreparedStatement pstmtHistory = null;
        ResultSet rs = null;
    
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/employee_management", "root", "");
    
            // Fetch the employee details to insert them into employee_history before deleting
            String fetchQuery = "SELECT * FROM employees WHERE id = ?";
            pstmtDelete = conn.prepareStatement(fetchQuery);
            pstmtDelete.setInt(1, employeeId);
            rs = pstmtDelete.executeQuery();
    
            if (rs.next()) {
                // Insert the employee data into employee_history
                String historyQuery = "INSERT INTO employee_history (id, name, contact, position, department, status, deleted_at, deleted_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                pstmtHistory = conn.prepareStatement(historyQuery);
                pstmtHistory.setInt(1, rs.getInt("id"));
                pstmtHistory.setString(2, rs.getString("name"));
                pstmtHistory.setString(3, rs.getString("contact"));
                pstmtHistory.setString(4, rs.getString("position"));
                pstmtHistory.setString(5, rs.getString("department"));
                pstmtHistory.setString(6, rs.getString("status"));
                pstmtHistory.setTimestamp(7, new Timestamp(System.currentTimeMillis())); // Set current timestamp
                pstmtHistory.setString(8, "deleted");
    
                pstmtHistory.executeUpdate();
            }
    
            // Delete the employee from the employees table
            String deleteQuery = "DELETE FROM employees WHERE id = ?";
            pstmtDelete = conn.prepareStatement(deleteQuery);
            pstmtDelete.setInt(1, employeeId);
            int rowsDeleted = pstmtDelete.executeUpdate();
    
            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Employee deleted successfully.");
            }
    
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting employee: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmtDelete != null) pstmtDelete.close();
                if (pstmtHistory != null) pstmtHistory.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
}
