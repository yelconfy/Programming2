
package Interface;

import Objects.models.EmpAttendance;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ITimeKeepingProcess {
    List<EmpAttendance> GetEmpAttendance(Optional<String> empNo, Optional<LocalDate> fromDate, Optional<LocalDate> toDate);
}
