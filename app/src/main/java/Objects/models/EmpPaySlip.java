package Objects.models;

import java.time.LocalDate;

public class EmpPaySlip extends EmployeeInfo {

    private double GrossPay;
    private double NetPay;
    private EmpDeductions Deductions;
    private LocalDate PeriodStart;
    private LocalDate PeriodEnd;

    // <editor-fold defaultstate="collapsed" desc="Constructor">
    public EmpPaySlip(
            String empNo,
            String lastName,
            String firstName,
            LocalDate birthday,
            AddressInfo address,
            double grossPay,
            double netPay,
            EmpDeductions deductions,
            LocalDate periodStart,
            LocalDate periodEnd
    ) {
        super(empNo, lastName, firstName, birthday, address);
        this.GrossPay = grossPay;
        this.NetPay = netPay;
        this.Deductions = deductions;
        this.PeriodStart = periodStart;
        this.PeriodEnd = periodEnd;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters">
    public double GetGrossPay() {
        return GrossPay;
    }

    public double GetNetPay() {
        return NetPay;
    }

    public EmpDeductions GetDeductions() {
        return Deductions;
    }

    public LocalDate GetPeriodStart() {
        return PeriodStart;
    }

    public LocalDate GetPeriodEnd() {
        return PeriodEnd;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters">
    public void SetGrossPay(double grossPay) {
        this.GrossPay = grossPay;
    }

    public void SetNetPay(double netPay) {
        this.NetPay = netPay;
    }

    public void SetDeductions(EmpDeductions deductions) {
        this.Deductions = deductions;
    }

    public void SetPeriodStart(LocalDate periodStart) {
        this.PeriodStart = periodStart;
    }

    public void SetPeriodEnd(LocalDate periodEnd) {
        this.PeriodEnd = periodEnd;
    }
    // </editor-fold>
}
