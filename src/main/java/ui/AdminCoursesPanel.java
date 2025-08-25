package ui;

import model.Course;
import repo.InMemoryCourseRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class AdminCoursesPanel extends JPanel {

    private final JTextField tfSearch = new JTextField(16);

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Mã HP","Tên học phần","TC","Tiên quyết"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    private final InMemoryCourseRepository repo = InMemoryCourseRepository.getInstance();

    public AdminCoursesPanel() {
        setLayout(new BorderLayout(8,8));

        // === Thanh công cụ ===
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JButton btnReload = new JButton("Làm mới");
        JButton btnAdd    = new JButton("Thêm học phần");
        JButton btnDel    = new JButton("Xóa");
        top.add(new JLabel("Tìm (mã/tên):"));
        top.add(tfSearch);
        top.add(btnReload);
        top.add(btnAdd);
        top.add(btnDel);
        add(top, BorderLayout.NORTH);

        tfSearch.addActionListener(e -> load());
        btnReload.addActionListener(e -> load());
        btnAdd.addActionListener(e -> addCourse());
        btnDel.addActionListener(e -> deleteSelected());

        // === Bảng ===
        table.setRowHeight(22);
        table.getTableHeader().setReorderingAllowed(false);
        add(new JScrollPane(table), BorderLayout.CENTER);

        load();
    }

    private void load() {
        model.setRowCount(0);
        String q = tfSearch.getText().trim().toLowerCase();

        // Lấy list, sort theo mã
        List<Course> list = new ArrayList<>(repo.findAll());
        list.sort(Comparator.comparing(Course::getCode));

        for (Course c : list) {
            if (!q.isEmpty()) {
                String hay = (c.getCode() + " " + c.getName()).toLowerCase();
                if (!hay.contains(q)) continue;
            }
            String prereq = c.getPrerequisites()==null || c.getPrerequisites().isEmpty()
                    ? "" : String.join(",", c.getPrerequisites());
            model.addRow(new Object[]{ c.getCode(), c.getName(), c.getCredits(), prereq });
        }
    }

    private void addCourse() {
        JTextField tfCode = new JTextField();
        JTextField tfName = new JTextField();
        JSpinner spCredits = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));
        JTextField tfPrereq = new JTextField(); // CSV: CS101,MATH101

        JPanel p = new JPanel(new GridLayout(0,1,6,6));
        p.add(new JLabel("Mã học phần:"));  p.add(tfCode);
        p.add(new JLabel("Tên học phần:")); p.add(tfName);
        p.add(new JLabel("Số tín chỉ:"));   p.add(spCredits);
        p.add(new JLabel("Tiên quyết (CSV):")); p.add(tfPrereq);

        if (JOptionPane.showConfirmDialog(this, p, "Thêm học phần",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {

            String code = tfCode.getText().trim().toUpperCase();
            String name = tfName.getText().trim();
            int credits = (Integer) spCredits.getValue();

            if (code.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Mã/Tên không được để trống");
                return;
            }
            if (repo.findByCode(code) != null) {
                JOptionPane.showMessageDialog(this, "Mã học phần đã tồn tại");
                return;
            }

            Set<String> prereq = new TreeSet<>();
            if (!tfPrereq.getText().isBlank()) {
                for (String s : tfPrereq.getText().split(",")) {
                    String v = s.trim().toUpperCase();
                    if (!v.isEmpty()) prereq.add(v);
                }
            }

            repo.save(new Course(code, name, credits, prereq));
            load();
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn 1 dòng để xóa");
            return;
        }
        String code = String.valueOf(model.getValueAt(row, 0));
        int ok = JOptionPane.showConfirmDialog(this,
                "Xóa học phần " + code + "?",
                "Xác nhận", JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            try {
                repo.delete(code); // cần có InMemoryCourseRepository.delete(code)
                load();
            } catch (Throwable t) {
                // Nếu repo chưa có delete(code), fallback xóa thủ công
                try {
                    var all = new ArrayList<>(repo.findAll());
                    all.removeIf(c -> c.getCode().equalsIgnoreCase(code));
                    // Ghi đè lại (repo của bạn có thể không hỗ trợ bulk set – giữ tạm như này)
                    // Nếu repo đã có delete, bạn sẽ không vào nhánh này.
                    JOptionPane.showMessageDialog(this, "Repo chưa hỗ trợ delete(code). Hãy thêm method delete(code) vào InMemoryCourseRepository.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Không thể xóa: " + ex.getMessage());
                }
            }
        }
    }
}
