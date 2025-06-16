package Objects.models;

public class WorkedHoursSummary {

    private int regularHours;
    private int weekendHours;
    private int holidayHours;

    // <editor-fold defaultstate="collapsed" desc="Construtors">
    // Optional: Default constructor
    public WorkedHoursSummary() {
    }

    // Optional: Constructor with initial values
    public WorkedHoursSummary(int regularHours, int weekendHours, int holidayHours) {
        this.regularHours = regularHours;
        this.weekendHours = weekendHours;
        this.holidayHours = holidayHours;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters">
    public int getRegularHours() {
        return regularHours;
    }

    public int getWeekendHours() {
        return weekendHours;
    }

    public int getHolidayHours() {
        return holidayHours;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Setters">
    public void setRegularHours(int regularHours) {
        this.regularHours = regularHours;
    }

    public void setWeekendHours(int weekendHours) {
        this.weekendHours = weekendHours;
    }

    public void setHolidayHours(int holidayHours) {
        this.holidayHours = holidayHours;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Utility">
    // Convenience: Add hours to each category
    public void addRegularHours(int hours) {
        this.regularHours += hours;
    }

    public void addWeekendHours(int hours) {
        this.weekendHours += hours;
    }

    public void addHolidayHours(int hours) {
        this.holidayHours += hours;
    }

    @Override
    public String toString() {
        return "WorkedHoursSummary{" +
                "regularHours=" + regularHours +
                ", weekendHours=" + weekendHours +
                ", holidayHours=" + holidayHours +
                '}';
    }
    // </editor-fold>
}

