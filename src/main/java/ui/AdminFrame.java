package ui;

import model.Course;
import model.Section;
import model.User;
import repo.InMemoryCourseRepository;
import repo.InMemorySectionRepository;
import repo.InMemoryStudentRepository;
import repo.SectionRepository;
import service.RegistrationService;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import ui.AdminStudentsPanel;

public class AdminFrame extends JFrame {
    private final User admin;
    private final RegistrationService svc = new RegistrationService();
    private final InMemoryCourseRepository courseRepo = InMemoryCourseRepository.getInstance();
    private final SectionRepository sectionRepo = InMemorySectionRepository.getInstance();

    // Courses tab
    private final CourseTableModel courseModel = new CourseTableModel();
    private final JTable tblCourses = new JTable(courseModel);

    // Sections tab
    private final JComboBox<String> cbTerm = new JComboBox<>(new String[]{"2025A", "2025B"});
    private final SectionModelTable sectionModel = new SectionModelTable();
    private final JTable tblSections = new JTable(sectionModel);

    public AdminFrame(User admin) {
        super("Quản trị - " + admin.getName());
        this.admin = admin;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        // Top bar
        var top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        var btnLogout = new JButton("Đăng xuất");
        btnLogout.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });
        top.add(btnLogout);
        add(top, BorderLayout.NORTH);

        // Tabs
        var tabs = new JTabbedPane();
        tabs.addTab("Học phần", buildCoursesTab());
        tabs.addTab("Lớp học phần", buildSectionsTab());
        tabs.addTab("Sinh viên", new AdminStudentsPanel());   // quản lý SV
        tabs.addTab("Tuyển sinh", new AdminAdmissionsPanel()); // hồ sơ tuyển sinh
        tabs.addTab("Nhập điểm", new LecturerGradePanel());    // nhập điểm/GPA



        // (Nếu bạn đã có 2 tab bổ sung thì giữ nguyên)
        // tabs.addTab("Tuyển sinh", new AdminAdmissionsPanel());
        // tabs.addTab("Nhập điểm", new LecturerGradePanel());

        add(tabs, BorderLayout.CENTER);

        loadCourses();
        refreshSections();
    }

    // ----- Courses -----
    private JComponent buildCoursesTab() {
        var root = new JPanel(new BorderLayout(6,6));
        var box = new JPanel(new BorderLayout());
        box.setBorder(new TitledBorder("Danh sách học phần"));
        box.add(new JScrollPane(tblCourses), BorderLayout.CENTER);

        var actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        var btnAdd = new JButton("Thêm học phần");
        btnAdd.addActionListener(e -> addCourseDialog());
        actions.add(btnAdd);

        root.add(box, BorderLayout.CENTER);
        root.add(actions, BorderLayout.SOUTH);
        return root;
    }

    private void loadCourses() {
        var list = new ArrayList<>(svc.listAllCourses());
        list.sort(Comparator.comparing(Course::getCode));
        courseModel.setData(list);
    }

    private void addCourseDialog() {
        JTextField tfCode = new JTextField(10);
        JTextField tfName = new JTextField(20);
        JSpinner spCredits = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));
        JTextField tfPrereq = new JTextField(20); // CSV: CS101,MATH101

        var p = new JPanel(new GridLayout(0,1,6,6));
        p.add(labeled("Mã học phần:", tfCode));
        p.add(labeled("Tên học phần:", tfName));
        p.add(labeled("Số tín chỉ:", spCredits));
        p.add(labeled("Tiên quyết (CSV):", tfPrereq));

        int ok = JOptionPane.showConfirmDialog(this, p, "Thêm học phần", JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            String code = tfCode.getText().trim().toUpperCase();
            String name = tfName.getText().trim();
            int credits = (Integer) spCredits.getValue();
            var prereq = new java.util.HashSet<String>();
            if (!tfPrereq.getText().isBlank()) {
                for (String s : tfPrereq.getText().split(",")) prereq.add(s.trim().toUpperCase());
            }
            if (code.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Mã/Tên không được để trống"); return;
            }
            if (courseRepo.findByCode(code) != null) {
                JOptionPane.showMessageDialog(this, "Mã học phần đã tồn tại"); return;
            }
            courseRepo.save(new Course(code, name, credits, prereq));
            loadCourses();
        }
    }

    // ----- Sections -----
    private JComponent buildSectionsTab() {
        var root = new JPanel(new BorderLayout(6,6));

        var north = new JPanel(new FlowLayout(FlowLayout.LEFT));
        north.add(new JLabel("Học kỳ:"));
        north.add(cbTerm);
        var btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> refreshSections());
        north.add(btnRefresh);
        root.add(north, BorderLayout.NORTH);

        var center = new JPanel(new BorderLayout());
        center.setBorder(new TitledBorder("Lớp học phần theo kỳ"));
        center.add(new JScrollPane(tblSections), BorderLayout.CENTER);
        root.add(center, BorderLayout.CENTER);

        var actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        var btnAddSec = new JButton("Thêm lớp học phần");
        btnAddSec.addActionListener(e -> addSectionDialog());
        actions.add(btnAddSec);

        // ✅ Nút xem danh sách SV của lớp
        var btnView = new JButton("Xem SV của lớp");
        btnView.addActionListener(e -> showStudentsOfSelected());
        actions.add(btnView);

        root.add(actions, BorderLayout.SOUTH);

        cbTerm.addActionListener(e -> refreshSections());
        return root;
    }

    private void refreshSections() {
        var list = new ArrayList<>(svc.listSectionsByTerm((String) cbTerm.getSelectedItem()));
        sectionModel.setSections(list);
    }

    private void addSectionDialog() {
        JTextField tfId = new JTextField(10);
        JTextField tfCourse = new JTextField(10);
        JComboBox<String> cbTerm2 = new JComboBox<>(new String[]{"2025A", "2025B"});
        JComboBox<DayOfWeek> cbDay = new JComboBox<>(DayOfWeek.values());
        JTextField tfStart = new JTextField("08:00", 6);
        JTextField tfEnd = new JTextField("10:00", 6);
        JTextField tfRoom = new JTextField(8);
        JSpinner spCap = new JSpinner(new SpinnerNumberModel(40, 1, 500, 1));
        JTextField tfLect = new JTextField(12);

        // gợi ý mã tự sinh
        tfId.setText("SEC-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase());

        var p = new JPanel(new GridLayout(0,1,6,6));
        p.add(labeled("Mã lớp:", tfId));
        p.add(labeled("Mã học phần:", tfCourse));
        p.add(labeled("Học kỳ:", cbTerm2));
        p.add(labeled("Thứ:", cbDay));
        p.add(labeled("Bắt đầu (HH:mm):", tfStart));
        p.add(labeled("Kết thúc (HH:mm):", tfEnd));
        p.add(labeled("Phòng:", tfRoom));
        p.add(labeled("Sức chứa:", spCap));
        p.add(labeled("Giảng viên:", tfLect));

        int ok = JOptionPane.showConfirmDialog(this, p, "Thêm lớp học phần", JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            try {
                String id = tfId.getText().trim();
                String courseCode = tfCourse.getText().trim().toUpperCase();
                String term = (String) cbTerm2.getSelectedItem();
                DayOfWeek dow = (DayOfWeek) cbDay.getSelectedItem();
                LocalTime start = LocalTime.parse(tfStart.getText().trim());
                LocalTime end = LocalTime.parse(tfEnd.getText().trim());
                String room = tfRoom.getText().trim();
                int cap = (Integer) spCap.getValue();
                String lect = tfLect.getText().trim();

                if (id.isEmpty() || courseCode.isEmpty() || room.isEmpty() || lect.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin"); return;
                }
                if (sectionRepo.findById(id) != null) {
                    JOptionPane.showMessageDialog(this, "Mã lớp đã tồn tại"); return;
                }
                sectionRepo.save(new Section(id, courseCode, term, dow, start, end, room, cap, lect));
                cbTerm.setSelectedItem(term);
                refreshSections();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ: " + ex.getMessage());
            }
        }
    }

    // ✅ hiển thị danh sách SV của lớp đang chọn
    private void showStudentsOfSelected(){
        int row = tblSections.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn một lớp trong bảng.");
            return;
        }
        var sec = sectionModel.get(row);
        var enrolls = svc.listEnrollmentsBySection(sec.getId());
        if (enrolls.isEmpty()){
            JOptionPane.showMessageDialog(this, "Chưa có sinh viên đăng ký lớp " + sec.getId());
            return;
        }
        StringBuilder sb = new StringBuilder("Sinh viên lớp " + sec.getId() + ":\n");
        var stuRepo = InMemoryStudentRepository.getInstance();
        for (var e : enrolls){
            var st = stuRepo.findById(e.getStudentId());
           String name = (st != null ? st.getName() : e.getStudentId());
            sb.append("- ").append(e.getStudentId()).append(" : ").append(name).append("\n");
        }
        JOptionPane.showMessageDialog(this, sb.toString());
    }

    // helpers
    private JPanel labeled(String title, JComponent comp) {
        var p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.add(new JLabel(title)); p.add(comp); return p;
    }

    // simple table for courses
    static class CourseTableModel extends AbstractTableModel {
        private final String[] cols = {"Mã HP", "Tên học phần", "TC", "Tiên quyết"};
        private final List<Course> data = new ArrayList<>();
        public void setData(List<Course> list){ data.clear(); data.addAll(list); fireTableDataChanged(); }
        @Override public int getRowCount(){ return data.size(); }
        @Override public int getColumnCount(){ return cols.length; }
        @Override public String getColumnName(int c){ return cols[c]; }
        @Override public Object getValueAt(int r, int c){
            var x = data.get(r);
            return switch (c){
                case 0 -> x.getCode();
                case 1 -> x.getName();
                case 2 -> x.getCredits();
                case 3 -> x.getPrerequisites().isEmpty()? "" : String.join(",", x.getPrerequisites());
                default -> "";
            };
        }
    }
}
