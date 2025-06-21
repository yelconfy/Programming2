package Forms;

import Helper.Injector;
import Helper.Paginator;
import Helper.TableUtil;
import Interface.IBaseFrame;
import Interface.ITimeKeepingProcess;
import Objects.enums.Constants;
import Objects.enums.Constants.Months;
import Objects.models.EmpAttendance;
import Objects.models.EmpDetail;
import Objects.table_model.EmpAttendanceTableModel;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingUtilities;

public class TimeKeepingForm extends javax.swing.JFrame implements IBaseFrame {

    private final ITimeKeepingProcess timeKeepingProcess;

    private LocalDate fromDate, toDate;

    private Paginator<EmpAttendance> paginator;
    private EmpAttendance selectedRecord;
    private int state = Constants.ScreenState.DEFAULT.GetKey();

    public TimeKeepingForm(ITimeKeepingProcess _timeKeepingProcess) {
        initComponents();
        
        this.CenterFrame(this);

        this.timeKeepingProcess = _timeKeepingProcess;

        this.PopulatePicker();
        this.PushActionListeners();
//        this.SearchEmpAttendanceRecord(this.searchFieldTxt.getText());
    }

    private void PushActionListeners() {
        this.logOutBtn.addActionListener(e -> {
            this.PerformLogout();
        });

        this.nextPageBtn.addActionListener(e -> {
            if (paginator.hasNextPage()) {
                this.paginator.nextPage();
                TableUtil.populate(this.empAttendanceGrid, paginator.getCurrentPage(), EmpAttendanceTableModel::new);
                this.SelectFirstRow();
            }
        });

        this.prevPageBtn.addActionListener(e -> {
            if (this.paginator.hasPreviousPage()) {
                this.paginator.previousPage();
                TableUtil.populate(this.empAttendanceGrid, paginator.getCurrentPage(), EmpAttendanceTableModel::new);
                this.SelectFirstRow();
            }
        });

        this.searchBtn.addActionListener(e -> {
            this.SearchEmpAttendanceRecord(this.searchFieldTxt.getText());
        });
        
        this.fromMonthPicker.addActionListener(e -> {
            this.SearchEmpAttendanceRecord(this.searchFieldTxt.getText());
        });

        this.toMonthPicker.addActionListener(e -> {
            this.SearchEmpAttendanceRecord(this.searchFieldTxt.getText());
        });

        this.fromDayPicker.addActionListener(e -> {
            this.SearchEmpAttendanceRecord(this.searchFieldTxt.getText());
        });

        this.toDayPicker.addActionListener(e -> {
            this.SearchEmpAttendanceRecord(this.searchFieldTxt.getText());
        });

        this.fromYearPicker.addActionListener(e -> {
            this.SearchEmpAttendanceRecord(this.searchFieldTxt.getText());
        });

        this.toYearPicker.addActionListener(e -> {
            this.SearchEmpAttendanceRecord(this.searchFieldTxt.getText());
        });
    }

    private void PopulatePicker() {
        DefaultComboBoxModel<String> fromModel = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<String> toModel = new DefaultComboBoxModel<>();
        
        for (Constants.Months month : Constants.Months.values()) {
            fromModel.addElement(month.toString());
            toModel.addElement(month.toString());
        }
        this.fromMonthPicker.setModel(fromModel);
        this.toMonthPicker.setModel(toModel);

        if (fromModel.getSize() > 0) {
            this.fromMonthPicker.setSelectedIndex(0);
            this.toMonthPicker.setSelectedIndex(0);
        }
    }

    private void PopulateTable(List<EmpAttendance> searchResults) {
//        List<EmpAttendance> dataToShow;
//
//        if (searchResults == null || searchResults.isEmpty()) {
//            System.out.println("search result is empty");
//            // No search: get all employees
//            dataToShow = this.timeKeepingProcess.GetEmpAttendance(
//                    Optional.empty(),
//                    Optional.of(this.fromDate),
//                    Optional.of(this.toDate)
//            );
//        } else {
//            System.out.println("search result is not empty");
//            // Search mode: use filtered results
//            dataToShow = searchResults;
//        }
        
        this.paginator = new Paginator<>(searchResults, 35);
        TableUtil.populate(this.empAttendanceGrid, this.paginator.getCurrentPage(), EmpAttendanceTableModel::new);

        this.SelectFirstRow();
    }

    private void SelectFirstRow() {
        SwingUtilities.invokeLater(() -> {
            if (this.empAttendanceGrid.getRowCount() > 0) {
                // Clear any existing selection
                this.empAttendanceGrid.clearSelection();

                // Re-select the first row
                this.empAttendanceGrid.setRowSelectionInterval(0, 0);

                // Scroll to make it visible
                this.empAttendanceGrid.scrollRectToVisible(this.empAttendanceGrid.getCellRect(0, 0, true));
            }
        });
    }

