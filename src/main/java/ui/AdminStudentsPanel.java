package ui;

import model.Student;
import model.StudentProfile;
import repo.InMemoryStudentProfileRepository;
import repo.InMemoryStudentRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class AdminStudentsPanel extends JPanel {

    private final JTextField tfSearch = new JTextField(16);

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Họ tên", "Ngành", "Khóa", "Lớp"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    private final InMemoryStudentRepository stuRepo = InMemoryStudentRepository.getInstance();
    private final InMemoryStudentProfileRepository profileRepo = InMemoryStudentProfileRepository.getInstance();

    public AdminStudentsPanel() {
        setLayout(new BorderLayout(8,8));

        // ===== Thanh công cụ =====
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JButton btnReload = new JButton("Làm mới");
        JButton btnAdd    = new JButton("Thêm SV");
        JButton btnEdit   = new JButton("Sửa hồ sơ");
        JButton btnDel    = new JButton("Xóa");
        top.add(new JLabel("Tìm (ID/tên/ngành/khóa/lớp):"));
        top.add(tfSearch);
        top.add(btnReload);
        top.add(btnAdd);
        top.add(btnEdit);
        top.add(btnDel);
        add(top, BorderLayout.NORTH);

        tfSearch.addActionListener(e -> load());
        btnReload.addActionListener(e -> load());
        btnAdd.addActionListener(e -> addStudent());
        btnEdit.addActionListener(e -> editSelected());
        btnDel.addActionListener(e -> deleteSelected());

        // ===== Bảng =====
        table.setRowHeight(22);
        table.getTableHeader().setReorderingAllowed(false);
        add(new JScrollPane(table), BorderLayout.CENTER);

        load();
    }

    /* ================= Data ================= */

    private void load() {
        model.setRowCount(0);
        String q = tfSearch.getText().trim().toLowerCase();

        List<Student> list = new ArrayList<>(stuRepo.findAll());
        list.sort(Comparator.comparing(Student::getId));

        for (Student s : list) {
            StudentProfile p = profileRepo.find(s.getId());
            String major = p != null ? nz(p.getMajor()) : "";
            String cohort = p != null ? nz(p.getCohort()) : "";
            String clazz = p != null ? nz(p.getClassCode()) : "";

            if (!q.isEmpty()) {
                String hay = (s.getId()+" "+s.getName()+" "+major+" "+cohort+" "+clazz).toLowerCase();
                if (!hay.contains(q)) continue;
            }
            model.addRow(new Object[]{s.getId(), s.getName(), major, cohort, clazz});
        }
    }

    private static String nz(String v){ return v==null? "" : v; }

    /* ================= CRUD ================= */

    private void addStudent() {
        JTextField tfName   = new JTextField();
        JTextField tfMajor  = new JTextField();
        JTextField tfCohort = new JTextField("K2025");
        JTextField tfClass  = new JTextField();

        JPanel p = new JPanel(new GridLayout(0,1,6,6));
        p.add(new JLabel("Họ tên:"));          p.add(tfName);
        p.add(new JLabel("Ngành:"));           p.add(tfMajor);
        p.add(new JLabel("Khóa:"));            p.add(tfCohort);
        p.add(new JLabel("Lớp (tuỳ chọn):"));  p.add(tfClass);

        if (JOptionPane.showConfirmDialog(this, p, "Thêm sinh viên",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            String name = tfName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Họ tên không được trống"); return;
            }

            // Tạo ID đơn giản: S + 2 chữ số tăng dần, fallback UUID nếu repo bạn không có COUNTER
            String id = genNextId();

            // user/pass mặc định = id / "123"
            Student s = new Student(id, id, "123", name, 18);
            stuRepo.save(s);

            StudentProfile sp = new StudentProfile(id);
            sp.setMajor(tfMajor.getText().trim());
            sp.setCohort(tfCohort.getText().trim());
            sp.setClassCode(tfClass.getText().trim());
            profileRepo.save(sp);

            load();
            JOptionPane.showMessageDialog(this, "Đã tạo SV: " + id + " (mật khẩu mặc định: 123)");
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn 1 dòng để sửa"); return; }

        String id = String.valueOf(model.getValueAt(row, 0));
        Student s = stuRepo.findById(id);
        StudentProfile p = profileRepo.find(id);
        if (s == null) { JOptionPane.showMessageDialog(this, "Không tìm thấy SV"); return; }
        if (p == null) p = new StudentProfile(id);

        JTextField tfName   = new JTextField(s.getName());
        JTextField tfMajor  = new JTextField(nz(p.getMajor()));
        JTextField tfCohort = new JTextField(nz(p.getCohort()));
        JTextField tfClass  = new JTextField(nz(p.getClassCode()));

        JPanel dlg = new JPanel(new GridLayout(0,1,6,6));
        dlg.add(new JLabel("ID: " + id));
        dlg.add(new JLabel("Họ tên:")); dlg.add(tfName);
        dlg.add(new JLabel("Ngành:"));  dlg.add(tfMajor);
        dlg.add(new JLabel("Khóa:"));   dlg.add(tfCohort);
        dlg.add(new JLabel("Lớp:"));    dlg.add(tfClass);

        if (JOptionPane.showConfirmDialog(this, dlg, "Sửa hồ sơ",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            s.setName(tfName.getText().trim());
            stuRepo.save(s); // ghi đè

            p.setMajor(tfMajor.getText().trim());
            p.setCohort(tfCohort.getText().trim());
            p.setClassCode(tfClass.getText().trim());
            profileRepo.save(p);

            load();
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn 1 dòng để xóa"); return; }

        String id = String.valueOf(model.getValueAt(row, 0));
        if (JOptionPane.showConfirmDialog(this,
                "Xóa sinh viên " + id + " ?",
                "Xác nhận", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                stuRepo.delete(id);
            } catch (Throwable ignore) {
                JOptionPane.showMessageDialog(this, "Repo SV chưa hỗ trợ delete(id). Hãy thêm method delete vào InMemoryStudentRepository.");
            }
            try {
                profileRepo.delete(id);
            } catch (Throwable ignore) {
                // nếu repo profile chưa có delete cũng không sao
            }
            load();
        }
    }

    private String genNextId() {
        // Nếu repo bạn có COUNTER riêng, hãy dùng. Ở đây làm nhẹ: tìm max "Snn"
        int max = 0;
        for (Student s : stuRepo.findAll()) {
            String id = s.getId();
            if (id != null && id.matches("S\\d{2}")) {
                int n = Integer.parseInt(id.substring(1));
                if (n > max) max = n;
            }
        }
        int next = max + 1;
        if (next <= 99) return String.format("S%02d", next);
        // fallback UUID rút gọn
        return "S" + UUID.randomUUID().toString().substring(0,4).toUpperCase();
    }
}
