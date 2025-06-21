
package Interface;

import Objects.models.EmpDetail;
import java.util.List;

public interface IEmpMgmtProcess {
    List<EmpDetail> GetEmpDetails();
    List<EmpDetail> SearchEmployee(String query);
    boolean AddEmployee(EmpDetail newEmployee);
    boolean UpdateEmployee(EmpDetail updatedEmp);
    boolean DeleteEmployee(String empNo);
}
