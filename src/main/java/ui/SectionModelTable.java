package ui;

import model.Section;
import service.RegistrationService;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SectionModelTable extends AbstractTableModel {
    private final String[] cols = {
            "Mã lớp", "Mã HP", "Học kỳ", "Thứ", "Bắt đầu", "Kết thúc", "Phòng", "Sức chứa", "GV", "ĐK/SC"
    };
    private final List<Section> data = new ArrayList<>();
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
    private final RegistrationService svc = new RegistrationService();

    public void setSections(List<Section> list){
        data.clear(); data.addAll(list); fireTableDataChanged();
    }
    public Section get(int row){ return data.get(row); }

    @Override public int getRowCount(){ return data.size(); }
    @Override public int getColumnCount(){ return cols.length; }
    @Override public String getColumnName(int c){ return cols[c]; }

    @Override public Object getValueAt(int r, int c){
        var s = data.get(r);
        return switch (c){
            case 0 -> s.getId();
            case 1 -> s.getCourseCode();
            case 2 -> s.getTerm();
            case 3 -> s.getDayOfWeek();
            case 4 -> fmt.format(s.getStart());
            case 5 -> fmt.format(s.getEnd());
            case 6 -> s.getRoom();
            case 7 -> s.getCapacity();
            case 8 -> s.getLecturer();
            case 9 -> svc.countRegistered(s.getId()) + "/" + s.getCapacity(); // ✅ số đã đăng ký / sức chứa
            default -> "";
        };
    }
}
