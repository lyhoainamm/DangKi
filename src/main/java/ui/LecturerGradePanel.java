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

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Nhập điểm"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4,4,4,4);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Lớp:"), gbc);
        gbc.gridx = 1;
        form.add(cbSection, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("SV:"), gbc);
        gbc.gridx = 1;
        form.add(cbStudent, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        form.add(new JLabel("Thành phần:"), gbc);
        gbc.gridx = 1;
        form.add(cbComp, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        form.add(new JLabel("Điểm:"), gbc);
        gbc.gridx = 1;
        form.add(spScore, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        form.add(new JLabel("Trọng số:"), gbc);
        gbc.gridx = 1;
        form.add(spWeight, gbc);

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

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        form.add(btn, gbc);

        add(form, BorderLayout.NORTH);

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
