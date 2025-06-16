package Objects.models;

import Helper.AmountUtil;
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
            String birthday,
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
            LocalDate dateHired
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

        // Base info
        String empNo = data[0];
        String lastName = data[1];
        String firstName = data[2];
        String birthday = data[3];

        // Address info
        AddressInfo address = new AddressInfo(empNo);
        address.SetHouseBlkLotNo(data[4]);
        address.SetStreet(data[5]);
        address.SetBarangay(data[6]);
        address.SetCityMunicipality(data[7]);
        address.SetProvince(data[8]);
        address.SetZipCode(data[9]);

        // Contact & IDs
        String phoneNo = data[10];
        String email = data[11];
        String sssNo = data[12];
        String philHealthNo = data[13];
        String tinNo = data[14];
        String pagIbigNo = data[15];
        String empStatus = data[16];
        String position = data[17];
        String immSupervisor = data[18];

        // Compensation
        CompensationInfo compensation = new CompensationInfo(empNo);
        compensation.SetBasicSalary(AmountUtil.ParseFormattedDouble(data[19]));
        compensation.SetRiceSubsidy(AmountUtil.ParseFormattedDouble(data[20]));
        compensation.SetPhoneAllowance(AmountUtil.ParseFormattedDouble(data[21]));
        compensation.SetClothingAllowance(AmountUtil.ParseFormattedDouble(data[22]));
        compensation.SetGrossSemiMonthlyRate(AmountUtil.ParseFormattedDouble(data[23]));
        compensation.SetHourlyRate(AmountUtil.ParseFormattedDouble(data[24]));

        // Date hired
        LocalDate dateHired = LocalDate.parse(data[25]);

        // Status
        boolean status = Boolean.parseBoolean(data[26]);

        // Create and set status
        EmpDetail emp = new EmpDetail(
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
                dateHired
        );

        emp.SetStatus(status);
        return emp;
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
            Field field = EmpDetail.class.getDeclaredField(DISPLAY_FIELDS[index]);
            field.setAccessible(true);
            return field.get(this);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    // </editor-fold>
}

