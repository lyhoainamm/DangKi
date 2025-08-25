package ui;

import service.AdmissionsService;
import model.Admission;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AdminAdmissionsPanel extends JPanel {
    private final AdmissionsService svc = new AdmissionsService();
    private final JTable table = new JTable(new Model());

    public AdminAdmissionsPanel() {
        setLayout(new BorderLayout(6, 6));
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnNew = new JButton("Tạo hồ sơ");
        JButton btnAccept = new JButton("Chấp nhận");
        JButton btnEnroll = new JButton("Nhập học");

        btnNew.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Họ tên:");
            if (name == null || name.isBlank()) return;
            svc.createApplication(name, "CID" + (int)(Math.random()*100000), "CNTT", "K2025");
            ((Model) table.getModel()).reload();
        });
        btnAccept.addActionListener(e -> {
            Admission a = ((Model) table.getModel()).selected(table);
            if (a != null) {
                svc.accept(a.getId());
                ((Model) table.getModel()).reload();
            }
        });
        btnEnroll.addActionListener(e -> {
            Admission a = ((Model) table.getModel()).selected(table);
            if (a != null) {
                svc.enroll(a.getId());
                ((Model) table.getModel()).reload();
                JOptionPane.showMessageDialog(this, "Đã tạo tài khoản SV & hồ sơ cơ bản (mật khẩu mặc định 123).");
            }
        });

        bar.add(btnNew); bar.add(btnAccept); bar.add(btnEnroll);
        add(bar, BorderLayout.NORTH);

        ((Model) table.getModel()).reload();
    }

    static class Model extends AbstractTableModel {
        private final String[] cols = {"ID", "Họ tên", "Ngành", "Khóa", "Trạng thái"};
        private List<Admission> data = new ArrayList<>();

        void reload() {
            data = new ArrayList<>(repo.InMemoryAdmissionRepository.getInstance().findAll());
            fireTableDataChanged();
        }

        Admission selected(JTable t) {
            int r = t.getSelectedRow();
            return r < 0 ? null : data.get(r);
        }

        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int c) { return cols[c]; }

        @Override public Object getValueAt(int r, int c) {
            var a = data.get(r);
            return switch (c) {
                case 0 -> a.getId();
                case 1 -> a.getFullName();
                case 2 -> a.getMajor();
                case 3 -> a.getCohort();
                case 4 -> a.getStatus();
                default -> "";
            };
        }
    }
}
