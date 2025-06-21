package Interface;

import Objects.models.Credentials;
import Objects.models.EmpAttendance;
import Objects.models.EmpDetail;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ICSVFileReader {
    List<Credentials> Getcred();
    List<EmpDetail> GetEmpDeets();
    List<EmpAttendance> GetEmpAttendance(Optional<String> empNo, Optional<LocalDate> fromDate, Optional<LocalDate> toDate);
    boolean WriteToEmpDetailsCSV(List<EmpDetail> allEmployees);
}
