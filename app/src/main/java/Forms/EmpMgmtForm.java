package Forms;

import Helper.AmountUtil;
import Helper.DateTimeUtil;
import Helper.GuidUtil;
import Helper.Injector;
import Helper.Paginator;
import Helper.TableUtil;
import Interface.IBaseFrame;
import Interface.IEmpMgmtProcess;
import Objects.enums.Constants.EmpStatus;
import Objects.enums.Constants.ScreenState;
import Objects.enums.Constants.ShiftTime;
import Objects.models.AddressInfo;
import Objects.models.CompensationInfo;
import Objects.models.EmpDetail;
import Objects.table_model.EmpDetailTableModel;
import java.awt.Component;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class EmpMgmtForm extends javax.swing.JFrame implements IBaseFrame {

    private final IEmpMgmtProcess empMgmtProcess;

    private Paginator<EmpDetail> paginator;
    private EmpDetail selectedEmp;
    private int state = ScreenState.DEFAULT.GetKey();

    public EmpMgmtForm(IEmpMgmtProcess _empMgmtProcess) {
        initComponents();
        this.empMgmtProcess = _empMgmtProcess;

        this.CenterFrame(this);

        this.PopulateTable(null);
        this.PopulatePicker();
        this.PushActionListeners();
        this.ControlComponentState(this.state);
    }

    private void PushActionListeners() {

        this.empDetailsGrid.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                this.MapSelectedEmpDetail();
            }
        });

        this.prevPageBtn.addActionListener(e -> {
            if (this.paginator.hasPreviousPage()) {
                this.paginator.previousPage();
                TableUtil.populate(empDetailsGrid, paginator.getCurrentPage(), EmpDetailTableModel::new);
                this.SelectFirstRow();
            }
        });

        this.nextPageBtn.addActionListener(e -> {
            if (paginator.hasNextPage()) {
                this.paginator.nextPage();
                TableUtil.populate(empDetailsGrid, paginator.getCurrentPage(), EmpDetailTableModel::new);
                this.SelectFirstRow();
            }
        });

        this.searchBtn.addActionListener(e -> {
            String query = this.searchFieldTxt.getText();
            List<EmpDetail> searchResults = this.empMgmtProcess.SearchEmployee(query);
            this.PopulateTable(searchResults);
        });

        this.addBtn.addActionListener(e -> {
            this.state = ScreenState.ADD.GetKey();
            this.ControlComponentState(this.state);
            this.ClearTextFields(this.jPanel1);
        });

        this.editBtn.addActionListener(e -> {
            this.state = ScreenState.EDIT.GetKey();
            this.ControlComponentState(this.state);
        });

        this.deleteBtn.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete the selected record?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION
            );

            if (result == JOptionPane.YES_OPTION) {
                this.DelEmployee();
            }
        });

        this.saveBtn.addActionListener(e -> {
            this.CreateEmployee();
        });

        this.cancelBtn.addActionListener(e -> {
            this.state = ScreenState.DEFAULT.GetKey();
            this.ControlComponentState(this.state);
            this.SelectFirstRow();
        });

        this.logOutBtn.addActionListener(e -> {
            this.PerformLogout();
        });

    }

    private void SelectFirstRow() {
        SwingUtilities.invokeLater(() -> {
            if (empDetailsGrid.getRowCount() > 0) {
                // Clear any existing selection
                empDetailsGrid.clearSelection();

                // Re-select the first row
                empDetailsGrid.setRowSelectionInterval(0, 0);

                // Scroll to make it visible
                empDetailsGrid.scrollRectToVisible(empDetailsGrid.getCellRect(0, 0, true));
            }
        });
    }

    private void PopulateTable(List<EmpDetail> searchResults) {
        List<EmpDetail> dataToShow;

        if (searchResults == null) {
            // No search: get all employees
            dataToShow = this.empMgmtProcess.GetEmpDetails()
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

    private void PopulatePicker() {
        List<EmpDetail> empDetails = this.empMgmtProcess.GetEmpDetails();

        // Use a Set for unique positions
        Set<String> uniquePositions = new HashSet<>();

        for (EmpDetail emp : empDetails) {
            uniquePositions.add(emp.GetPosition());
        }

        // Clear combo box first (optional)
        positionPicker.removeAllItems();

        // Add unique positions
        for (String position : uniquePositions) {
            positionPicker.addItem(position);
        }

        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        for (EmpStatus status : EmpStatus.values()) {
            model.addElement(status.toString()); // or getDisplayName()
        }

        this.employmentStatusPicker.setModel(model);
    }

    private void MapSelectedEmpDetail() {
        int selectedRow = empDetailsGrid.getSelectedRow();

        if (selectedRow < 0) {
            return;
        }

        this.selectedEmp = this.paginator.getCurrentPage().get(selectedRow);

        this.lastNameTxt.setText(this.selectedEmp.GetLastName());
        this.firstNameTxt.setText(this.selectedEmp.GetFirstName());
        this.birthdayTxt.setText(DateTimeUtil.formatDate(this.selectedEmp.GetBirthday()));

        this.mobileNoTxt.setText(this.selectedEmp.GetPhoneNo());
        this.personalEmailTxt.setText(this.selectedEmp.GetEmail());

        this.houseBlkLotNoTxt.setText(this.selectedEmp.GetAddress().GetHouseBlkLotNo());
        this.streetTxt.setText(this.selectedEmp.GetAddress().GetStreet());
        this.brgyTxt.setText(this.selectedEmp.GetAddress().GetBarangay());
        this.cityMunicipalityTxt.setText(this.selectedEmp.GetAddress().GetCityMunicipality());
        this.provinceTxt.setText(this.selectedEmp.GetAddress().GetProvince());
        this.zipCodeTxt.setText(this.selectedEmp.GetAddress().GetZipCode());

        this.sssNoTxt.setText(this.selectedEmp.GetSssNo());
        this.philHealthNoTxt.setText(this.selectedEmp.GetPhilHealthNo());
        this.pagIbigNoTxt.setText(this.selectedEmp.GetPagIbigNo());
        this.tinNoTxt.setText(this.selectedEmp.GetTinNo());

        this.employmentStatusPicker.setSelectedItem(this.selectedEmp.GetEmpStatus());
        this.immediateSupervisorTxt.setText(this.selectedEmp.GetImmSupervisor());
        this.positionPicker.setSelectedItem(this.selectedEmp.GetPosition());
        this.dateHiredTxt.setText(DateTimeUtil.formatDate(this.selectedEmp.GetDateHired()));

        this.basicSalaryTxt.setText(AmountUtil.FormatAmount(this.selectedEmp.GetCompensation().GetBasicSalary()));
        this.riceSubsidyTxt.setText(AmountUtil.FormatAmount(this.selectedEmp.GetCompensation().GetRiceSubsidy()));
        this.phoneAllowanceTxt.setText(AmountUtil.FormatAmount(this.selectedEmp.GetCompensation().GetPhoneAllowance()));
        this.clothingAllowanceTxt.setText(AmountUtil.FormatAmount(this.selectedEmp.GetCompensation().GetClothingAllowance()));
        this.hourlyRateLabel.setText(AmountUtil.FormatAmount(this.selectedEmp.GetCompensation().GetHourlyRate()));
    }

    private void ControlComponentState(int state) {
        if (state == ScreenState.DEFAULT.GetKey()) {
            this.SetPanelEnabled(this.jPanel1, false);
            this.SetPanelEnabled(this.jPanel2, true);
        } else {
            this.SetPanelEnabled(this.jPanel1, true);
            this.SetPanelEnabled(this.jPanel2, false);
        }
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

    private void ClearTextFields(JComponent parent) {
        this.hourlyRateLabel.setText("");
        for (Component c : parent.getComponents()) {
            if (c instanceof JTextField) {
                ((JTextField) c).setText("");
            } else if (c instanceof JComponent) {
                // Recurse for nested panels, tab panes, etc.
                ClearTextFields((JComponent) c);
            }
        }
    }

    private void CreateEmployee() {

        List<EmpDetail> allEmployees = this.empMgmtProcess.GetEmpDetails();
        String lastEmpNo = allEmployees.get(allEmployees.size() - 1).GetEmpNo();

        String empNo = this.state == ScreenState.ADD.GetKey() ? GuidUtil.GenerateEmpNo(lastEmpNo) : this.selectedEmp.GetEmpNo();
        String lastName = this.lastNameTxt.getText();
        String firstName = this.firstNameTxt.getText();
        LocalDate birthday = DateTimeUtil.parseDate(this.birthdayTxt.getText());

        String houseBlkLotNo = this.houseBlkLotNoTxt.getText();
        String street = this.streetTxt.getText();
        String barangay = this.brgyTxt.getText();
        String cityMunicipality = this.cityMunicipalityTxt.getText();
        String province = this.provinceTxt.getText();
        String zipCode = this.zipCodeTxt.getText();

        AddressInfo address = new AddressInfo(empNo);
        address.SetHouseBlkLotNo(houseBlkLotNo);
        address.SetStreet(street);
        address.SetBarangay(barangay);
        address.SetCityMunicipality(cityMunicipality);
        address.SetProvince(province);
        address.SetZipCode(zipCode);

        String email = this.personalEmailTxt.getText();
        String phoneNo = this.mobileNoTxt.getText();
        String sssNo = this.sssNoTxt.getText();
        String philHealthNo = this.philHealthNoTxt.getText();
        String tinNo = this.tinNoTxt.getText();
        String pagIbigNo = this.pagIbigNoTxt.getText();

        String empStatus = (String) this.employmentStatusPicker.getSelectedItem();
        String position = (String) this.positionPicker.getSelectedItem();
        String immSupervisor = this.immediateSupervisorTxt.getText();

        CompensationInfo compensation = new CompensationInfo(empNo);
        compensation.SetBasicSalary(AmountUtil.ParseFormattedStringToDouble(this.basicSalaryTxt.getText()));
        compensation.SetClothingAllowance(AmountUtil.ParseFormattedStringToDouble(this.clothingAllowanceTxt.getText()));

        double grossSemiMonthlyRate = AmountUtil.ParseFormattedStringToDouble(this.clothingAllowanceTxt.getText()) / 2.0;
        compensation.SetGrossSemiMonthlyRate(grossSemiMonthlyRate);

        double shiftLen = ShiftTime.SHIFT_LENGTH.GetShiftLength().toHours() * 1.0;
        double hourlyRate = AmountUtil.ParseFormattedStringToDouble(this.basicSalaryTxt.getText()) / 21.75 / shiftLen;
        compensation.SetHourlyRate(hourlyRate);

        compensation.SetPhoneAllowance(AmountUtil.ParseFormattedStringToDouble(this.phoneAllowanceTxt.getText()));
        compensation.SetRiceSubsidy(AmountUtil.ParseFormattedStringToDouble(this.riceSubsidyTxt.getText()));

        LocalDate dateHired = DateTimeUtil.parseDate(this.dateHiredTxt.getText());
        boolean status = true;

        EmpDetail newEmployee = new EmpDetail(
                empNo,
                lastName,
                firstName,
                birthday,
                address,
                email,
                phoneNo,
                sssNo,
                philHealthNo,
                tinNo,
                pagIbigNo,
                empStatus,
                position,
                immSupervisor,
                compensation,
                dateHired,
                status
        );

        boolean isSuccess = this.state == ScreenState.ADD.GetKey() ? this.empMgmtProcess.AddEmployee(newEmployee) : this.empMgmtProcess.UpdateEmployee(newEmployee);

        String message = "";
        String title = "";
        int messageType = -2;

        if (isSuccess == true) {
            message = this.state == ScreenState.ADD.GetKey() ? "Record added successfully!" : "Record Updated successfully!";
            title = "Information";
            messageType = 1; // For Information Message
        } else {
            message = this.state == ScreenState.ADD.GetKey() ? "Record wasn't added" : "Record wasn't updated";
            title = "Error";
            messageType = 0; // For Error Message
        }

        JOptionPane.showMessageDialog(
                null,
                message,
                title,
                messageType
        );

        ControlComponentState(ScreenState.DEFAULT.GetKey());
        PopulateTable(null);
    }

    private void DelEmployee() {
        String empNo = this.selectedEmp.GetEmpNo();
        boolean isSuccess = this.empMgmtProcess.DeleteEmployee(empNo);

        String message = "";
        String title = "";
        int messageType = -2;

        if (isSuccess == true) {
            message = "Record Deleted successfully!";
            title = "Information";
            messageType = 1; // For Information Message
        } else {
            message = "Record wasn't deleted";
            title = "Error";
            messageType = 0; // For Error Message
        }

        JOptionPane.showMessageDialog(
                null,
                message,
                title,
                messageType
        );

        ControlComponentState(ScreenState.DEFAULT.GetKey());
        PopulateTable(null);
    }
    
    // <editor-fold defaultstate="collapsed" desc="DO NOT TOUCH">

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        cancelBtn = new javax.swing.JButton();
        saveBtn = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        lastNameTxt = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        firstNameTxt = new javax.swing.JTextField();
        birthdayTxt = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        mobileNoTxt = new javax.swing.JTextField();
        personalEmailTxt = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel33 = new javax.swing.JLabel();
        houseBlkLotNoTxt = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        streetTxt = new javax.swing.JTextField();
        brgyTxt = new javax.swing.JTextField();
        cityMunicipalityTxt = new javax.swing.JTextField();
        provinceTxt = new javax.swing.JTextField();
        zipCodeTxt = new javax.swing.JTextField();
        jLabel40 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        immediateSupervisorTxt = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        dateHiredTxt = new javax.swing.JTextField();
        employmentStatusPicker = new javax.swing.JComboBox<>();
        positionPicker = new javax.swing.JComboBox<>();
        jPanel7 = new javax.swing.JPanel();
        jLabel41 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        sssNoTxt = new javax.swing.JTextField();
        philHealthNoTxt = new javax.swing.JTextField();
        pagIbigNoTxt = new javax.swing.JTextField();
        jLabel48 = new javax.swing.JLabel();
        tinNoTxt = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        basicSalaryTxt = new javax.swing.JTextField();
        riceSubsidyTxt = new javax.swing.JTextField();
        phoneAllowanceTxt = new javax.swing.JTextField();
        jLabel53 = new javax.swing.JLabel();
        clothingAllowanceTxt = new javax.swing.JTextField();
        jLabel54 = new javax.swing.JLabel();
        hourlyRateLabel = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        deleteBtn = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        empDetailsGrid = new javax.swing.JTable();
        prevPageBtn = new javax.swing.JButton();
        nextPageBtn = new javax.swing.JButton();
        searchFieldTxt = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        searchBtn = new javax.swing.JButton();
        editBtn = new javax.swing.JButton();
        addBtn = new javax.swing.JButton();
        logOutBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3));

        cancelBtn.setText("Cancel");

        saveBtn.setText("Save");

        jLabel23.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel23.setText("<html><u>PERSONAL DETAILS</html></u>");

        jLabel25.setText("Last Name:");

        lastNameTxt.setEnabled(false);

        jLabel29.setText("First Name:");

        firstNameTxt.setEnabled(false);

        birthdayTxt.setEnabled(false);

        jLabel30.setText("Birthday:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel25)
                            .addComponent(jLabel29)
                            .addComponent(jLabel30))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lastNameTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                            .addComponent(firstNameTxt)
                            .addComponent(birthdayTxt))))
                .addGap(15, 15, 15))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(lastNameTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(firstNameTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(birthdayTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(10, Short.MAX_VALUE))
        );

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel14.setText("<html><u>CONTACT DETAILS</html></u>");

        jLabel7.setText("Mobile Number:");

        mobileNoTxt.setEnabled(false);

        personalEmailTxt.setEnabled(false);

        jLabel16.setText("Personal Email:");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addGap(18, 18, 18)
                        .addComponent(personalEmailTxt))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(mobileNoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(mobileNoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(personalEmailTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jLabel33.setText("House/Block/Lot Number:");

        houseBlkLotNoTxt.setEnabled(false);

        jLabel36.setText("Street:");

        streetTxt.setEnabled(false);

        brgyTxt.setEnabled(false);

        cityMunicipalityTxt.setEnabled(false);

        provinceTxt.setEnabled(false);

        zipCodeTxt.setEnabled(false);

        jLabel40.setText("ZIP Code:");

        jLabel39.setText("Province:");

        jLabel38.setText("City/Municipality:");

        jLabel37.setText("Barangay:");

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel18.setText("<html><u>ADDRESS DETAILS<html><u>");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel38)
                    .addComponent(jLabel37)
                    .addComponent(jLabel39)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addComponent(jLabel40)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 101, Short.MAX_VALUE)
                            .addComponent(zipCodeTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel33)
                                .addComponent(jLabel36))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(provinceTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                                .addComponent(brgyTxt)
                                .addComponent(streetTxt)
                                .addComponent(houseBlkLotNoTxt)
                                .addComponent(cityMunicipalityTxt)))))
                .addGap(10, 16, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33)
                    .addComponent(houseBlkLotNoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel36)
                    .addComponent(streetTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel37)
                    .addComponent(brgyTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel38)
                    .addComponent(cityMunicipalityTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel39)
                    .addComponent(provinceTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel40)
                    .addComponent(zipCodeTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel11.setText("<html><u>EMPLOYEMENT DETAILS</html></u>");

        jLabel12.setText("Immediate Supervisor:");

        jLabel24.setText("Position:");

        jLabel26.setText("Employement Status:");

        immediateSupervisorTxt.setEnabled(false);

        jLabel27.setText("Date Hired:");

        dateHiredTxt.setEnabled(false);

        employmentStatusPicker.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "EmpStatus" }));

        positionPicker.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel27)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(jLabel26)
                            .addComponent(jLabel24))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(immediateSupervisorTxt)
                            .addComponent(dateHiredTxt)
                            .addComponent(employmentStatusPicker, javax.swing.GroupLayout.Alignment.TRAILING, 0, 154, Short.MAX_VALUE)
                            .addComponent(positionPicker, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(10, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(positionPicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(immediateSupervisorTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(employmentStatusPicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(dateHiredTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14))
        );

        jLabel41.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel41.setText("<html><u>STATUTORY DETAILS</html></u>");

        jLabel43.setText("PhilHealth Number:");

        jLabel45.setText("SSS Number::");

        jLabel46.setText("Pag-Ibig Number:");

        sssNoTxt.setEnabled(false);

        philHealthNoTxt.setEnabled(false);

        pagIbigNoTxt.setEnabled(false);

        jLabel48.setText("TIN Number:");

        tinNoTxt.setEnabled(false);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel43)
                            .addComponent(jLabel45))
                        .addGap(14, 14, 14)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(philHealthNoTxt)
                            .addComponent(sssNoTxt)))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel48)
                        .addGap(50, 50, 50)
                        .addComponent(tinNoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel46)
                        .addGap(24, 24, 24)
                        .addComponent(pagIbigNoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(20, 20, 20))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel45)
                    .addComponent(sssNoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel43)
                    .addComponent(philHealthNoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel46)
                    .addComponent(pagIbigNoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel48)
                    .addComponent(tinNoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14))
        );

        jLabel49.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel49.setText("<html><u>COMPENSATION DETAILS</html></u>");

        jLabel50.setText("Rice Subsidy:");

        jLabel51.setText("Basic Salary:");

        jLabel52.setText("Phone Allowance:");

        basicSalaryTxt.setEnabled(false);

        riceSubsidyTxt.setEnabled(false);

        phoneAllowanceTxt.setEnabled(false);

        jLabel53.setText("Clothing Allowance:");

        clothingAllowanceTxt.setEnabled(false);

        jLabel54.setText("Hourly Rate:");

        hourlyRateLabel.setText(" ");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel51, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel50, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(56, 56, 56)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(basicSalaryTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(riceSubsidyTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel53)
                            .addComponent(jLabel54)
                            .addComponent(jLabel52))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(phoneAllowanceTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                            .addComponent(hourlyRateLabel)
                            .addComponent(clothingAllowanceTxt))))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel51)
                    .addComponent(basicSalaryTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel50)
                    .addComponent(riceSubsidyTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel52)
                    .addComponent(phoneAllowanceTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel53)
                    .addComponent(clothingAllowanceTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel54)
                    .addComponent(hourlyRateLabel))
                .addGap(35, 35, 35))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(saveBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cancelBtn))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(16, 16, 16))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(80, 80, 80)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(saveBtn)
                            .addComponent(cancelBtn)))
                    .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(35, Short.MAX_VALUE))
        );

        jLabel35.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel35.setText("MotorPH Employee Management System");

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3));

        deleteBtn.setText("Delete");

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

        nextPageBtn.setText("Next Page");

        jLabel15.setText("Search:");

        searchBtn.setText("Search");

        editBtn.setText("Edit");

        addBtn.setText("Add");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(prevPageBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nextPageBtn))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel15)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(searchFieldTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(searchBtn)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(addBtn)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(editBtn)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(deleteBtn))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1153, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(19, 19, 19))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(deleteBtn)
                            .addComponent(editBtn)
                            .addComponent(addBtn)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(searchFieldTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(searchBtn))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(prevPageBtn)
                    .addComponent(nextPageBtn))
                .addContainerGap())
        );

        logOutBtn.setText("Logout");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(17, 17, 17))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 717, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(135, 135, 135)
                .addComponent(logOutBtn)
                .addGap(43, 43, 43))
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

    /**
     * @param args the command line arguments
     */
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
            java.util.logging.Logger.getLogger(EmpMgmtForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EmpMgmtForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EmpMgmtForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EmpMgmtForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                EmpMgmtForm empMgmtForm = Injector.createEmpMgmtForm();
                empMgmtForm.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addBtn;
    private javax.swing.JTextField basicSalaryTxt;
    private javax.swing.JTextField birthdayTxt;
    private javax.swing.JTextField brgyTxt;
    private javax.swing.JButton cancelBtn;
    private javax.swing.JTextField cityMunicipalityTxt;
    private javax.swing.JTextField clothingAllowanceTxt;
    private javax.swing.JTextField dateHiredTxt;
    private javax.swing.JButton deleteBtn;
    private javax.swing.JButton editBtn;
    private javax.swing.JTable empDetailsGrid;
    private javax.swing.JComboBox<String> employmentStatusPicker;
    private javax.swing.JTextField firstNameTxt;
    private javax.swing.JLabel hourlyRateLabel;
    private javax.swing.JTextField houseBlkLotNoTxt;
    private javax.swing.JTextField immediateSupervisorTxt;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField lastNameTxt;
    private javax.swing.JButton logOutBtn;
    private javax.swing.JTextField mobileNoTxt;
    private javax.swing.JButton nextPageBtn;
    private javax.swing.JTextField pagIbigNoTxt;
    private javax.swing.JTextField personalEmailTxt;
    private javax.swing.JTextField philHealthNoTxt;
    private javax.swing.JTextField phoneAllowanceTxt;
    private javax.swing.JComboBox<String> positionPicker;
    private javax.swing.JButton prevPageBtn;
    private javax.swing.JTextField provinceTxt;
    private javax.swing.JTextField riceSubsidyTxt;
    private javax.swing.JButton saveBtn;
    private javax.swing.JButton searchBtn;
    private javax.swing.JTextField searchFieldTxt;
    private javax.swing.JTextField sssNoTxt;
    private javax.swing.JTextField streetTxt;
    private javax.swing.JTextField tinNoTxt;
    private javax.swing.JTextField zipCodeTxt;
    // End of variables declaration//GEN-END:variables
    // </editor-fold>
}
