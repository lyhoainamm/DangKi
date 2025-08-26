package ui;

import model.Section;
import model.User;
import service.GradeService;
import service.RegistrationService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StudentTranscriptPanel extends JPanel {
    private final RegistrationService reg = new RegistrationService();
    private final GradeService grades = new GradeService();
    private final User user;

    private final JComboBox<String> cbTerm = new JComboBox<>(new String[]{"2025A","2025B"});
    private final JTable tbl = new JTable(new Model());

    public StudentTranscriptPanel(User user){
        this.user = user;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(12,12,12,12));

        add(buildTop(), BorderLayout.NORTH);
        add(new JScrollPane(tbl), BorderLayout.CENTER);

        reload();
    }

    private JComponent buildTop(){
        JPanel top = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Bảng điểm");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0));
        right.add(new JLabel("Học kỳ:"));
        right.add(cbTerm);
        JButton btn = new JButton("Làm mới");
        btn.addActionListener(e -> reload());
        right.add(btn);
        top.add(title, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);
        return top;
    }

    private void reload(){
        String term = (String) cbTerm.getSelectedItem();
        List<Object[]> rows = new ArrayList<>();
        for (var e : reg.listEnrollments(user.getId(), term)){
            Section sec = reg.findSection(e.getSectionId());
            var c = reg.findCourse(sec.getCourseCode());
            double s100 = grades.courseTotal(user.getId(), sec.getId());
            double gpa = grades.toGPA4(s100);
            rows.add(new Object[]{c.getCode(), c.getName(), c.getCredits(),
                    String.format("%.1f", s100), String.format("%.2f", gpa)});
        }
        ((Model) tbl.getModel()).set(rows);

        double termGpa = grades.termGPA(user.getId(), term);
        // Thông báo nhanh GPA học kỳ
        JOptionPane.showMessageDialog(this, "GPA học kỳ " + term + ": " + String.format("%.2f", termGpa));
    }

    static class Model extends AbstractTableModel {
        private final String[] cols = {"Mã HP", "Tên học phần", "TC", "Điểm (100)", "GPA(4.0)"};
        private List<Object[]> data = new ArrayList<>();
        void set(List<Object[]> d){ data = d; fireTableDataChanged(); }
        @Override public int getRowCount(){ return data.size(); }
        @Override public int getColumnCount(){ return cols.length; }
        @Override public String getColumnName(int c){ return cols[c]; }
        @Override public Object getValueAt(int r, int c){ return data.get(r)[c]; }
    }
}
