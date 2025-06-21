package Objects.models;

public class CompensationInfo extends BaseEmployeeInfo {

    private double BasicSalary;
    private double RiceSubsidy;
    private double PhoneAllowance;
    private double ClothingAllowance;
    private double GrossSemiMonthlyRate;
    private double HourlyRate;

    public CompensationInfo(String empNo) {
        super(empNo);
    }

    // <editor-fold defaultstate="collapsed" desc="Getters">
    public double GetBasicSalary() {
        return BasicSalary;
    }

    public double GetRiceSubsidy() {
        return RiceSubsidy;
    }

    public double GetPhoneAllowance() {
        return PhoneAllowance;
    }

    public double GetClothingAllowance() {
        return ClothingAllowance;
    }

    public double GetGrossSemiMonthlyRate() {
        return GrossSemiMonthlyRate;
    }

    public double GetHourlyRate() {
        return HourlyRate;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Setters">
    public void SetBasicSalary(double basicSalary) {
        this.BasicSalary = basicSalary;
    }

    public void SetRiceSubsidy(double riceSubsidy) {
        this.RiceSubsidy = riceSubsidy;
    }

    public void SetPhoneAllowance(double phoneAllowance) {
        this.PhoneAllowance = phoneAllowance;
    }

    public void SetClothingAllowance(double clothingAllowance) {
        this.ClothingAllowance = clothingAllowance;
    }

    public void SetGrossSemiMonthlyRate(double grossSemiMonthlyRate) {
        this.GrossSemiMonthlyRate = grossSemiMonthlyRate;
    }

    public void SetHourlyRate(double hourlyRate) {
        this.HourlyRate = hourlyRate;
    }
    // </editor-fold>
}
