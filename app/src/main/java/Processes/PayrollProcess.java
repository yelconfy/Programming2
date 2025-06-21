package Processes;

import Helper.AmountUtil;
import Interface.ICSVFileReader;
import Interface.IPayrollProcess;
import Objects.enums.Constants.Holidays;
import Objects.enums.Constants.OvertimeRateMultiplier;
import Objects.enums.Constants.PagIbigBracket;
import Objects.enums.Constants.PhilHealthBracket;
import Objects.enums.Constants.SSSBracket;
import Objects.enums.Constants.ShiftTime;
import Objects.enums.Constants.WithholdingTaxBracket;
import Objects.models.EmpAttendance;
import Objects.models.EmpDeductions;
import Objects.models.EmpDetail;
import Objects.models.EmpPaySlip;
import Objects.models.WorkedHoursSummary;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PayrollProcess implements IPayrollProcess {

    private final ICSVFileReader csvFileReader;

    // <editor-fold defaultstate="collapsed" desc="Constructor">
    public PayrollProcess(ICSVFileReader _csvFileReader) {
        this.csvFileReader = _csvFileReader;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Public Methods">
    // <editor-fold defaultstate="collapsed" desc="GetEmpDetails">
    public List<EmpDetail> GetEmpDetails() {
        return csvFileReader.GetEmpDeets();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="CalculateHoursWorked">
    public WorkedHoursSummary CalculateHoursWorked(String empNo, LocalDate fromDate, LocalDate toDate) {
        List<EmpAttendance> empAttendances = this.GetEmpAttendance(
                Optional.of(empNo),
                Optional.of(fromDate),
                Optional.of(toDate)
        );

        LocalTime shiftStart = ShiftTime.START.GetTime();
        LocalTime shiftEnd = ShiftTime.END.GetTime();
        Duration lunchBreak = ShiftTime.LUNCH_BREAK_DURATION.GetDuration();

        // Daily shift duration minus lunch break
        long shiftDurationMinutes = Duration.between(shiftStart, shiftEnd).toMinutes() - lunchBreak.toMinutes();

        // Count billable days in this cutoff
        int billableDays = 0;
        LocalDate date = fromDate;
        while (!date.isAfter(toDate)) {
            boolean isWeekend = date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;
            boolean isHoliday = Holidays.IsHoliday(date);

            if (!isWeekend && !isHoliday) {
                billableDays++;
            }
            date = date.plusDays(1);
        }

        // Compute standard paid hours for this cutoff
        double dailyPaidHours = shiftDurationMinutes / 60.0;
        double standardHoursPerCutoff = billableDays * dailyPaidHours;

        // Prepare summary
        WorkedHoursSummary summary = new WorkedHoursSummary();

        for (EmpAttendance record : empAttendances) {
            LocalDate bizDate = record.GetBizDate();
            boolean isWeekend = bizDate.getDayOfWeek() == DayOfWeek.SATURDAY || bizDate.getDayOfWeek() == DayOfWeek.SUNDAY;
            boolean isHoliday = Holidays.IsHoliday(bizDate);

            if (record.GetLogInTime() != null && record.GetLogOutTime() != null) {
                long workedMinutes = Duration.between(record.GetLogInTime(), record.GetLogOutTime()).toMinutes();

                // Calculate late minutes if login is after shift start
                long lateMinutes = 0;
                if (record.GetLogInTime().isAfter(shiftStart)) {
                    lateMinutes = Duration.between(shiftStart, record.GetLogInTime()).toMinutes();
                    summary.AddLateMinutes((int) lateMinutes);
                }

                // Adjust worked minutes for lateness
                long adjustedMinutes = workedMinutes - lateMinutes;
                if (adjustedMinutes < 0) {
                    adjustedMinutes = 0;
                }

                // Regular vs overtime split
                long regularMinutes = Math.min(adjustedMinutes, shiftDurationMinutes);
                long overtimeMinutes = Math.max(0, adjustedMinutes - shiftDurationMinutes);

                int regularHours = (int) (regularMinutes / 60);
                int overtimeHours = (int) (overtimeMinutes / 60);

                if (isHoliday) {
                    summary.AddHolidayHours(regularHours);
                    summary.AddOvertimeHours(overtimeHours);
                } else if (isWeekend) {
                    summary.AddWeekendHours(regularHours);
                    summary.AddOvertimeHours(overtimeHours);
                } else {
                    summary.AddRegularHours(regularHours);
                    summary.AddOvertimeHours(overtimeHours);
                }

            } else {
                // Missing login/logout = absent
                summary.AddAbsentDays(1);
            }
        }

        // Compute final overtime to ensure it aligns with excess over standard cutoff
        int totalWorked = summary.GetRegularHours() + summary.GetWeekendHours() + summary.GetHolidayHours();
        int finalOvertime = Math.max(0, totalWorked - (int) standardHoursPerCutoff);

        summary.SetOvertimeHours(finalOvertime);

        return summary;
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="ComputeOvertimePay">
    public double ComputeOvertimePay(WorkedHoursSummary summary, double hourlyRate) {
        int regularOT = summary.GetOvertimeHours(); // if you keep just one field for all overtime
        // OR if you split OT by category, adjust this!

        // For demonstration, let's assume you want to apply the same multiplier:
        double pay = regularOT * hourlyRate * OvertimeRateMultiplier.REGULAR_OT.getMultiplier();

        return pay;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="ComputeEmployeeDeductions">
    public EmpDeductions ComputeEmployeeDeductions(
            EmpDetail empDetail,
            LocalDate fromDate,
            LocalDate toDate
    ) {
        String empNo = empDetail.GetEmpNo();

        // Use semi-monthly basic salary for this cut-off
        double semiMonthlyBasic = empDetail.GetCompensation().GetGrossSemiMonthlyRate();

        EmpDeductions deductions = new EmpDeductions(empNo);

        // Get full monthly statutory contributions
        double sssContributionMonthly = GetSSSContribution(empDetail.GetCompensation().GetBasicSalary());
        double philHealthContributionMonthly = ComputePhilHealth(empDetail.GetCompensation().GetBasicSalary());
        double pagIbigContributionMonthly = ComputePagIbig(empDetail.GetCompensation().GetBasicSalary());

        // Split by 2 for semi-monthly cut-off
        double sssContribution = sssContributionMonthly / 2.0;
        double philHealthContribution = philHealthContributionMonthly / 2.0;
        double pagIbigContribution = pagIbigContributionMonthly / 2.0;

        deductions.SetSssContribution(sssContribution);
        deductions.SetPhilHealthContribution(philHealthContribution);
        deductions.SetPagIbigContribution(pagIbigContribution);

        // Compute worked hours in this cut-off
        WorkedHoursSummary hoursWorked = this.CalculateHoursWorked(empNo, fromDate, toDate);

        // Compute lates & absences based on semi-monthly rate
        double latesDeduction = ComputeLatesDeduction(hoursWorked.GetTotalLateMinutes(), semiMonthlyBasic);
        double absencesDeduction = ComputeAbsencesDeduction(hoursWorked.GetTotalAbsentDays(), semiMonthlyBasic);

        deductions.SetLatesDeduction(latesDeduction);
        deductions.SetAbsencesDeduction(absencesDeduction);

        // Taxable income for this cut-off
        double taxableIncome = semiMonthlyBasic - (sssContribution + philHealthContribution + pagIbigContribution);

        double withholdingTax = ComputeWithholdingTax(taxableIncome);
        deductions.SetWithholdingTax(withholdingTax);

        // Total deductions for this cut-off
        double totalDeductions = sssContribution
                + philHealthContribution
                + pagIbigContribution
                + withholdingTax
                + latesDeduction
                + absencesDeduction;

        deductions.SetTotalDeductions(totalDeductions);

        return deductions;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="GenerateEmpPaySlip">
    public EmpPaySlip GenerateEmpPaySlip(
            EmpDetail empDetail,
            LocalDate periodStart,
            LocalDate periodEnd,
            WorkedHoursSummary hoursWorked,
            EmpDeductions deductions
    ) {
        // Use semi-monthly base salary for this cut-off
        double semiMonthlyBasic = empDetail.GetCompensation().GetGrossSemiMonthlyRate();

        // Total allowances (typically also semi-monthly if paid per cut-off)
        double totalAllowances = empDetail.GetCompensation().GetRiceSubsidy()
                + empDetail.GetCompensation().GetPhoneAllowance()
                + empDetail.GetCompensation().GetClothingAllowance();

        // Overtime pay for this cut-off
        double overtimePay = ComputeOvertimePay(hoursWorked, empDetail.GetCompensation().GetHourlyRate());

        // Gross pay for this cut-off
        double grossPay = semiMonthlyBasic + totalAllowances + overtimePay;

        // Net pay = gross - cut-off deductions
        double netPay = grossPay - deductions.GetTotalDeductions();

        // Build payslip
        EmpPaySlip paySlip = new EmpPaySlip(
                empDetail.GetEmpNo(),
                empDetail.GetLastName(),
                empDetail.GetFirstName(),
                empDetail.GetBirthday(),
                empDetail.GetAddress(),
                grossPay,
                netPay,
                deductions,
                periodStart,
                periodEnd
        );

        return paySlip;
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

    // <editor-fold defaultstate="collapsed" desc="UpdateAllowances">
    public boolean UpdateAllowances(EmpDetail selectedEmp, String riceSubsidy, String clothingAllowance, String phoneAllowance) {
        // Load all employees
        List<EmpDetail> allEmployees = GetEmpDetails();

        // Update the matching employee's allowances
        for (EmpDetail emp : allEmployees) {
            if (emp.GetEmpNo().equals(selectedEmp.GetEmpNo())) {
                emp.GetCompensation().SetRiceSubsidy(AmountUtil.ParseFormattedStringToDouble(riceSubsidy));
                emp.GetCompensation().SetClothingAllowance(AmountUtil.ParseFormattedStringToDouble(clothingAllowance));
                emp.GetCompensation().SetPhoneAllowance(AmountUtil.ParseFormattedStringToDouble(phoneAllowance));
                break;
            }
        }
        return this.csvFileReader.WriteToEmpDetailsCSV(allEmployees);
    }
    // </editor-fold>

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Private Methods">
    // <editor-fold defaultstate="collapsed" desc="GetEmpAttendance">
    private List<EmpAttendance> GetEmpAttendance(Optional<String> empNo, Optional<LocalDate> fromDate, Optional<LocalDate> toDate) {
        return csvFileReader.GetEmpAttendance(empNo, fromDate, toDate);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="ComputeLatesDeduction">
    private double ComputeLatesDeduction(int totalLateMinutes, double monthlyBasic) {
        // 1 month assumed = 22 work days, 8 hours/day
        double hourlyRate = monthlyBasic / (22 * 8);
        double perMinuteRate = hourlyRate / 60;
        return totalLateMinutes * perMinuteRate;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="ComputeAbsencesDeduction">
    private double ComputeAbsencesDeduction(int totalAbsentDays, double monthlyBasic) {
        // 1 month assumed = 22 work days
        double dailyRate = monthlyBasic / 22;
        return totalAbsentDays * dailyRate;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="GetSSSContribution">
    private double GetSSSContribution(double monthlyBasic) {
        for (SSSBracket bracket : SSSBracket.values()) {
            if (monthlyBasic >= bracket.GetMin() && monthlyBasic <= bracket.GetMax()) {
                return bracket.GetContribution();
            }
        }
        return 0.0;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="ComputePhilHealth">
    private static double ComputePhilHealth(double monthlyBasic) {
        double rate = 0.0;

        for (PhilHealthBracket bracket : PhilHealthBracket.values()) {
            if (monthlyBasic >= bracket.GetMin() && monthlyBasic <= bracket.GetMax()) {
                rate = bracket.GetRate();
                break;
            }
        }

        double monthlyPremium = monthlyBasic * rate;

        // Apply minimum & maximum bounds
        if (monthlyBasic <= 10_000) {
            monthlyPremium = 300.00;
        } else if (monthlyBasic >= 60_000) {
            monthlyPremium = 1_800.00;
        }

        // Employee share is half
        double employeeShare = monthlyPremium / 2.0;

        return employeeShare;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="ComputePagIbig">
    private static double ComputePagIbig(double monthlyBasic) {
        double employeeRate = 0.0;

        for (PagIbigBracket bracket : PagIbigBracket.values()) {
            if (monthlyBasic >= bracket.getMin() && monthlyBasic <= bracket.getMax()) {
                employeeRate = bracket.getEmployeeRate();
                break;
            }
        }

        double contribution = monthlyBasic * employeeRate;

        // Pag-IBIG maximum cap is ₱100
        contribution = Math.min(contribution, 100.00);

        return contribution;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ComputeWithholdingTax">
    private static double ComputeWithholdingTax(double taxableIncome) {
        double withholdingTax = 0.0;

        for (WithholdingTaxBracket bracket : WithholdingTaxBracket.values()) {
            if (taxableIncome >= bracket.GetMin() && taxableIncome <= bracket.GetMax()) {
                if (bracket == WithholdingTaxBracket.EXEMPT) {
                    withholdingTax = 0.0;
                } else {
                    withholdingTax = bracket.GetBaseTax() + (taxableIncome - bracket.GetMin()) * bracket.GetRate();
                }
                break;
            }
        }

        return withholdingTax;
    }
    // </editor-fold>

    // </editor-fold>
}
