import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/employee_management";
    private static final String USER = "root"; 
    private static final String PASSWORD = ""; 
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    static {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Driver not found!");
        }
    }

    // Add a new employee
    public static boolean addEmployee(Employee employee) {
        String query = "INSERT INTO employees (name, contact, position, department) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, employee.getName());
            stmt.setString(2, employee.getContact());
            stmt.setString(3, employee.getPosition());
            stmt.setString(4, employee.getDepartment());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding employee: " + e.getMessage());
            return false;
        }
    }

    // Fetch employees by status
    public static List<Employee> getEmployees(String status) {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT * FROM employees WHERE status = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Employee employee = new Employee(
                    rs.getString("name"),
                    rs.getString("contact"),
                    rs.getString("position"),
                    rs.getString("department")
                );
                employee.setStatus(rs.getString("status"));
                employees.add(employee);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching employees: " + e.getMessage());
        }
        return employees;
    }
}
