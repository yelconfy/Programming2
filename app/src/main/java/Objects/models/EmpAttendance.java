package Objects.models;

import Helper.AmountUtil;
import Helper.DateTimeUtil;
import static Objects.models.EmpAttendance.DISPLAY_FIELDS;
import static Objects.models.EmpDetail.DISPLAY_FIELDS;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

public class EmpAttendance extends EmployeeInfo {

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
            LocalDate birthday,
            AddressInfo address,
            LocalDate bizDate,
            LocalTime logInTime,
            LocalTime logOutTime
    ) {
        super(empNo, lastName, firstName, birthday, address);
        this.BizDate = bizDate;
        this.LogInTime = logInTime;
        this.LogOutTime = logOutTime;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters">
    public LocalDate GetBizDate() {
        return BizDate;
    }

    public LocalTime GetLogInTime() {
        return LogInTime;
    }

    public LocalTime GetLogOutTime() {
        return LogOutTime;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Setters">
    public void SetBizDate(LocalDate bizDate) {
        this.BizDate = bizDate;
    }

    public void SetLogInTime(LocalTime logInTime) {
        this.LogInTime = logInTime;
    }

    public void SetLogOutTime(LocalTime logOutTime) {
        this.LogOutTime = logOutTime;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Utility">
    public static EmpAttendance FromArray(String[] data) {
        if (data.length < 6) {
            throw new IllegalArgumentException("Invalid CSV row: expected 6 fields");
        }

        String empNo = data[0];
        String lastName = data[1];
        String firstName = data[2];

        // If attendance file does NOT have birthday/address, supply defaults.
        LocalDate birthday = null; // or "" if you prefer
        AddressInfo address = null; // or new AddressInfo(empNo) with blanks

        LocalDate bizDate = DateTimeUtil.parseDate(data[3]);
        LocalTime logInTime = DateTimeUtil.parseTime(data[4]);
        LocalTime logOutTime = DateTimeUtil.parseTime(data[5]);

        return new EmpAttendance(
                empNo,
                lastName,
                firstName,
                birthday,
                address,
                bizDate,
                logInTime,
                logOutTime
        );
    }

    public static String[] GetFieldNames() {
        return Arrays.stream(EmpDetail.class.getDeclaredFields())
                .map(Field::getName)
                .toArray(String[]::new);
    }

    public Object GetFieldValue(int index) {
        try {
            Field field = EmpDetail.class.getDeclaredFields()[index];
            field.setAccessible(true);
            return field.get(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object GetDisplayFieldValue(int index) {
        try {
            Field field = GetFieldRecursive(this.getClass(), DISPLAY_FIELDS[index]);
            field.setAccessible(true);
            return field.get(this);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Field GetFieldRecursive(Class<?> clazz, String name) throws NoSuchFieldException {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new NoSuchFieldException(name);
    }
    // </editor-fold>
}
