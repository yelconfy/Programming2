package Helper;

import com.opencsv.CSVReader;
import Interface.ICSVFileReader;
import Objects.models.Credentials;
import Objects.models.EmpAttendance;
import Objects.models.EmpDetail;
import java.util.List;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;
import javax.swing.JOptionPane;

public class CSVFileReader implements ICSVFileReader {

    private final String csvRootPath = "files";

    private final String credCsvFilePath = MessageFormat.format("{0}/CredCSV.csv", csvRootPath);
    private final String attendanceCsvFilePath = MessageFormat.format("{0}/EmpAttendanceRecordCSV.csv", csvRootPath);
    private final String empDataCsvFilePath = MessageFormat.format("{0}/EmpDetailsCSV.csv", csvRootPath);

    private InputStream GetCSV(String csvPath) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(csvPath);

        if (inputStream == null) {
            JOptionPane.showMessageDialog(
                    null,
                    "CSV file not found: " + csvPath,
                    "File Not Found",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        return inputStream;
    }

    public List<Credentials> Getcred() {
        InputStream csvFile = GetCSV(credCsvFilePath);

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
//        InputStream csvFile = GetCSV(empDataCsvFilePath);
//
//        // Map to store employee data
//        List<EmpDetail> empDetails = new ArrayList<>();
//
//        try (CSVReader reader = new CSVReader(new InputStreamReader(csvFile))) {
//            reader.readNext(); // Skip header row
//
//            String[] nextLine;
//            // Read file line by line
//            while ((nextLine = reader.readNext()) != null) {
//                EmpDetail e = EmpDetail.fromArray(nextLine);
//                empDetails.add(e);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return empDetails;
        return null;
    }

    public List<EmpAttendance> GetEmpAttendance(Optional<String> empNo, Optional<LocalDate> fromDate, Optional<LocalDate> toDate) {
        InputStream csvFile = GetCSV(attendanceCsvFilePath);

        List<EmpAttendance> empAttendances = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(csvFile))) {
            reader.readNext(); // Skip header row

            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                EmpAttendance e = EmpAttendance.fromArray(nextLine);

                boolean matchesEmpNo = empNo.isEmpty() || empNo.get().equals(e.getEmpNo());
                boolean matchesFromDate = fromDate.isEmpty() || !e.getBizDate().isBefore(fromDate.get());
                boolean matchesToDate = toDate.isEmpty() || !e.getBizDate().isAfter(toDate.get());

                if (matchesEmpNo && matchesFromDate && matchesToDate) {
                    empAttendances.add(e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return empAttendances;
    }

}
