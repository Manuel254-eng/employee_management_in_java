// public class Admin {
//     private String username;
//     private String password;

//     // Constructor
//     public Admin(String username, String password) {
//         this.username = username;
//         this.password = password;
//     }

//     // Getters
//     public String getUsername() {
//         return username;
//     }

//     public String getPassword() {
//         return password;
//     }
// }

public class Admin {
    private String username;
    private String password;

    // Constructor
    public Admin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // Validate password
    public boolean validatePassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }
}

