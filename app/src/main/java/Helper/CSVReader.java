/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Helper;

import Interface.ICSVReader;
import Objects.models.Credentials;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author eyell
 */
public class CSVReader implements ICSVReader {

    private final String credCsvFile = "C:\\Users\\eyell\\OneDrive\\Documents\\School\\CredCSV.csv";
    private final String attendanceCsvFile = "C:\\Users\\eyell\\OneDrive\\Documents\\School\\CredCSV.csv"; // CHANGE CredCSV.csv TO ATTENDANCE CSV
    private final String empDataCsvFile = "C:\\Users\\eyell\\OneDrive\\Documents\\School\\CredCSV.csv"; // CHANGE CredCSV.csv TO EMPLOYEE DATA CSV
    private String line;
    private final String csvSplitBy = ",";

    public List<Credentials> Getcred() {
        // Map to store employee data
        List<Credentials> credData = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(credCsvFile))) {
            // Read the header line
            br.readLine();

            // Read file line by line
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(csvSplitBy);  // Split the line by the delimiter
                Credentials cred = Credentials.fromArray(parts);
                credData.add(cred);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return credData;
    }

}
