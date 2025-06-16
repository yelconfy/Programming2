
package Processes;

import Forms.PayrollForm;
import Helper.Injector;
import Interface.ICSVFileReader;
import Interface.ILogInProcess;
import Objects.models.Credentials;
import com.example.S1101.project.LoginFrame;
import java.util.List;

public class LogInProcess implements ILogInProcess{
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
        switch (deptCode) {
            case "PRS" -> {
                PayrollForm landingPage = Injector.createPayrollForm();
                landingPage.setVisible(true);
            }
            default -> {}

        }
    }
}
