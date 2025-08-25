package ui;

import model.Role;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AdminPortalFrame extends JFrame {

    private static final Color NAVY    = new Color(0x0F2855);
    private static final Color PAGE_BG = new Color(0xF3F6FC);

    private final User admin;

    private final CardLayout cards = new CardLayout();
    private final JPanel content   = new JPanel(cards);

    private final AdminDashboardPanel dashboard;
    private final AdminManagePanel    managePanel;
    private final AdminAdmissionsPanel admissionsPanel;
    private final LecturerGradePanel  gradePanel;

    public AdminPortalFrame(User admin){
        super("Quản trị – " + admin.getName());
        this.admin = admin;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1280, 750);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        setContentPane(root);

        // Sidebar
        root.add(buildSidebar(), BorderLayout.WEST);

        // Trung tâm
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(PAGE_BG);
        center.add(buildTopbar(), BorderLayout.NORTH);

        // Khởi tạo panel con (an toàn)
        dashboard      = new AdminDashboardPanel();
        managePanel    = new AdminManagePanel(admin);
        admissionsPanel= new AdminAdmissionsPanel();
        gradePanel     = new LecturerGradePanel();

        // Đăng ký card
        content.add(dashboard,       "DASH");
        content.add(managePanel,     "MANAGE");
        content.add(admissionsPanel, "ADMISSION");
        content.add(gradePanel,      "GRADE");

        center.add(content, BorderLayout.CENTER);
        root.add(center, BorderLayout.CENTER);

        // Show mặc định
        showCard("DASH");
    }

    private JComponent buildSidebar(){
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setPreferredSize(new Dimension(230, 0));
        side.setBackground(Color.WHITE);

        JLabel brand = new JLabel("  ADMIN PORTAL");
        brand.setForeground(Color.BLACK);
        brand.setBorder(new EmptyBorder(16, 12, 12, 12));
        brand.setFont(brand.getFont().deriveFont(Font.BOLD, 18f));
        side.add(brand);

        side.add(menu("Tổng quan", UIManager.getIcon("FileView.computerIcon"),
                e -> showCard("DASH")));

        side.add(menu("Học phần", UIManager.getIcon("FileChooser.detailsViewIcon"),
                e -> { showCard("MANAGE"); managePanel.showCoursesTab(); }));

        side.add(menu("Lớp học phần", UIManager.getIcon("FileView.directoryIcon"),
                e -> { showCard("MANAGE"); managePanel.showSectionsTab(); }));

        side.add(menu("Sinh viên", UIManager.getIcon("FileView.fileIcon"),
                e -> { showCard("MANAGE"); managePanel.showStudentsTab(); }));

        side.add(menu("Tuyển sinh", UIManager.getIcon("FileChooser.newFolderIcon"),
                e -> showCard("ADMISSION")));

        side.add(menu("Nhập điểm", UIManager.getIcon("OptionPane.informationIcon"),
                e -> showCard("GRADE")));

        side.add(Box.createVerticalGlue());

        JButton logout = new JButton("Đăng xuất");
        logout.setAlignmentX(Component.LEFT_ALIGNMENT);
        logout.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });
        logout.setBorder(new EmptyBorder(10,12,12,12));
        side.add(logout);

        return side;
    }

    private JButton menu(String text, Icon icon, java.awt.event.ActionListener al){
        JButton b = new JButton(text, icon);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        b.setForeground(Color.BLACK);
        b.setBackground(Color.WHITE);
        b.setFocusPainted(false);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setBorder(new EmptyBorder(10,16,10,12));
        b.setFont(b.getFont().deriveFont(Font.PLAIN, 14f));
        b.addActionListener(al);

        b.addChangeListener(e -> b.setBackground(
                b.getModel().isRollover() ? new Color(0xE6F0FF) : Color.WHITE
        ));
        return b;
    }

    private JComponent buildTopbar(){
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(0x1B3C7A));
        top.setBorder(new EmptyBorder(10,18,10,18));

        JLabel hello = new JLabel("Xin chào, " + admin.getName() + " (" + admin.getRole() + ")");
        hello.setForeground(Color.WHITE);
        hello.setFont(hello.getFont().deriveFont(Font.BOLD, 20f));
        top.add(hello, BorderLayout.WEST);
        return top;
    }

    private void showCard(String key){
        cards.show(content, key);
        if ("DASH".equals(key)) {
            dashboard.refreshStats(); // luôn cập nhật số liệu thật khi quay lại
        }
    }

    // Test nhanh (nếu cần)
    public static void main(String[] args) {
        User ad = new User("admin","admin","123","Quản trị viên", Role.ADMIN);
        new AdminPortalFrame(ad).setVisible(true);
    }
}
