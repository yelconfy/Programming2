package Objects.table_model;

import Objects.models.EmpDetail;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class EmpDetailTableModel extends AbstractTableModel {

    private List<EmpDetail> pageData;

    public EmpDetailTableModel(List<EmpDetail> _pageData) {
        this.pageData = _pageData;
    }
    
    public void setPageData(List<EmpDetail> _pageData) {
        this.pageData = _pageData;
        fireTableDataChanged(); // refresh table
    }

    @Override
    public int getRowCount() {
        return pageData.size();
    }

    @Override
    public int getColumnCount() {
       return EmpDetail.DISPLAY_FIELDS.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return pageData.get(rowIndex).GetDisplayFieldValue(columnIndex);
    }

    @Override
    public String getColumnName(int column) {
         return EmpDetail.DISPLAY_FIELDS[column];
    }
}
