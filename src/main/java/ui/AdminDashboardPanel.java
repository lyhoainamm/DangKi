package ui;

import model.Section;
import repo.InMemoryCourseRepository;
import repo.InMemorySectionRepository;
import repo.InMemoryStudentRepository;
import service.RegistrationService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AdminDashboardPanel extends JPanel {

    private final JLabel lblCourses  = new JLabel("0", SwingConstants.CENTER);
    private final JLabel lblSections = new JLabel("0", SwingConstants.CENTER);
    private final JLabel lblStudents = new JLabel("0", SwingConstants.CENTER);
    private final JLabel lblEnrolls  = new JLabel("0", SwingConstants.CENTER);

    public AdminDashboardPanel(){
        setLayout(new BorderLayout());
        setBackground(new Color(0xF3F6FC));

        JPanel grid = new JPanel(new GridLayout(1, 4, 16, 16));
        grid.setBorder(new EmptyBorder(16,16,16,16));
        grid.setOpaque(false);

        grid.add(statCard(lblCourses,  "Học phần"));
        grid.add(statCard(lblSections, "Lớp học phần"));
        grid.add(statCard(lblStudents, "Sinh viên"));
        grid.add(statCard(lblEnrolls,  "Đăng ký (kỳ chọn)"));

        add(grid, BorderLayout.NORTH);

        refreshStats();
    }

    private JComponent statCard(JLabel bigLabel, String label){
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xDDE3F0),1,true),
                new EmptyBorder(16,16,16,16)
        ));
        bigLabel.setFont(bigLabel.getFont().deriveFont(Font.BOLD, 28f));
        bigLabel.setForeground(new Color(0x1B3C7A));

        JLabel lb = new JLabel(label, SwingConstants.CENTER);
        lb.setForeground(Color.DARK_GRAY);

        p.add(bigLabel, BorderLayout.CENTER);
        p.add(lb, BorderLayout.SOUTH);
        return p;
    }

    public void refreshStats(){
        int courses  = InMemoryCourseRepository.getInstance().findAll().size();
        int students = InMemoryStudentRepository.getInstance().findAll().size();

        // Sections: dùng findAll() nếu có; nếu không, cộng theo các kỳ mặc định
        Collection<Section> allSections;
        try {
            allSections = InMemorySectionRepository.getInstance().findAll();
        } catch (Throwable t) {
            // fallback theo kỳ
            List<Section> tmp = new ArrayList<>();
            tmp.addAll(InMemorySectionRepository.getInstance().findByTerm("2025A"));
            tmp.addAll(InMemorySectionRepository.getInstance().findByTerm("2025B"));
            allSections = tmp;
        }
        int sections = allSections.size();

        // Enrolls: cộng tổng đăng ký qua RegistrationService để tránh phụ thuộc repo
        var svc = new RegistrationService();
        int enrolls = 0;
        for (Section s : allSections) {
            try {
                enrolls += svc.listEnrollmentsBySection(s.getId()).size();
            } catch (Throwable ignore) { /* nếu panel enroll chưa sẵn sàng thì bỏ qua */ }
        }

        lblCourses.setText(String.valueOf(courses));
        lblSections.setText(String.valueOf(sections));
        lblStudents.setText(String.valueOf(students));
        lblEnrolls.setText(String.valueOf(enrolls));
    }
}
