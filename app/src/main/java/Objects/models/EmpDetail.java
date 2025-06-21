package Objects.models;

import Helper.AmountUtil;
import Helper.DateTimeUtil;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Arrays;

public class EmpDetail extends EmployeeInfo {

    private String Email;
    private String PhoneNo;
    private String SssNo;
    private String PhilHealthNo;
    private String TinNo;
    private String PagIbigNo;
    private String Position;
    private String ImmSupervisor;
    private String EmpStatus;
    private CompensationInfo Compensation;
    private LocalDate DateHired;

    public static final String[] DISPLAY_FIELDS = {
        "EmpNo",
        "LastName",
        "FirstName",
        "Position"
    };

    // <editor-fold defaultstate="collapsed" desc="Constructor">
    public EmpDetail(
            String empNo,
            String lastName,
            String firstName,
            LocalDate birthday,
            AddressInfo address,
            String email,
            String phoneNo,
            String sssNo,
            String philHealthNo,
            String tinNo,
            String pagIbigNo,
            String empStatus,
            String position,
            String immSupervisor,
            CompensationInfo compensation,
            LocalDate dateHired,
            boolean status
    ) {
        super(empNo, lastName, firstName, birthday, address);
        this.Email = email;
        this.PhoneNo = phoneNo;
        this.SssNo = sssNo;
        this.PhilHealthNo = philHealthNo;
        this.TinNo = tinNo;
        this.PagIbigNo = pagIbigNo;
        this.EmpStatus = empStatus;
        this.Position = position;
        this.ImmSupervisor = immSupervisor;
        this.Compensation = compensation;
        this.DateHired = dateHired;
        this.SetStatus(status);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters">
    public String GetEmail() {
        return Email;
    }

    public String GetPhoneNo() {
        return PhoneNo;
    }

    public String GetSssNo() {
        return SssNo;
    }

    public String GetPhilHealthNo() {
        return PhilHealthNo;
    }

    public String GetTinNo() {
        return TinNo;
    }

    public String GetPagIbigNo() {
        return PagIbigNo;
    }

    public String GetPosition() {
        return Position;
    }

    public String GetImmSupervisor() {
        return ImmSupervisor;
    }

    public String GetEmpStatus() {
        return EmpStatus;
    }

    public CompensationInfo GetCompensation() {
        return Compensation;
    }

    public LocalDate GetDateHired() {
        return DateHired;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Setters">
    public void SetEmail(String email) {
        this.Email = email;
    }

    public void SetPhoneNo(String phoneNo) {
        this.PhoneNo = phoneNo;
    }

    public void SetSssNo(String sssNo) {
        this.SssNo = sssNo;
    }

    public void SetPhilHealthNo(String philHealthNo) {
        this.PhilHealthNo = philHealthNo;
    }

    public void SetTinNo(String tinNo) {
        this.TinNo = tinNo;
    }

    public void SetPagIbigNo(String pagIbigNo) {
        this.PagIbigNo = pagIbigNo;
    }

    public void SetPosition(String position) {
        this.Position = position;
    }

    public void SetImmSupervisor(String immSupervisor) {
        this.ImmSupervisor = immSupervisor;
    }

    public void SetEmpStatus(String empStatus) {
        this.EmpStatus = empStatus;
    }

    public void SetCompensation(CompensationInfo compensation) {
        this.Compensation = compensation;
    }

    public void SetDateHired(LocalDate dateHired) {
        this.DateHired = dateHired;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="FromArray (27 columns)">
    public static EmpDetail FromArray(String[] data) {
        if (data.length < 27) {
            throw new IllegalArgumentException("Invalid CSV row: expected 27 fields");
        }

        // Personal Details
        String empNo = data[0];
        String lastName = data[1];
        String firstName = data[2];
        LocalDate birthday = DateTimeUtil.parseDate(data[3]);
        String email = data[4];
        String phoneNo = data[5];

        // Address Details
        AddressInfo address = new AddressInfo(empNo);
        address.SetHouseBlkLotNo(data[6]);
        address.SetStreet(data[7]);
        address.SetBarangay(data[8]);
        address.SetCityMunicipality(data[9]);
        address.SetProvince(data[10]);
        address.SetZipCode(data[11]);

        // Statutory Details
        String sssNo = data[12];
        String philHealthNo = data[13];
        String tinNo = data[14];
        String pagIbigNo = data[15];

        // Employement Details
        String empStatus = data[16];
        String position = data[17];
        String immSupervisor = data[18];
        LocalDate dateHired = DateTimeUtil.parseDate(data[19]);

        // Compensation Details
        CompensationInfo compensation = new CompensationInfo(empNo);
        compensation.SetBasicSalary(AmountUtil.ParseFormattedStringToDouble(data[20]));
        compensation.SetRiceSubsidy(AmountUtil.ParseFormattedStringToDouble(data[21]));
        compensation.SetPhoneAllowance(AmountUtil.ParseFormattedStringToDouble(data[22]));
        compensation.SetClothingAllowance(AmountUtil.ParseFormattedStringToDouble(data[23]));
        compensation.SetGrossSemiMonthlyRate(AmountUtil.ParseFormattedStringToDouble(data[24]));
        compensation.SetHourlyRate(AmountUtil.ParseFormattedStringToDouble(data[25]));

        // Status
        boolean status = Boolean.parseBoolean(data[26]);

        // Create and set status
        return new EmpDetail(
                empNo,
                lastName,
                firstName,
                birthday,
                address,
                email,
                phoneNo,
                sssNo,
                philHealthNo,
                tinNo,
                pagIbigNo,
                empStatus,
                position,
                immSupervisor,
                compensation,
                dateHired,
                status
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Reflection Utility">
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
