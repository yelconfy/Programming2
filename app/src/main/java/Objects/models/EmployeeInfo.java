package Objects.models;

public class EmployeeInfo extends BaseEmployeeInfo {

    private String LastName;
    private String FirstName;
    private String Birthday;
    private AddressInfo Address;

    public EmployeeInfo(String empNo, String lastName, String firstName, String birthday, AddressInfo address) {
        super(empNo);
        this.LastName = lastName;
        this.FirstName = firstName;
        this.Birthday = birthday;
        this.Address = address;
    }

    // <editor-fold defaultstate="collapsed" desc="Getters">
    public String GetLastName() {
        return LastName;
    }

    public String GetFirstName() {
        return FirstName;
    }

    public String GetBirthday() {
        return Birthday;
    }

    public AddressInfo GetAddress() {
        return Address;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Setters">
    public void SetLastName(String lastName) {
        this.LastName = lastName;
    }

    public void SetFirstName(String firstName) {
        this.FirstName = firstName;
    }

    public void SetBirthday(String birthday) {
        this.Birthday = birthday;
    }

    public void SetAddress(AddressInfo address) {
        this.Address = address;
    }
    // </editor-fold>
}
