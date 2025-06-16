package Interface;

import Objects.models.EmpDetail;
import java.time.LocalDate;
import java.util.List;

public interface IPayrollProcess {
    int CalculateHoursWorked(String empNo, LocalDate fromDate, LocalDate toDate, List<LocalDate> holidays);
    List<EmpDetail> GetEmpDetails();
}
