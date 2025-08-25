package ui;

import model.Section;
import model.Enrollment;
import repo.InMemoryCourseRepository;
import repo.InMemorySectionRepository;
import repo.InMemoryStudentRepository;
import service.RegistrationService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public class AdminSectionsPanel extends JPanel {
    private final JComboBox<String> cbTerm = new JComboBox<>(new String[]{"2025A","2025B"});
    private final JTextField tfSearch = new JTextField(14);

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Mã lớp","Mã HP","Kỳ","Thứ","BĐ","KT","Phòng","SC","ĐK/SC","GV"}, 0) {
        @Override public boolean isCellEditable(int r, int c){ return false; }
    };
    private final JTable table = new JTable(model);

    private final InMemorySectionRepository sectionRepo = InMemorySectionRepository.getInstance();
    private final RegistrationService svc = new RegistrationService();

    public AdminSectionsPanel(){
        setLayout(new BorderLayout(8,8));

        var top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        var btnReload = new JButton("Làm mới");
        var btnAdd    = new JButton("Thêm lớp");
        var btnDel    = new JButton("Xóa lớp");

        btnReload.addActionListener(e -> load());
        btnAdd.addActionListener(e -> addSection());
        btnDel.addActionListener(e -> deleteSelected());
        cbTerm.addActionListener(e -> load());
        tfSearch.addActionListener(e -> load());

        top.add(new JLabel("Học kỳ:"));  top.add(cbTerm);
        top.add(new JLabel("Tìm (Mã lớp/Mã HP):")); top.add(tfSearch);
        top.add(btnReload); top.add(btnAdd); top.add(btnDel);
        add(top, BorderLayout.NORTH);

        table.setRowHeight(22);
        table.getTableHeader().setReorderingAllowed(false);
        add(new JScrollPane(table), BorderLayout.CENTER);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount()==2 && table.getSelectedRow()>=0) {
                    showStudentsOfSelected();
                }
            }
        });

        load();
    }

    private void load(){
        model.setRowCount(0);
        String term = (String) cbTerm.getSelectedItem();
        String q = tfSearch.getText().trim().toUpperCase();

        for (Section s : sectionRepo.findByTerm(term)){
            if (!q.isEmpty()) {
                if (!(s.getId().toUpperCase().contains(q) || s.getCourseCode().toUpperCase().contains(q)))
                    continue;
            }
            int reg = 0;
            try { reg = svc.listEnrollmentsBySection(s.getId()).size(); } catch (Throwable ignored) {}
            model.addRow(new Object[]{
                    s.getId(), s.getCourseCode(), s.getTerm(),
                    s.getDayOfWeek(), s.getStart(), s.getEnd(), s.getRoom(),
                    s.getCapacity(), reg + "/" + s.getCapacity(), s.getLecturer()
            });
        }
    }

    private void addSection(){
        JTextField tfId    = new JTextField("SEC-"+ UUID.randomUUID().toString().substring(0,4).toUpperCase());
        JTextField tfCourse= new JTextField();
        JComboBox<String> cbT  = new JComboBox<>(new String[]{"2025A","2025B"});
        JComboBox<DayOfWeek> cbD = new JComboBox<>(DayOfWeek.values());
        JTextField tfStart = new JTextField("08:00");
        JTextField tfEnd   = new JTextField("10:00");
        JTextField tfRoom  = new JTextField("A101");
        JSpinner spCap     = new JSpinner(new SpinnerNumberModel(40,1,500,1));
        JTextField tfLect  = new JTextField("ThS. A");

        JPanel p=new JPanel(new GridLayout(0,1,6,6));
        p.add(new JLabel("Mã lớp:"));     p.add(tfId);
        p.add(new JLabel("Mã học phần:"));p.add(tfCourse);
        p.add(new JLabel("Kỳ:"));         p.add(cbT);
        p.add(new JLabel("Thứ:"));        p.add(cbD);
        p.add(new JLabel("Bắt đầu:"));    p.add(tfStart);
        p.add(new JLabel("Kết thúc:"));   p.add(tfEnd);
        p.add(new JLabel("Phòng:"));      p.add(tfRoom);
        p.add(new JLabel("Sức chứa:"));   p.add(spCap);
        p.add(new JLabel("Giảng viên:")); p.add(tfLect);

        if (JOptionPane.showConfirmDialog(this, p, "Thêm lớp học phần",
                JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION) {
            try {
                var id     = tfId.getText().trim();
                var course = tfCourse.getText().trim().toUpperCase();
                if (InMemoryCourseRepository.getInstance().findByCode(course)==null){
                    JOptionPane.showMessageDialog(this,"Mã học phần không tồn tại"); return;
                }
                var term   = (String) cbT.getSelectedItem();
                var dow    = (DayOfWeek) cbD.getSelectedItem();
                var start  = LocalTime.parse(tfStart.getText().trim());
                var end    = LocalTime.parse(tfEnd.getText().trim());
                var room   = tfRoom.getText().trim();
                var cap    = (Integer) spCap.getValue();
                var lect   = tfLect.getText().trim();

                if (id.isEmpty() || course.isEmpty() || room.isEmpty() || lect.isEmpty()){
                    JOptionPane.showMessageDialog(this,"Vui lòng nhập đủ thông tin"); return;
                }
                if (sectionRepo.findById(id)!=null){
                    JOptionPane.showMessageDialog(this,"Mã lớp đã tồn tại"); return;
                }
                sectionRepo.save(new Section(id, course, term, dow, start, end, room, cap, lect));
                cbTerm.setSelectedItem(term);
                load();
            } catch (Exception ex){
                JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ: "+ex.getMessage());
            }
        }
    }

    private void deleteSelected(){
        int row = table.getSelectedRow();
        if (row < 0){ JOptionPane.showMessageDialog(this,"Chọn một lớp để xóa"); return; }
        String secId = String.valueOf(model.getValueAt(row,0));

        List<Enrollment> ens = svc.listEnrollmentsBySection(secId);
        if (!ens.isEmpty()){
            JOptionPane.showMessageDialog(this,
                    "Lớp đã có sinh viên đăng ký ("+ens.size()+"). Không thể xóa.");
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this,
                "Xóa lớp "+secId+"?", "Xác nhận", JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION){
            try {
                InMemorySectionRepository.getInstance().delete(secId);
                load();
            } catch (Throwable t){
                JOptionPane.showMessageDialog(this, "Repository chưa hỗ trợ delete(id).");
            }
        }
    }

    private void showStudentsOfSelected(){
        int row = table.getSelectedRow();
        if (row < 0) return;
        String secId = String.valueOf(model.getValueAt(row,0));

        List<Enrollment> ens = svc.listEnrollmentsBySection(secId);
        if (ens.isEmpty()){
            JOptionPane.showMessageDialog(this,"Chưa có sinh viên đăng ký lớp "+secId);
            return;
        }
        var stuRepo = InMemoryStudentRepository.getInstance();
        StringBuilder sb = new StringBuilder("Sinh viên lớp ").append(secId).append(":\n");
        for (var e : ens){
            var st = stuRepo.findById(e.getStudentId());
            sb.append("- ").append(e.getStudentId())
              .append(" : ").append(st!=null? st.getName(): "(?)").append('\n');
        }
        JOptionPane.showMessageDialog(this, sb.toString());
    }
}
