# Employee Management System

This is an Employee Management System built with Java Swing for the front end and MySQL for the database. The system allows users to manage employees, including adding, editing, and deleting employee records. It also tracks changes by storing historical data in the `employee_history` table for auditing purposes.

## Features

- **Employee Dashboard**: View employee data in a table format.
- **Add Employee**: Add new employees to the system.
- **Edit Employee**: Modify existing employee records, with historical data tracking.
- **Delete Employee**: Delete employee records, with historical data tracking and a confirmation dialog.
- **Employee History**: Tracks changes (deletion or editing) with timestamp and status (`deleted` or `edited`).
- **Verification Check**: Employees marked as `Verified` cannot be edited or deleted.

## Prerequisites

Before running the application, ensure that you have the following installed:

- **Java 8+** (for running the application)
- **MySQL Database** (for storing employee data)

## Setting Up the Database

### Step 1: Create Database
use the database.sql file to create a db
### Step 2: Configure Database Connection
Ensure that you update the database connection in your Java application (EmployeeDashboard.java):
Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/employee_management", "root", "");
update the username (root) and password

### Run the Application
1. Run the Application

Execute the Main.java file to launch the application.

2. Access the Sign-Up Form

Upon launching, the application will display the admin login page.

To create a new administrator account, click on the "Sign Up" link located below the login form.

3. Complete the Sign-Up Form

In the sign-up form, enter the following details:

Username: Choose a unique username for the new administrator account.

Password: Create a secure password for the account.

After filling in the required information, click the "Sign Up" button to submit the form.

4. Log In with New Credentials

After successfully creating the new administrator account, return to the login page.

Enter the username and password you just set up, then click the "Login"

After logging in, you will have access to the Employee Management System features.

Employee Dashboard
The main window displays a table containing the list of employees. It has the following columns:

ID: Unique identifier for each employee.
Full Name: The name of the employee.
Contact: Contact number of the employee.
Position: Employee's job title.
Department: The department the employee belongs to.
Status: Current status of the employee (e.g., Verified, Active).
Edit: A button to edit the employee's details.
Delete: A button to delete the employee record.
Add Employee
Click on the "Add Employee" button to open a form where you can input the details of a new employee and save them to the database.

Edit Employee
Click the "Edit" button next to an employee's name to modify their details. Before making changes, the system checks if the employee is verified. If verified, the employee's details cannot be edited. To test this simply change status in the edit form to Verified

Delete Employee
Click the "Delete" button next to an employee's name. A confirmation popup will appear, asking if you're sure you want to delete the employee. If the employee is not verified, the record will be deleted, and the previous details will be stored in the employee_history table with a status of deleted.

