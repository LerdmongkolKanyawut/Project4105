package model;

/**
 * Model: users table
 */
public class User {

    private int userId;
    private String username;
    private String password;   // hashed
    private String firstname;
    private String lastname;
    private String email;
    private String role;       // Admin | อาจารย์ | นักศึกษา | บุคคลทั่วไป
    private String avatar;     // base64 หรือ path

    public User() {
    }

    // Getters & Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int v) {
        this.userId = v;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String v) {
        this.username = v;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String v) {
        this.password = v;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String v) {
        this.firstname = v;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String v) {
        this.lastname = v;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String v) {
        this.email = v;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String v) {
        this.role = v;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String v) {
        this.avatar = v;
    }
}
