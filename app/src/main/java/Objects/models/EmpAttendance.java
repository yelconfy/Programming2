package Objects.models;

import Helper.AmountUtil;
import Helper.DateTimeUtil;
import static Objects.models.EmpAttendance.DISPLAY_FIELDS;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

public class EmpAttendance {

    private String EmpNo;
    private String LastName;
    private String FirstName;
    private LocalDate BizDate;
    private LocalTime LogInTime;
    private LocalTime LogOutTime;

    public static final String[] DISPLAY_FIELDS = {
        "EmpNo",
        "LastName",
        "FirstName",
        "BizDate",
        "LogInTime",
        "LogOutTime"
    };

    // <editor-fold defaultstate="collapsed" desc="Constructor">
    public EmpAttendance(
            String empNo,
            String lastName,
            String firstName,
            LocalDate bizDate,
            LocalTime logInTime,
            LocalTime logOutTime
    ) {
        this.EmpNo = empNo;
        this.LastName = lastName;
        this.FirstName = firstName;
        this.BizDate = bizDate;
        this.LogInTime = logInTime;
        this.LogOutTime = logOutTime;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters">
    public String getEmpNo() {
        return EmpNo;
    }

    public String getLastName() {
        return LastName;
    }

    public String getFirstName() {
        return FirstName;
    }

    public LocalDate getBizDate() {
        return BizDate;
    }

    public LocalTime getLogInTime() {
        return LogInTime;
    }

    public LocalTime getLogOutTime() {
        return LogOutTime;
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Setters">
    public void setEmpNo(String empNo) {
        this.EmpNo = empNo;
    }

    public void setLastName(String lastName) {
        this.LastName = lastName;
    }

    public void setFirstName(String firstName) {
        this.FirstName = firstName;
    }

    public void setBizDate(LocalDate bizDate) {
        this.BizDate = bizDate;
    }

    public void setLogInTime(LocalTime logInTime) {
        this.LogInTime = logInTime;
    }

    public void setLogOutTime(LocalTime logOutTime) {
        this.LogOutTime = logOutTime;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Utility">
    public static EmpAttendance fromArray(String[] data) {
        if (data.length < 6) {
            throw new IllegalArgumentException("Invalid CSV row");
        }
        return new EmpAttendance(
                data[0],
                data[1],
                data[2],
                DateTimeUtil.parseDate(data[3]),
                DateTimeUtil.parseTime(data[4]),
                DateTimeUtil.parseTime(data[5])
        );
    }

    // Get field names dynamically
    public static String[] GetFieldNames() {
        return Arrays.stream(EmpAttendance.class.getDeclaredFields())
                .map(Field::getName)
                .toArray(String[]::new);
    }

    // Get values dynamically
    public Object GetFieldValue(int index) {
        try {
            Field field = EmpAttendance.class.getDeclaredFields()[index];
            field.setAccessible(true);
            return field.get(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object getDisplayFieldValue(int index) {
        try {
            Field field = EmpAttendance.class.getDeclaredField(DISPLAY_FIELDS[index]);
            field.setAccessible(true);
            return field.get(this);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    // </editor-fold>
}
