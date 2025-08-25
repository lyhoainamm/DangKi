package ui;

import model.Course;
import model.Enrollment;
import model.Section;
import model.User;
import service.RegistrationService;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class StudentDashboardFrame extends JFrame {
    private final User user;
    private final RegistrationService svc = new RegistrationService();

    private final JComboBox<String> cbTerm = new JComboBox<>(new String[]{"2025A", "2025B"});
    private final JList<Course> lstCourses = new JList<>();
    // ĐỔI: SectionModelTable -> SectionTableModel
    private final SectionModelTable sectionModel = new SectionModelTable();
    private final JTable tblSections = new JTable(sectionModel);
    private final EnrollmentTableModel enrollModel = new EnrollmentTableModel();
    private final JTable tblEnroll = new JTable(enrollModel);
    private final JLabel lbStatus = new JLabel(" ");

    public StudentDashboardFrame(User user){
        super("Sinh viên: " + user.getName());
        this.user = user;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8,8));

        // Thanh trên cùng
        add(buildTop(), BorderLayout.NORTH);

        // Tạo Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Đăng ký", buildRegisterTab());                 // tab cũ gói lại
        tabs.addTab("Bảng điểm", new StudentTranscriptPanel(user)); // tab mới
        add(tabs, BorderLayout.CENTER);

        // Thanh trạng thái
        add(buildBottom(), BorderLayout.SOUTH);

        loadCourses();
        refreshEnrollments();
    }

    private JPanel buildTop(){
        var p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.add(new JLabel("Học kỳ:"));
        p.add(cbTerm);

        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.addActionListener(e -> doLogout());
        p.add(btnLogout);

        cbTerm.addActionListener(e -> onTermChanged());
        return p;
    }

    /** Gói giao diện đăng ký vào 1 JPanel để đưa vào Tab "Đăng ký" */
    private JPanel buildRegisterTab(){
        JPanel root = new JPanel(new BorderLayout(8,8));

        // Left: course list
        var left = new JPanel(new BorderLayout());
        left.setBorder(new TitledBorder("Học phần"));
        lstCourses.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstCourses.addListSelectionListener(e -> onCourseSelected());
        left.add(new JScrollPane(lstCourses), BorderLayout.CENTER);

        // Center: sections of selected course
        var mid = new JPanel(new BorderLayout());
        mid.setBorder(new TitledBorder("Lớp học phần"));
        mid.add(new JScrollPane(tblSections), BorderLayout.CENTER);
        var btnReg = new JButton("Đăng ký");
        btnReg.addActionListener(e -> doRegister());
        mid.add(btnReg, BorderLayout.SOUTH);

        // Right: current enrollments (TKB)
        var right = new JPanel(new BorderLayout());
        right.setBorder(new TitledBorder("Thời khóa biểu"));
        right.add(new JScrollPane(tblEnroll), BorderLayout.CENTER);
        var btnDrop = new JButton("Hủy đăng ký");
        btnDrop.addActionListener(e -> doDrop());
        right.add(btnDrop, BorderLayout.SOUTH);

        var split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, mid), right);
        split.setResizeWeight(0.33);

        root.add(split, BorderLayout.CENTER);
        return root;
    }

    private JPanel buildBottom(){
        var p = new JPanel(new BorderLayout());
        p.add(lbStatus, BorderLayout.CENTER);
        return p;
    }

    private String term(){ return (String) cbTerm.getSelectedItem(); }

    private void loadCourses(){
        DefaultListModel<Course> m = new DefaultListModel<>();
        for (var c : svc.listAllCourses()) m.addElement(c);
        lstCourses.setModel(m);
        if (!m.isEmpty()) lstCourses.setSelectedIndex(0);
    }

    private void onTermChanged(){
        onCourseSelected();
        refreshEnrollments();
    }

    private void onCourseSelected(){
        var c = lstCourses.getSelectedValue();
        if (c == null){ sectionModel.setSections(new ArrayList<>()); return; }
        var list = svc.listSectionsByCourseAndTerm(c.getCode(), term());
        sectionModel.setSections(new ArrayList<>(list));
    }

    private void refreshEnrollments(){
        var fmt = DateTimeFormatter.ofPattern("HH:mm");
        List<EnrollmentRow> rows = new ArrayList<>();
        for (Enrollment e : svc.listEnrollments(user.getId(), term())){
            Section s = svc.findSection(e.getSectionId());
            var course = svc.findCourse(s.getCourseCode());
            rows.add(new EnrollmentRow(s.getId(), course.getCode(), course.getName(),
                    s.getDayOfWeek().toString(), fmt.format(s.getStart()), fmt.format(s.getEnd()), course.getCredits()));
        }
        enrollModel.setRows(rows);
        lbStatus.setText("Tổng lớp: " + rows.size());
    }

    private void doRegister(){
        int row = tblSections.getSelectedRow();
        if (row < 0){ JOptionPane.showMessageDialog(this, " Đăng ký"); return; }
        var sec = sectionModel.get(row);
        var msg = svc.register(user.getId(), sec.getId());
        if (msg == null){ lbStatus.setText("Đăng ký thành công: " + sec.getId()); refreshEnrollments(); }
        else JOptionPane.showMessageDialog(this, msg, "Không thể đăng ký", JOptionPane.WARNING_MESSAGE);
    }

    private void doDrop(){
        int row = tblEnroll.getSelectedRow();
        if (row < 0){ JOptionPane.showMessageDialog(this, "Chọn một lớp trong TKB để hủy"); return; }
        var er = enrollModel.get(row);
        var msg = svc.drop(user.getId(), er.sectionId());
        if (msg == null){ lbStatus.setText("Đã hủy: " + er.sectionId()); refreshEnrollments(); }
        else JOptionPane.showMessageDialog(this, msg, "Không thể hủy", JOptionPane.WARNING_MESSAGE);
    }

    private void doLogout(){
        dispose();
        new LoginFrame().setVisible(true);
    }
}