    private void SearchEmpAttendanceRecord(String query) {
        this.GetDate();
        
        List<EmpAttendance> searchResults = this.timeKeepingProcess.GetEmpAttendance(
                (query.isEmpty() || query.isEmpty()) ? Optional.empty() : Optional.of(query),
                Optional.of(this.fromDate),
                Optional.of(this.toDate)
        );

        PopulateTable(searchResults);
    }

    private void GetDate() {
        this.fromDate = LocalDate.of(
                Integer.parseInt((String) fromYearPicker.getSelectedItem()),
                Months.valueOf(((String) fromMonthPicker.getSelectedItem()).toUpperCase()).GetValue(),
                Integer.parseInt((String) fromDayPicker.getSelectedItem())
        );

        this.toDate = LocalDate.of(
                Integer.parseInt((String) toYearPicker.getSelectedItem()),
                Months.valueOf(((String) toMonthPicker.getSelectedItem()).toUpperCase()).GetValue(),
                Integer.parseInt((String) toDayPicker.getSelectedItem())
        );
    }

    // <editor-fold defaultstate="Collapsed" desc="DO NO TOUCH">
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        empAttendanceGrid = new javax.swing.JTable();
        prevPageBtn = new javax.swing.JButton();
        nextPageBtn = new javax.swing.JButton();
        searchFieldTxt = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        searchBtn = new javax.swing.JButton();
        fromMonthPicker = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        fromYearPicker = new javax.swing.JComboBox<>();
        jLabel23 = new javax.swing.JLabel();
        fromDayPicker = new javax.swing.JComboBox<>();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        toMonthPicker = new javax.swing.JComboBox<>();
        jLabel24 = new javax.swing.JLabel();
        toDayPicker = new javax.swing.JComboBox<>();
        jLabel21 = new javax.swing.JLabel();
        toYearPicker = new javax.swing.JComboBox<>();
        jLabel35 = new javax.swing.JLabel();
        logOutBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3));

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerifyInputWhenFocusTarget(false);

        empAttendanceGrid.setFont(new java.awt.Font("Tw Cen MT", 1, 12)); // NOI18N
        empAttendanceGrid.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Employee Number", "Last Name", "First Name", "Date", "Log In", "Log Out"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(empAttendanceGrid);

        prevPageBtn.setText("Previous Page");

        nextPageBtn.setText("Next Page");

        jLabel15.setText("Search:");

        searchBtn.setText("Search");

        jLabel16.setText("Month:");

        jLabel17.setText("Year:");

        fromYearPicker.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2024", "2025", "2026", "2027", "2028", "2029", "2030" }));

        jLabel23.setText("Day:");

        fromDayPicker.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" }));

        jLabel18.setText("From:");

        jLabel19.setText("To:");

        jLabel20.setText("Month:");

        jLabel24.setText("Day:");

        toDayPicker.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" }));

        jLabel21.setText("Year:");

        toYearPicker.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2024", "2025", "2026", "2027", "2028", "2029", "2030" }));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                    .addGap(36, 36, 36)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel19)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel20)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(toMonthPicker, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel24)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(toDayPicker, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel21)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(toYearPicker, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel18)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel16)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(fromMonthPicker, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel23)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(fromDayPicker, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel17)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(fromYearPicker, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel15)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(searchFieldTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(searchBtn)))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(35, 35, 35)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 870, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(25, Short.MAX_VALUE)))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(prevPageBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nextPageBtn)
                .addGap(30, 30, 30))
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
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fromMonthPicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(fromYearPicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17)
                    .addComponent(jLabel23)
                    .addComponent(fromDayPicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(toMonthPicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20)
                    .addComponent(toYearPicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21)
                    .addComponent(jLabel24)
                    .addComponent(toDayPicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 593, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(prevPageBtn)
                    .addComponent(nextPageBtn))
                .addGap(25, 25, 25))
        );

        jLabel35.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel35.setText("MotorPH Time Keeping");

        logOutBtn.setText("Logout");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 432, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(131, 131, 131)
                .addComponent(logOutBtn)
                .addGap(37, 37, 37))
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
            java.util.logging.Logger.getLogger(TimeKeepingForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TimeKeepingForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TimeKeepingForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TimeKeepingForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                TimeKeepingForm timeKeepingForm = Injector.createTimeKeepingForm();
                timeKeepingForm.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable empAttendanceGrid;
    private javax.swing.JComboBox<String> fromDayPicker;
    private javax.swing.JComboBox<String> fromMonthPicker;
    private javax.swing.JComboBox<String> fromYearPicker;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton logOutBtn;
    private javax.swing.JButton nextPageBtn;
    private javax.swing.JButton prevPageBtn;
    private javax.swing.JButton searchBtn;
    private javax.swing.JTextField searchFieldTxt;
    private javax.swing.JComboBox<String> toDayPicker;
    private javax.swing.JComboBox<String> toMonthPicker;
    private javax.swing.JComboBox<String> toYearPicker;
    // End of variables declaration//GEN-END:variables

    // </editor-fold>
}
