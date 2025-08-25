package ui;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentTableModel extends AbstractTableModel {
    private final String[] cols = {"Mã lớp", "Mã HP", "Tên học phần", "Thứ", "Bắt đầu", "Kết thúc", "TC"};
    private final List<EnrollmentRow> data = new ArrayList<>();

    public void setRows(List<EnrollmentRow> rows){ data.clear(); data.addAll(rows); fireTableDataChanged(); }
    public EnrollmentRow get(int r){ return data.get(r); }

    @Override public int getRowCount(){ return data.size(); }
    @Override public int getColumnCount(){ return cols.length; }
    @Override public String getColumnName(int c){ return cols[c]; }

    @Override public Object getValueAt(int r, int c){
        var e = data.get(r);
        return switch (c){
            case 0 -> e.sectionId();
            case 1 -> e.courseCode();
            case 2 -> e.courseName();
            case 3 -> e.day();
            case 4 -> e.start();
            case 5 -> e.end();
            case 6 -> e.credits();
            default -> "";
        };
    }
}
