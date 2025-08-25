package ui;

import model.GradeComponent;
import service.GradeService;
import service.RegistrationService;

import javax.swing.*;
import java.awt.*;

public class LecturerGradePanel extends JPanel {
    private final RegistrationService reg = new RegistrationService();
    private final GradeService grades = new GradeService();

    private final JComboBox<String> cbSection = new JComboBox<>();
    // Danh sách sinh viên của lớp được chọn
    private final JComboBox<String> cbStudent = new JComboBox<>();
    private final JComboBox<GradeComponent> cbComp = new JComboBox<>(GradeComponent.values());
    private final JSpinner spScore = new JSpinner(new SpinnerNumberModel(80.0, 0.0, 100.0, 1.0));
    private final JSpinner spWeight = new JSpinner(new SpinnerNumberModel(0.5, 0.0, 1.0, 0.1));

    public LecturerGradePanel(){
        setLayout(new BorderLayout(6,6));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Lớp:")); top.add(cbSection);
        top.add(new JLabel("SV:")); top.add(cbStudent);
        top.add(new JLabel("Thành phần:")); top.add(cbComp);
        top.add(new JLabel("Điểm:")); top.add(spScore);
        top.add(new JLabel("Trọng số:")); top.add(spWeight);

        JButton btn = new JButton("Ghi điểm");
        btn.addActionListener(e -> {
            String sec = (String) cbSection.getSelectedItem();
            String stu = (String) cbStudent.getSelectedItem();
            if (sec == null || stu == null) {
                JOptionPane.showMessageDialog(this, "Chọn lớp và sinh viên.");
                return;
            }
            grades.enterGrade(stu, sec, (GradeComponent) cbComp.getSelectedItem(),
                    ((Number) spScore.getValue()).doubleValue(),
                    ((Number) spWeight.getValue()).doubleValue());
            JOptionPane.showMessageDialog(this, "Đã ghi điểm.");
        });

        top.add(btn);
        add(top, BorderLayout.NORTH);

        // Nạp danh sách section demo (theo 2025A)
        for (var s : reg.listSectionsByTerm("2025A")) cbSection.addItem(s.getId());

        // Khi chọn lớp -> tải danh sách sinh viên của lớp đó
        cbSection.addActionListener(e -> reloadStudents());
        reloadStudents();
    }

    private void reloadStudents(){
        cbStudent.removeAllItems();
        String sec = (String) cbSection.getSelectedItem();
        if(sec == null) return;
        for(var e : reg.listEnrollmentsBySection(sec)){
            cbStudent.addItem(e.getStudentId());
        }
    }
}
