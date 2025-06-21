package Processes;

import Interface.ICSVFileReader;
import Interface.ITimeKeepingProcess;
import Objects.models.EmpAttendance;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TimeKeepingProcess implements ITimeKeepingProcess {

    private final ICSVFileReader csvFileReader;

    // <editor-fold defaultstate="collapsed" desc="Constructor">
    public TimeKeepingProcess(ICSVFileReader _csvFileReader) {
        this.csvFileReader = _csvFileReader;
    }
    // </editor-fold>

    // This is your main business method
    public List<EmpAttendance> GetEmpAttendance(Optional<String> query, Optional<LocalDate> fromDate, Optional<LocalDate> toDate) {
        Optional<String> empNo = Optional.empty();

        // If query is numeric → treat as EmpNo
        if (query.isPresent() && query.get().matches("\\d+")) {
            empNo = query;
        }

        // 👉 Call CSV reader with EmpNo and date range
        List<EmpAttendance> employees = this.csvFileReader.GetEmpAttendance(empNo, fromDate, toDate);

        // Now, do extra name filtering in Java if query is non-numeric
        if (query.isEmpty() || query.get().trim().isEmpty() || empNo.isPresent()) {
            return employees; // Already filtered by EmpNo + date range
        }

        String lowerQuery = query.get().trim().toLowerCase();

        return employees.stream()
                .filter(emp -> emp.GetFirstName().toLowerCase().contains(lowerQuery)
                || emp.GetLastName().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }

}
