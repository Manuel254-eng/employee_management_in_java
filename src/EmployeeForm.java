import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EmployeeForm extends JFrame {
    private JTextField nameField, contactField, positionField, departmentField;
    private JLabel statusLabel;

    public EmployeeForm() {
        setTitle("Employee Onboarding");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // Full Name
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setBounds(50, 30, 100, 25);
        add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(150, 30, 200, 25);
        add(nameField);

        // Contact
        JLabel contactLabel = new JLabel("Contact:");
        contactLabel.setBounds(50, 70, 100, 25);
        add(contactLabel);

        contactField = new JTextField();
        contactField.setBounds(150, 70, 200, 25);
        add(contactField);

        // Position
        JLabel positionLabel = new JLabel("Position:");
        positionLabel.setBounds(50, 110, 100, 25);
        add(positionLabel);

        positionField = new JTextField();
        positionField.setBounds(150, 110, 200, 25);
        add(positionField);

        // Department
        JLabel departmentLabel = new JLabel("Department:");
        departmentLabel.setBounds(50, 150, 100, 25);
        add(departmentLabel);

        departmentField = new JTextField();
        departmentField.setBounds(150, 150, 200, 25);
        add(departmentField);

        // Add Button
        JButton addButton = new JButton("Add Employee");
        addButton.setBounds(150, 190, 150, 25);
        addButton.addActionListener(new AddEmployeeAction());
        add(addButton);

        // Status Label
        statusLabel = new JLabel();
        statusLabel.setBounds(50, 230, 400, 25);
        add(statusLabel);
    }

    // Action to add employee
    private class AddEmployeeAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = nameField.getText();
            String contact = contactField.getText();
            String position = positionField.getText();
            String department = departmentField.getText();

            Employee employee = new Employee(name, contact, position, department);
            boolean success = Database.addEmployee(employee);

            if (success) {
                statusLabel.setText("Employee added successfully!");
            } else {
                statusLabel.setText("Failed to add employee.");
            }
        }
    }
}
