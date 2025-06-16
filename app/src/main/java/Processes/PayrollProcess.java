package Processes;

import Interface.ICSVFileReader;
import Interface.IPayrollProcess;
import Objects.enums.Constants.ShiftTime;
import Objects.models.EmpAttendance;
import Objects.models.EmpDetail;
import Objects.models.WorkedHoursSummary;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class PayrollProcess implements IPayrollProcess {

    private final ICSVFileReader csvFileReader;

    public PayrollProcess(ICSVFileReader _csvFileReader) {
        this.csvFileReader = _csvFileReader;
    }

    public List<EmpDetail> GetEmpDetails() {
        return csvFileReader.GetEmpDeets();
    }

    public int CalculateHoursWorked(String empNo, LocalDate fromDate, LocalDate toDate, List<LocalDate> holidays) {
        List<EmpAttendance> empAttendances = this.GetEmpAttendance(
                Optional.of(empNo),
                Optional.of(fromDate),
                Optional.of(toDate)
        );

        LocalTime shiftStart = ShiftTime.START.getTime();

        WorkedHoursSummary summary = new WorkedHoursSummary();

        for (EmpAttendance record : empAttendances) {
            LocalDate date = record.getBizDate();
            boolean isWeekend = date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;
            boolean isHoliday = holidays.contains(date);

            if (record.getLogInTime() != null && record.getLogOutTime() != null) {
                long workedMinutes = Duration.between(record.getLogInTime(), record.getLogOutTime()).toMinutes();

                long lateMinutes = 0;
                if (record.getLogInTime().isAfter(shiftStart)) {
                    lateMinutes = Duration.between(shiftStart, record.getLogInTime()).toMinutes();
                }

                long adjustedMinutes = workedMinutes - lateMinutes;
                if (adjustedMinutes < 0) {
                    adjustedMinutes = 0;
                }

                int workedHours = (int) (adjustedMinutes / 60);

                if (isHoliday) {
                    summary.setHolidayHours(summary.getHolidayHours() + workedHours);
                } else if (isWeekend) {
                    summary.setWeekendHours(summary.getWeekendHours() + workedHours);
                } else {
                    summary.setRegularHours(summary.getRegularHours() + workedHours);
                }
            }
        }

        int totalHours = summary.getRegularHours() + summary.getWeekendHours() + summary.getHolidayHours();
        return totalHours;
    }

    private List<EmpAttendance> GetEmpAttendance(Optional<String> empNo, Optional<LocalDate> fromDate, Optional<LocalDate> toDate) {
        return csvFileReader.GetEmpAttendance(empNo, fromDate, toDate);
    }
}
