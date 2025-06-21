package Forms;

import Helper.AmountUtil;
import Helper.Injector;
import Helper.Paginator;
import Helper.TableUtil;
import Interface.IBaseFrame;
import Interface.IPayrollProcess;
import Objects.enums.Constants.Months;
import Objects.models.EmpDeductions;
import Objects.models.EmpDetail;
import Objects.models.EmpPaySlip;
import Objects.models.WorkedHoursSummary;
import Objects.table_model.EmpDetailTableModel;
import java.awt.Component;
import javax.swing.ButtonGroup;
import javax.swing.SwingUtilities;
import javax.swing.JComponent;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;

public class PayrollForm extends javax.swing.JFrame implements IBaseFrame {

    private final IPayrollProcess payrollProcess;

    private Paginator<EmpDetail> paginator;
    private LocalDate fromDate, toDate;
    private EmpDetail selectedEmp;
    private boolean isFirstCutOff = true;
    private Months selectedMonth;
    private Integer selectedYear;
    private boolean isEditMode = false;

    private final ButtonGroup btnGroup = new ButtonGroup();

    public PayrollForm(IPayrollProcess _payrollProcess) {
        initComponents();
        this.payrollProcess = _payrollProcess;

        this.CenterFrame(this);

        this.btnGroup.add(firstCutOffRdBtn);
        this.btnGroup.add(secondCutOffRdBtn);

        this.PopulateTable(null);

        this.PushActionListeners();
        ControlComponentState(this.isEditMode);
    }

