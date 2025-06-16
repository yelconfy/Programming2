package Interface;

import Objects.models.Credentials;

public interface ILogInProcess {
    Credentials performLogin(String username, String password);
    void redirect(String deptCode);
}
