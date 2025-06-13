
package Objects.models;

public class Credentials {
    String UserName;
    String Password;
    String DeptCode;
    String FirstName;
    String LastName;
    
    // Constructor
    public Credentials(String userName, String password, String deptCode, String firstName, String lastName) {
        this.UserName = userName;
        this.Password = password;
        this.DeptCode = deptCode;
        this.FirstName = firstName;
        this.LastName = lastName;
    }

    public static Credentials fromArray(String[] data) {
        if (data.length < 5) throw new IllegalArgumentException("Invalid CSV row");
        return new Credentials(data[0], data[1], data[2], data[3], data[4]);
    }
    
    // Getters
    public String getUserName() {
        return UserName;
    }

    public String getPassword() {
        return Password;
    }

    public String getDeptCode() {
        return DeptCode;
    }

    public String getFirstName() {
        return FirstName;
    }

    public String getLastName() {
        return LastName;
    }

    // Setters
    public void setUserName(String userName) {
        this.UserName = userName;
    }

    public void setPassword(String password) {
        this.Password = password;
    }

    public void setDeptCode(String deptCode) {
        this.DeptCode = deptCode;
    }

    public void setFirstName(String firstName) {
        this.FirstName = firstName;
    }

    public void setLastName(String lastName) {
        this.LastName = lastName;
    }
}

