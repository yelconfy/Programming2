package Processes;

import Helper.AmountUtil;
import Interface.ICSVFileReader;
import Interface.IEmpMgmtProcess;
import Objects.models.EmpDetail;
import java.util.List;
import java.util.stream.Collectors;

public class EmpMgmtProcess implements IEmpMgmtProcess {

    private final ICSVFileReader csvFileReader;

    // <editor-fold defaultstate="collapsed" desc="Constructor">
    public EmpMgmtProcess(ICSVFileReader _csvFileReader) {
        this.csvFileReader = _csvFileReader;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Public Methods">
    // <editor-fold defaultstate="collapsed" desc="GetEmpDetails">
    public List<EmpDetail> GetEmpDetails() {
        return csvFileReader.GetEmpDeets();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="SearchEmployee">
    public List<EmpDetail> SearchEmployee(String query) {
        List<EmpDetail> employees = this.csvFileReader.GetEmpDeets()
                .stream()
                .filter(EmpDetail::GetStatus)
                .collect(Collectors.toList());

        if (query == null || query.trim().isEmpty()) {
            return employees; // return all if query is empty
        }

        String lowerQuery = query.toLowerCase();

        return employees.stream()
                .filter(emp -> emp.GetEmpNo().toLowerCase().contains(lowerQuery)
                || emp.GetFirstName().toLowerCase().contains(lowerQuery)
                || emp.GetLastName().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }
    // </editor-fold>

    public boolean AddEmployee(EmpDetail newEmployee) {
        List<EmpDetail> allEmployees = this.csvFileReader.GetEmpDeets();

        allEmployees.add(newEmployee);
        return this.csvFileReader.WriteToEmpDetailsCSV(allEmployees);
    }

    public boolean UpdateEmployee(EmpDetail updatedEmp) {
        List<EmpDetail> allEmployees = this.csvFileReader.GetEmpDeets();

        for (int i = 0; i < allEmployees.size(); i++) {
            if (allEmployees.get(i).GetEmpNo().equals(updatedEmp.GetEmpNo())) {
                allEmployees.set(i, updatedEmp); // ✅ actually updates the list
                break;
            }
        }

        return this.csvFileReader.WriteToEmpDetailsCSV(allEmployees);
    }
    
    public boolean DeleteEmployee(String empNo){
        List<EmpDetail> allEmployees = this.csvFileReader.GetEmpDeets();
        
        for (EmpDetail emp : allEmployees){
            if (emp.GetEmpNo().equals(empNo)){
                emp.SetStatus(false);
            }
        }
        
        return this.csvFileReader.WriteToEmpDetailsCSV(allEmployees);
    }

    // </editor-fold>
}
