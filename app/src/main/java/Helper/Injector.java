package Helper;

import Forms.EmpMgmtForm;
import Forms.PayrollForm;
import Forms.TimeKeepingForm;
import Interface.ICSVFileReader;
import Interface.IEmpMgmtProcess;
import Interface.ILogInProcess;
import Interface.IPayrollProcess;
import Interface.ITimeKeepingProcess;
import Processes.EmpMgmtProcess;
import Processes.LogInProcess;
import Processes.PayrollProcess;
import Processes.TimeKeepingProcess;
import com.example.S1101.project.HomePage;

public class Injector {

    public static HomePage createHomePage() {
        ILogInProcess loginProcess = createLoginProcess();
        return new HomePage(loginProcess);
    }

    public static PayrollForm createPayrollForm() {
        ICSVFileReader csvReader = new CSVFileReader();
        IPayrollProcess payrollProcess = new PayrollProcess(csvReader); 
        return new PayrollForm(payrollProcess); 
    }
    
    public static EmpMgmtForm createEmpMgmtForm() {
        ICSVFileReader csvReader = new CSVFileReader();
        IEmpMgmtProcess empMgmtProcess = new EmpMgmtProcess(csvReader); 
        return new EmpMgmtForm(empMgmtProcess);
    }
    
     public static TimeKeepingForm createTimeKeepingForm() {
        ICSVFileReader csvReader = new CSVFileReader();
        ITimeKeepingProcess timeKeepingProcess = new TimeKeepingProcess(csvReader); 
        return new TimeKeepingForm(timeKeepingProcess);
    }

    public static ILogInProcess createLoginProcess() {
        ICSVFileReader csvReader = new CSVFileReader();
        return new LogInProcess(csvReader);
    }
}
