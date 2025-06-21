package Objects.table_model;

import Objects.models.EmpAttendance;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class EmpAttendanceTableModel extends AbstractTableModel {

    private List<EmpAttendance> pageData;

    public EmpAttendanceTableModel(List<EmpAttendance> _pageData) {
        this.pageData = _pageData;
    }
    
    public void setPageData(List<EmpAttendance> _pageData) {
        this.pageData = _pageData;
        fireTableDataChanged(); // refresh table
    }

    @Override
    public int getRowCount() {
        return pageData.size();
    }

    @Override
    public int getColumnCount() {
       return EmpAttendance.DISPLAY_FIELDS.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return pageData.get(rowIndex).GetDisplayFieldValue(columnIndex);
    }

    @Override
    public String getColumnName(int column) {
         return EmpAttendance.DISPLAY_FIELDS[column];
    }
}
