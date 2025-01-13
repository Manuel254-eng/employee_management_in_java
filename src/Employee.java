public class Employee {
    private String name;
    private String contact;
    private String position;
    private String department;
    private String status;

    // Constructor
    public Employee(String name, String contact, String position, String department) {
        this.name = name;
        this.contact = contact;
        this.position = position;
        this.department = department;
        this.status = "Pending Verification"; // Default status
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public String getContact() {
        return contact;
    }

    public String getPosition() {
        return position;
    }

    public String getDepartment() {
        return department;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
