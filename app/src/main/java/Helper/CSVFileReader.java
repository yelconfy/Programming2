package Helper;

import com.opencsv.CSVReader;
import Interface.ICSVFileReader;
import Objects.enums.Constants.File;
import Objects.models.Credentials;
import Objects.models.EmpAttendance;
import Objects.models.EmpDetail;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.List;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import javax.swing.JOptionPane;

public class CSVFileReader implements ICSVFileReader {

    public List<Credentials> Getcred() {
        InputStream csvFile = GetCSV(File.CredCSV.GetFileName());

        // Map to store employee data
        List<Credentials> credData = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(csvFile))) {
            reader.readNext(); // Skip header row

            String[] nextLine;
            // Read file line by line
            while ((nextLine = reader.readNext()) != null) {
                Credentials cred = Credentials.fromArray(nextLine);
                credData.add(cred);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return credData;
    }

    public List<EmpDetail> GetEmpDeets() {
        InputStream csvFile = GetCSV(File.EmpDetailsCSV.GetFileName());

        // Map to store employee data
        List<EmpDetail> empDetails = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(csvFile))) {
            reader.readNext(); // Skip header row

            String[] nextLine;
            // Read file line by line
            while ((nextLine = reader.readNext()) != null) {
                EmpDetail e = EmpDetail.FromArray(nextLine);
                empDetails.add(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return empDetails;
    }

    public List<EmpAttendance> GetEmpAttendance(Optional<String> empNo, Optional<LocalDate> fromDate, Optional<LocalDate> toDate) {
        InputStream csvFile = GetCSV(File.EmpAttendanceRecordCSV.GetFileName());

        List<EmpAttendance> empAttendances = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(csvFile))) {
            reader.readNext(); // Skip header row

            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                EmpAttendance e = EmpAttendance.FromArray(nextLine);

                boolean matchesEmpNo = empNo.isEmpty() || empNo.get().equals(e.GetEmpNo());
                boolean matchesFromDate = fromDate.isEmpty() || !e.GetBizDate().isBefore(fromDate.get());
                boolean matchesToDate = toDate.isEmpty() || !e.GetBizDate().isAfter(toDate.get());

                if (matchesEmpNo && matchesFromDate && matchesToDate) {
                    empAttendances.add(e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return empAttendances;
    }

    public boolean WriteToEmpDetailsCSV(List<EmpDetail> allEmployees) {
        String filePath = File.GetFilePath(File.EmpDetailsCSV.GetFileName());
        String header = File.EmpDetailsCSV.GetHeader();
        String format = File.EmpDetailsCSV.GetColumnFormat();

        try {
            // ✅ Ensure 'files' folder exists
            java.io.File file = new java.io.File(filePath);
            file.getParentFile().mkdirs();

            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println(header);

                for (EmpDetail emp : allEmployees) {
                    writer.printf(
                            format,
                            emp.GetEmpNo(),
                            emp.GetLastName(),
                            emp.GetFirstName(),
                            DateTimeUtil.formatDate(emp.GetBirthday()),
                            emp.GetEmail(),
                            emp.GetPhoneNo(),
                            emp.GetAddress().GetHouseBlkLotNo(),
                            emp.GetAddress().GetStreet(),
                            emp.GetAddress().GetBarangay(),
                            emp.GetAddress().GetCityMunicipality(),
                            emp.GetAddress().GetProvince(),
                            emp.GetAddress().GetZipCode(),
                            emp.GetSssNo(),
                            emp.GetPhilHealthNo(),
                            emp.GetTinNo(),
                            emp.GetPagIbigNo(),
                            emp.GetEmpStatus(),
                            emp.GetPosition(),
                            emp.GetImmSupervisor(),
                            DateTimeUtil.formatDate(emp.GetDateHired()),
                            emp.GetCompensation().GetBasicSalary(),
                            emp.GetCompensation().GetRiceSubsidy(),
                            emp.GetCompensation().GetPhoneAllowance(),
                            emp.GetCompensation().GetClothingAllowance(),
                            emp.GetCompensation().GetGrossSemiMonthlyRate(),
                            emp.GetCompensation().GetHourlyRate(),
                            emp.GetStatus()
                    );
                }

                writer.flush();
                System.out.println("Employee CSV updated successfully → " + filePath);

            }
        } catch (Exception e) {
            System.err.println("Error saving employee data: " + e.getMessage());
            return false;
        }

        return true;
    }

    public boolean WriteToCredCSV() {

        return true;
    }

    public boolean WriteToEmpAttendanceRecordCSV() {

        return true;
    }

    private InputStream GetCSV(String csvName) {
        InputStream inputStream = null;
        try {
            // Try runtime 'files' folder first
            String filePath = File.GetFilePath(csvName);
            inputStream = new FileInputStream(filePath);
        } catch (Exception ignored) {
            // fallback: optional if you still want to check classpath (for templates)
            inputStream = getClass().getClassLoader().getResourceAsStream("files/" + csvName);
        }

        if (inputStream == null) {
            JOptionPane.showMessageDialog(
                    null,
                    "CSV file not found: " + csvName,
                    "File Not Found",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        return inputStream;
    }

}
