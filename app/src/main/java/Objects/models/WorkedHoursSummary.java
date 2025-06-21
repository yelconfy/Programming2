package Objects.models;

public class WorkedHoursSummary {

    private int RegularHours;
    private int WeekendHours;
    private int HolidayHours;
    private int OvertimeHours;
    private int TotalLateMinutes;
    private int TotalAbsentDays;

    // <editor-fold defaultstate="collapsed" desc="Constructors">
    public WorkedHoursSummary() {
    }

    public WorkedHoursSummary(int regularHours, int weekendHours, int holidayHours) {
        this.RegularHours = regularHours;
        this.WeekendHours = weekendHours;
        this.HolidayHours = holidayHours;
    }

    public WorkedHoursSummary(int regularHours, int weekendHours, int holidayHours, int overtimeHours) {
        this.RegularHours = regularHours;
        this.WeekendHours = weekendHours;
        this.HolidayHours = holidayHours;
        this.OvertimeHours = overtimeHours;
    }

    public WorkedHoursSummary(int regularHours, int weekendHours, int holidayHours, int overtimeHours,
                              int totalLateMinutes, int totalAbsentDays) {
        this.RegularHours = regularHours;
        this.WeekendHours = weekendHours;
        this.HolidayHours = holidayHours;
        this.OvertimeHours = overtimeHours;
        this.TotalLateMinutes = totalLateMinutes;
        this.TotalAbsentDays = totalAbsentDays;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters">
    public int GetRegularHours() {
        return RegularHours;
    }

    public int GetWeekendHours() {
        return WeekendHours;
    }

    public int GetHolidayHours() {
        return HolidayHours;
    }

    public int GetOvertimeHours() {
        return OvertimeHours;
    }

    public int GetTotalLateMinutes() {
        return TotalLateMinutes;
    }

    public int GetTotalAbsentDays() {
        return TotalAbsentDays;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Setters">
    public void SetRegularHours(int regularHours) {
        this.RegularHours = regularHours;
    }

    public void SetWeekendHours(int weekendHours) {
        this.WeekendHours = weekendHours;
    }

    public void SetHolidayHours(int holidayHours) {
        this.HolidayHours = holidayHours;
    }

    public void SetOvertimeHours(int overtimeHours) {
        this.OvertimeHours = overtimeHours;
    }

    public void SetTotalLateMinutes(int totalLateMinutes) {
        this.TotalLateMinutes = totalLateMinutes;
    }

    public void SetTotalAbsentDays(int totalAbsentDays) {
        this.TotalAbsentDays = totalAbsentDays;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Utility">
    public void AddRegularHours(int hours) {
        this.RegularHours += hours;
    }

    public void AddWeekendHours(int hours) {
        this.WeekendHours += hours;
    }

    public void AddHolidayHours(int hours) {
        this.HolidayHours += hours;
    }

    public void AddOvertimeHours(int hours) {
        this.OvertimeHours += hours;
    }

    public void AddLateMinutes(int minutes) {
        this.TotalLateMinutes += minutes;
    }

    public void AddAbsentDays(int days) {
        this.TotalAbsentDays += days;
    }

    @Override
    public String toString() {
        return "WorkedHoursSummary{" +
                "RegularHours=" + RegularHours +
                ", WeekendHours=" + WeekendHours +
                ", HolidayHours=" + HolidayHours +
                ", OvertimeHours=" + OvertimeHours +
                ", TotalLateMinutes=" + TotalLateMinutes +
                ", TotalAbsentDays=" + TotalAbsentDays +
                '}';
    }
    // </editor-fold>
}



