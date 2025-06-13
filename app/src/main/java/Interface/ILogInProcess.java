/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Interface;

import Objects.models.Credentials;

/**
 *
 * @author eyell
 */
public interface ILogInProcess {
    Credentials performLogin(String username, String password);
    void redirect(String deptCode);
}
