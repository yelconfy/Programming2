/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Helper;

import Interface.ICSVReader;
import Interface.ILogInProcess;
import com.example.S1101.project.HomePage;
import com.example.S1101.project.LogInProcess;

/**
 *
 * @author eyell
 */
public class Injector {
     public static HomePage createHomePage() {
        ILogInProcess loginProcess = createLoginProcess();
        return new HomePage(loginProcess);
    }
    
    public static ILogInProcess createLoginProcess() {
        ICSVReader csvReader = new CSVReader();
        return new LogInProcess(csvReader); 
    }
}
