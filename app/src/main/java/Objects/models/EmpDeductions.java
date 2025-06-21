package Objects.models;

public class EmpDeductions extends BaseEmployeeInfo {

    private double withholdingTax;
    private double sssContribution;
    private double philHealthContribution;
    private double pagIbigContribution;
    private double latesDeduction;
    private double absencesDeduction;
    private double totalDeductions;

    public EmpDeductions(String empNo) {
        super(empNo);
    }

    // <editor-fold defaultstate="collapsed" desc="Getters">
    public double GetTotalDeductions() {
        return totalDeductions;
    }

    public double GetWithholdingTax() {
        return withholdingTax;
    }

    public double GetSssContribution() {
        return sssContribution;
    }

    public double GetPhilHealthContribution() {
        return philHealthContribution;
    }

    public double GetPagIbigContribution() {
        return pagIbigContribution;
    }

    public double GetLatesDeduction() {
        return latesDeduction;
    }

    public double GetAbsencesDeduction() {
        return absencesDeduction;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Setters">
    public void SetWithholdingTax(double withholdingTax) {
        this.withholdingTax = withholdingTax;
    }

    public void SetSssContribution(double sssContribution) {
        this.sssContribution = sssContribution;
    }

    public void SetPhilHealthContribution(double philHealthContribution) {
        this.philHealthContribution = philHealthContribution;
    }

    public void SetPagIbigContribution(double pagIbigContribution) {
        this.pagIbigContribution = pagIbigContribution;
    }

    public void SetLatesDeduction(double latesDeduction) {
        this.latesDeduction = latesDeduction;
    }

    public void SetAbsencesDeduction(double absencesDeduction) {
        this.absencesDeduction = absencesDeduction;
    }
    
    public void SetTotalDeductions(double totalDeductions) {
        this.totalDeductions = totalDeductions;
    }
    // </editor-fold>
}
