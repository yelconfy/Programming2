package Interface;

import Objects.models.EmpDeductions;
import Objects.models.EmpDetail;
import Objects.models.EmpPaySlip;
import Objects.models.WorkedHoursSummary;
import java.time.LocalDate;
import java.util.List;

public interface IPayrollProcess {
    WorkedHoursSummary CalculateHoursWorked(String empNo, LocalDate fromDate, LocalDate toDate);
    double ComputeOvertimePay(WorkedHoursSummary summary, double hourlyRate);
    List<EmpDetail> GetEmpDetails();
    EmpDeductions ComputeEmployeeDeductions(EmpDetail empDetail, LocalDate fromDate, LocalDate toDate);
    EmpPaySlip GenerateEmpPaySlip(EmpDetail empDetail, LocalDate periodStart, LocalDate periodEnd, WorkedHoursSummary hoursWorked, EmpDeductions deductions);
    List<EmpDetail> SearchEmployee(String query);
    boolean UpdateAllowances(EmpDetail selectedEmp, String riceSubsidy, String clothingAllowance, String phoneAllowance);
}