    private void PushActionListeners() {

        empDetailsGrid.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                this.MapSelectedEmpDetail();
            }
        });

        firstCutOffRdBtn.addActionListener(e -> {
            if (firstCutOffRdBtn.isSelected()) {
                secondCutOffRdBtn.setSelected(false);
                this.isFirstCutOff = true;

                this.MapSelectedEmpDetail();
            }
        });

        secondCutOffRdBtn.addActionListener(e -> {
            if (secondCutOffRdBtn.isSelected()) {
                firstCutOffRdBtn.setSelected(false);
                this.isFirstCutOff = false;
                this.MapSelectedEmpDetail();
            }
        });

        monthPicker.addActionListener(e -> {
            this.MapSelectedEmpDetail();
        });

        yearPicker.addActionListener(e -> {
            this.MapSelectedEmpDetail();
        });

        this.logOutBtn.addActionListener(e -> {
            this.PerformLogout();
        });
    }

    private void PopulateTable(List<EmpDetail> searchResults) {
        List<EmpDetail> dataToShow;

        if (searchResults == null) {
            // No search: get all employees
            dataToShow = payrollProcess.GetEmpDetails()
                    .stream()
                    .filter(emp -> emp.GetStatus() == true)
                    .collect(Collectors.toList());
        } else {
            // Search mode: use filtered results
            dataToShow = searchResults;
        }

        this.paginator = new Paginator<>(dataToShow, 10);
        TableUtil.populate(empDetailsGrid, this.paginator.getCurrentPage(), EmpDetailTableModel::new);

        SelectFirstRow();
    }

    private void MapSelectedEmpDetail() {
        int selectedRow = empDetailsGrid.getSelectedRow();

        if (selectedRow < 0) {
            return;
        }

        GetDate();
        this.selectedEmp = this.paginator.getCurrentPage().get(selectedRow);
        WorkedHoursSummary hoursWorked = this.payrollProcess.CalculateHoursWorked(this.selectedEmp.GetEmpNo(), this.fromDate, this.toDate);
        EmpDeductions empDeductions = this.payrollProcess.ComputeEmployeeDeductions(this.selectedEmp, this.fromDate, this.toDate);
        EmpPaySlip empPaySlip = this.payrollProcess.GenerateEmpPaySlip(this.selectedEmp, this.fromDate, this.toDate, hoursWorked, empDeductions);

        this.hrsWorkedLabel.setText(String.valueOf(ComputeHoursWorked(hoursWorked)));
        this.basicSalaryLabel.setText(AmountUtil.FormatAmount(this.selectedEmp.GetCompensation().GetBasicSalary()));
        this.hourlyRateLabel.setText(AmountUtil.FormatAmount(this.selectedEmp.GetCompensation().GetHourlyRate()));
        this.grossSemiMonthlyRateLabel.setText(AmountUtil.FormatAmount(this.selectedEmp.GetCompensation().GetGrossSemiMonthlyRate()));
        this.otPayLabel.setText(AmountUtil.FormatAmount(this.payrollProcess.ComputeOvertimePay(hoursWorked, this.selectedEmp.GetCompensation().GetHourlyRate())));
        this.riceSubsidyTxt.setText(AmountUtil.FormatAmount(this.selectedEmp.GetCompensation().GetRiceSubsidy()));
        this.phoneAllowanceTxt.setText(AmountUtil.FormatAmount(this.selectedEmp.GetCompensation().GetPhoneAllowance()));
        this.clothingAllowanceTxt.setText(AmountUtil.FormatAmount(this.selectedEmp.GetCompensation().GetClothingAllowance()));
        this.withholdingTaxLabel.setText(AmountUtil.FormatAmount(empDeductions.GetWithholdingTax()));
        this.sssContribLabel.setText(AmountUtil.FormatAmount(empDeductions.GetSssContribution()));
        this.philHealthContribLabel.setText(AmountUtil.FormatAmount(empDeductions.GetPhilHealthContribution()));
        this.pagIbigContribLabel.setText(AmountUtil.FormatAmount(empDeductions.GetPagIbigContribution()));
        this.latesDeductionLabel.setText(AmountUtil.FormatAmount(empDeductions.GetLatesDeduction()));
        this.absencesDeductionLabel.setText(AmountUtil.FormatAmount(empDeductions.GetAbsencesDeduction()));
        this.totalDeductionsLabel1.setText(AmountUtil.FormatAmount(empDeductions.GetTotalDeductions()));
        this.grossPayLabel.setText(AmountUtil.FormatAmount(empPaySlip.GetGrossPay()));
        this.totalDeductionsLabel2.setText(totalDeductionsLabel1.getText());
        this.netPayLabel.setText(AmountUtil.FormatAmount(empPaySlip.GetNetPay()));
    }

    private void ControlComponentState(boolean isEditMode) {
        // Allow editing of individual allowance fields
        this.riceSubsidyTxt.setEnabled(isEditMode);
        this.phoneAllowanceTxt.setEnabled(isEditMode);
        this.clothingAllowanceTxt.setEnabled(isEditMode);

        // Use your recursive helper for the entire panel
        SetPanelEnabled(this.jPanel2, !isEditMode);

        // Specific buttons always follow edit mode
        this.saveBtn.setEnabled(isEditMode);
        this.cancelBtn.setEnabled(isEditMode);
    }

    private void GetDate() {
        this.selectedMonth = Months.valueOf(((String) monthPicker.getSelectedItem()).toUpperCase());
        this.selectedYear = Integer.parseInt((String) yearPicker.getSelectedItem());

        this.fromDate = LocalDate.of(this.selectedYear, this.selectedMonth.GetValue(), this.isFirstCutOff ? 1 : 16);
        this.toDate = this.isFirstCutOff ? LocalDate.of(this.selectedYear, this.selectedMonth.GetValue(), 15) : this.fromDate.withDayOfMonth(this.fromDate.lengthOfMonth());
    }

    private int ComputeHoursWorked(WorkedHoursSummary summary) {
        return summary.GetRegularHours()
                + summary.GetWeekendHours()
                + summary.GetHolidayHours()
                + summary.GetOvertimeHours();
    }

    private void SelectFirstRow() {
        SwingUtilities.invokeLater(() -> {
            if (empDetailsGrid.getRowCount() > 0) {
                empDetailsGrid.setRowSelectionInterval(0, 0);
                empDetailsGrid.scrollRectToVisible(empDetailsGrid.getCellRect(0, 0, true));
            }
        });
    }

    private void SetPanelEnabled(JComponent panel, boolean enabled) {
        panel.setEnabled(enabled);
        for (Component c : panel.getComponents()) {
            if (c instanceof JComponent) {
                SetPanelEnabled((JComponent) c, enabled); // recurse
            } else {
                c.setEnabled(enabled);
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="AUTO-GENERATED">
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        basicSalaryLabel = new javax.swing.JLabel();
        hourlyRateLabel = new javax.swing.JLabel();
        grossSemiMonthlyRateLabel = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        hrsWorkedLabel = new javax.swing.JLabel();
        riceSubsidyTxt = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        phoneAllowanceTxt = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        clothingAllowanceTxt = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        otPayLabel = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        withholdingTaxLabel = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        sssContribLabel = new javax.swing.JLabel();
        philHealthContribLabel = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        pagIbigContribLabel = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        latesDeductionLabel = new javax.swing.JLabel();
        absencesDeductionLabel = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        totalDeductionsLabel1 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        totalDeductionsLabel2 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        netPayLabel = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        grossPayLabel = new javax.swing.JLabel();
        saveBtn = new javax.swing.JButton();
        cancelBtn = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        editBtn = new javax.swing.JButton();
        generateSlipBtn = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        empDetailsGrid = new javax.swing.JTable();
        prevPageBtn = new javax.swing.JButton();
        nextPageBtn = new javax.swing.JButton();
        searchFieldTxt = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        searchBtn = new javax.swing.JButton();
        monthPicker = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        firstCutOffRdBtn = new javax.swing.JRadioButton();
        secondCutOffRdBtn = new javax.swing.JRadioButton();
        jLabel17 = new javax.swing.JLabel();
        yearPicker = new javax.swing.JComboBox<>();
        jLabel35 = new javax.swing.JLabel();
        logOutBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3));

        jLabel1.setText("Basic Salary:");

        jLabel2.setText("Hourly Rate:");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setText("COMPENSATION DETAILS");

        jLabel4.setText("Gross Semi-Monthly Rate:");

        basicSalaryLabel.setText(" ");

        hourlyRateLabel.setText(" ");

        grossSemiMonthlyRateLabel.setText(" ");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel8.setText("ADDITIONAL PAY");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel9.setText("ALLOWANCES");

        jLabel10.setText("Rice Subsidy:");

        jLabel13.setText("Hours Worked:");

        hrsWorkedLabel.setText("{hoursWorked}");

        riceSubsidyTxt.setEnabled(false);

        jLabel20.setText("Phone Allowance: ");

        phoneAllowanceTxt.setEnabled(false);

        jLabel21.setText("Clothing Allowance:");

        clothingAllowanceTxt.setEnabled(false);

        jLabel22.setText("Overtime Pay:");

        otPayLabel.setText(" ");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel11.setText("DEDUCTIONS");

        jLabel12.setText("PhilHealth Contribution:");

        withholdingTaxLabel.setText("{withHoldingTax}");

        jLabel18.setText("Witholding Tax:");

        jLabel24.setText("SSS Contribution:");

        sssContribLabel.setText("{sssContrib}");

        philHealthContribLabel.setText("{philHealthContrib}");

        jLabel26.setText("Pag-Ibig Contribution:");

        pagIbigContribLabel.setText("{pagIbigContrib}");

        jLabel28.setText("Lates:");

        latesDeductionLabel.setText("{lates}");

        absencesDeductionLabel.setText("{absences}");

        jLabel31.setText("Absences:");

        jLabel32.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel32.setText("TOTAL DEDUCTIONS:");

        totalDeductionsLabel1.setText("{totalDeductions}");

        jLabel19.setText("Gross Pay:");

        jLabel34.setText("Total Deductions: ");

        totalDeductionsLabel2.setText("{totalDeduct}");

        jLabel42.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel42.setText("NET PAY:");

        netPayLabel.setText("{netPay}");

        jLabel44.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel44.setText("SUMMARY BREAKDOWN");

        grossPayLabel.setText("{grossPay}");

        saveBtn.setText("Save");
        saveBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveBtnActionPerformed(evt);
            }
        });

        cancelBtn.setText("Cancel");
        cancelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(54, 54, 54)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(grossSemiMonthlyRateLabel)
                                    .addComponent(jLabel4)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(basicSalaryLabel)
                                            .addComponent(jLabel1))
                                        .addGap(49, 49, 49)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(hourlyRateLabel)
                                            .addComponent(jLabel2)))
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel22)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(otPayLabel)))
                                .addGap(75, 75, 75)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(jLabel32)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(totalDeductionsLabel1))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(jLabel31)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(absencesDeductionLabel))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(jLabel28)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(latesDeductionLabel))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(jLabel26)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(pagIbigContribLabel))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                            .addComponent(jLabel24)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(sssContribLabel))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                            .addComponent(jLabel18)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(withholdingTaxLabel))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                            .addComponent(jLabel12)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(philHealthContribLabel))))
                                .addGap(75, 75, 75)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(jLabel42)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(netPayLabel))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                            .addComponent(jLabel34)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 72, Short.MAX_VALUE)
                                            .addComponent(totalDeductionsLabel2))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                            .addComponent(jLabel19)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(grossPayLabel))))))
                        .addContainerGap(76, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel20)
                                    .addComponent(jLabel21)
                                    .addComponent(jLabel10))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(riceSubsidyTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(clothingAllowanceTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(saveBtn)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(cancelBtn))
                                    .addComponent(phoneAllowanceTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(hrsWorkedLabel)))
                        .addGap(31, 31, 31))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(hrsWorkedLabel))
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(basicSalaryLabel))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(hourlyRateLabel)))
                        .addGap(19, 19, 19)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(grossSemiMonthlyRateLabel)
                        .addGap(24, 24, 24)
                        .addComponent(jLabel8)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel22)
                            .addComponent(otPayLabel))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(riceSubsidyTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel20)
                            .addComponent(phoneAllowanceTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(clothingAllowanceTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel21)
                            .addComponent(saveBtn)
                            .addComponent(cancelBtn)))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel44)
                            .addGap(18, 18, 18)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel19)
                                .addComponent(grossPayLabel))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel34)
                                .addComponent(totalDeductionsLabel2))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel42)
                                .addComponent(netPayLabel))
                            .addGap(112, 112, 112))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel11)
                            .addGap(18, 18, 18)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel18)
                                .addComponent(withholdingTaxLabel))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel24)
                                .addComponent(sssContribLabel))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel12)
                                .addComponent(philHealthContribLabel))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel26)
                                .addComponent(pagIbigContribLabel))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel28)
                                .addComponent(latesDeductionLabel))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel31)
                                .addComponent(absencesDeductionLabel))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel32)
                                .addComponent(totalDeductionsLabel1)))))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3));

        editBtn.setText("Edit");
        editBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editBtnActionPerformed(evt);
            }
        });

        generateSlipBtn.setText("Generate Slip");

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerifyInputWhenFocusTarget(false);

        empDetailsGrid.setFont(new java.awt.Font("Tw Cen MT", 1, 12)); // NOI18N
        empDetailsGrid.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Employee Number", "Last Name", "First Name", "Position"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        empDetailsGrid.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(empDetailsGrid);

        prevPageBtn.setText("Previous Page");
        prevPageBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prevPageBtnActionPerformed(evt);
            }
        });

        nextPageBtn.setText("Next Page");
        nextPageBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextPageBtnActionPerformed(evt);
            }
        });

        jLabel15.setText("Search:");

        searchBtn.setText("Search");
        searchBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchBtnActionPerformed(evt);
            }
        });

        monthPicker.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" }));

        jLabel16.setText("Month:");

        firstCutOffRdBtn.setSelected(true);
        firstCutOffRdBtn.setText("1st Cut-Off");

        secondCutOffRdBtn.setText("2nd Cut-Off");

        jLabel17.setText("Year:");

        yearPicker.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2024", "2025", "2026", "2027", "2028", "2029", "2030" }));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                            .addComponent(prevPageBtn)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(nextPageBtn))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 870, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel15)
                            .addComponent(jLabel16))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(searchFieldTxt)
                            .addComponent(monthPicker, 0, 126, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(yearPicker, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(firstCutOffRdBtn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(secondCutOffRdBtn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(editBtn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(generateSlipBtn))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(searchBtn)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap(32, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(searchFieldTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchBtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(firstCutOffRdBtn)
                        .addComponent(secondCutOffRdBtn))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(yearPicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel17))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(editBtn)
                        .addComponent(generateSlipBtn)
                        .addComponent(monthPicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel16)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(prevPageBtn)
                    .addComponent(nextPageBtn))
                .addGap(15, 15, 15))
        );

        jLabel35.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel35.setText("MotorPH PayRoll System");

        logOutBtn.setText("Logout");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 432, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(165, 165, 165)
                        .addComponent(logOutBtn))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(10, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel35)
                    .addComponent(logOutBtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void nextPageBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextPageBtnActionPerformed
        if (paginator.hasNextPage()) {
            paginator.nextPage();
            TableUtil.populate(empDetailsGrid, paginator.getCurrentPage(), EmpDetailTableModel::new);
            SelectFirstRow();
        }
    }//GEN-LAST:event_nextPageBtnActionPerformed

    private void prevPageBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevPageBtnActionPerformed
        if (paginator.hasPreviousPage()) {
            paginator.previousPage();
            TableUtil.populate(empDetailsGrid, paginator.getCurrentPage(), EmpDetailTableModel::new);
            SelectFirstRow();
        }
    }//GEN-LAST:event_prevPageBtnActionPerformed

    private void searchBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBtnActionPerformed
        String query = this.searchFieldTxt.getText();
        List<EmpDetail> searchResults = this.payrollProcess.SearchEmployee(query);
        PopulateTable(searchResults);
    }//GEN-LAST:event_searchBtnActionPerformed

    private void saveBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveBtnActionPerformed
        String riceSubsidy = this.riceSubsidyTxt.getText();
        String clothingAllowance = this.clothingAllowanceTxt.getText();
        String phoneAllowance = this.phoneAllowanceTxt.getText();

        boolean isSuccess = this.payrollProcess.UpdateAllowances(this.selectedEmp, riceSubsidy, clothingAllowance, phoneAllowance);

        String message = "";
        String title = "";
        int messageType = -2;

        if (isSuccess == true) {
            message = "Record updated successfully!";
            title = "Information";
            messageType = 1; // For Information Message
        } else {
            message = "Record wasn't updated";
            title = "Error";
            messageType = 0; // For Error Message
        }

        JOptionPane.showMessageDialog(
                null,
                message,
                title,
                messageType
        );

        this.isEditMode = false;
        ControlComponentState(this.isEditMode);
        PopulateTable(null);
    }//GEN-LAST:event_saveBtnActionPerformed

    private void cancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelBtnActionPerformed
        this.isEditMode = false;
        ControlComponentState(this.isEditMode);
    }//GEN-LAST:event_cancelBtnActionPerformed

    private void editBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editBtnActionPerformed
        this.isEditMode = true;
        ControlComponentState(this.isEditMode);
    }//GEN-LAST:event_editBtnActionPerformed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PayrollForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PayrollForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PayrollForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PayrollForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                PayrollForm payrollForm = Injector.createPayrollForm();
                payrollForm.setVisible(true);
            }
        });
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DO NO TOUCH">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel absencesDeductionLabel;
    private javax.swing.JLabel basicSalaryLabel;
    private javax.swing.JButton cancelBtn;
    private javax.swing.JTextField clothingAllowanceTxt;
    private javax.swing.JButton editBtn;
    private javax.swing.JTable empDetailsGrid;
    private javax.swing.JRadioButton firstCutOffRdBtn;
    private javax.swing.JButton generateSlipBtn;
    private javax.swing.JLabel grossPayLabel;
    private javax.swing.JLabel grossSemiMonthlyRateLabel;
    private javax.swing.JLabel hourlyRateLabel;
    private javax.swing.JLabel hrsWorkedLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel latesDeductionLabel;
    private javax.swing.JButton logOutBtn;
    private javax.swing.JComboBox<String> monthPicker;
    private javax.swing.JLabel netPayLabel;
    private javax.swing.JButton nextPageBtn;
    private javax.swing.JLabel otPayLabel;
    private javax.swing.JLabel pagIbigContribLabel;
    private javax.swing.JLabel philHealthContribLabel;
    private javax.swing.JTextField phoneAllowanceTxt;
    private javax.swing.JButton prevPageBtn;
    private javax.swing.JTextField riceSubsidyTxt;
    private javax.swing.JButton saveBtn;
    private javax.swing.JButton searchBtn;
    private javax.swing.JTextField searchFieldTxt;
    private javax.swing.JRadioButton secondCutOffRdBtn;
    private javax.swing.JLabel sssContribLabel;
    private javax.swing.JLabel totalDeductionsLabel1;
    private javax.swing.JLabel totalDeductionsLabel2;
    private javax.swing.JLabel withholdingTaxLabel;
    private javax.swing.JComboBox<String> yearPicker;
    // End of variables declaration//GEN-END:variables
 // </editor-fold>
}
