package Helper;

import Forms.PayrollForm;
import Interface.ICSVFileReader;
import Interface.ILogInProcess;
import Interface.IPayrollProcess;
import Processes.LogInProcess;
import Processes.PayrollProcess;
import com.example.S1101.project.HomePage;

public class Injector {

    public static HomePage createHomePage() {
        ILogInProcess loginProcess = createLoginProcess();
        return new HomePage(loginProcess);
    }

    public static PayrollForm createPayrollForm() {
        ICSVFileReader csvReader = new CSVFileReader();
        IPayrollProcess payrollProcess = new PayrollProcess(csvReader); // ✅ use process, not reader
        return new PayrollForm(payrollProcess); // ✅ pass the process, not reader
    }

    public static ILogInProcess createLoginProcess() {
        ICSVFileReader csvReader = new CSVFileReader();
        return new LogInProcess(csvReader);
    }
}
