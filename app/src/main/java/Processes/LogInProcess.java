package Processes;

import Forms.EmpMgmtForm;
import Forms.PayrollForm;
import Forms.TimeKeepingForm;
import Helper.Injector;
import Interface.ICSVFileReader;
import Interface.ILogInProcess;
import Objects.enums.Constants.DeptCode;
import static Objects.enums.Constants.DeptCode.HR;
import Objects.models.Credentials;
import java.util.List;

public class LogInProcess implements ILogInProcess {

    private final ICSVFileReader csvFilereader;

    public LogInProcess(ICSVFileReader _csvFilereader) {
        this.csvFilereader = _csvFilereader;
    }

    public Credentials performLogin(String username, String password) {
        List<Credentials> credData = csvFilereader.Getcred();
        return credData.stream()
                .filter(user -> user.getUserName().equals(username) && user.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    public void redirect(String deptCode) {
        DeptCode code = DeptCode.valueOf(deptCode); // throws if invalid
        switch (code) {
            case PR -> {
                PayrollForm landingPage = Injector.createPayrollForm();
                landingPage.setVisible(true);
            }
            case HR -> {
                EmpMgmtForm landingPage = Injector.createEmpMgmtForm();
                landingPage.setVisible(true);
            }
            case TK -> {
                TimeKeepingForm landingPage = Injector.createTimeKeepingForm();
                landingPage.setVisible(true);
            }
            default -> {
            }
        }
    }
}
