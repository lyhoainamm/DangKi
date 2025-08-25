package ui;

import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class PortalFrame extends JFrame {

    private static final Color NAVY       = new Color(0x0F2855);
    private static final Color NAVY_DARK  = new Color(0x0B1E40);
    private static final Color ACCENT     = new Color(0xFF884D);
    private static final Color PAGE_BG    = new Color(0xF3F6FC);
    private static final Color CARD_BG    = Color.WHITE;

    private final User user;

    // CardLayout chuyển nội dung trong cùng cửa sổ
    private final CardLayout contentCards = new CardLayout();
    private final JPanel content = new JPanel(contentCards);

    public PortalFrame(User user) {
        super("Cổng thông tin sinh viên");
        this.user = user;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 750);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        setContentPane(root);

        // Sidebar trái
        root.add(buildSidebar(), BorderLayout.WEST);

        // Trung tâm: topbar + vùng content
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(PAGE_BG);
        center.add(buildTopbar(), BorderLayout.NORTH);

        // Các màn hình
        content.add(buildDashboard(), "HOME");
        content.add(new RegisterPortalPanel(user), "REGISTER");
        content.add(new TimetablePanel(user), "TIMETABLE");
        center.add(content, BorderLayout.CENTER);

        root.add(center, BorderLayout.CENTER);

        showHome();
    }

    // ---------------- Sidebar ----------------
    private JComponent buildSidebar() {
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setPreferredSize(new Dimension(220, 0));
        side.setBackground(NAVY);

        JLabel brand = new JLabel("  STUDENT PORTAL");
        brand.setForeground(Color.WHITE);
        brand.setBorder(new EmptyBorder(16, 12, 12, 12));
        brand.setFont(brand.getFont().deriveFont(Font.BOLD, 18f));
        side.add(brand);

        side.add(menu("Trang chủ", UIManager.getIcon("FileView.homeIcon"), e -> showHome()));
        side.add(menu("Đăng ký học", UIManager.getIcon("OptionPane.questionIcon"), e -> openRegister()));
        side.add(menu("Thời khóa biểu", UIManager.getIcon("OptionPane.warningIcon"), e -> openTimetable()));

        side.add(Box.createVerticalGlue());

        JButton logout = new JButton("Đăng xuất");
        logout.setAlignmentX(Component.LEFT_ALIGNMENT);
        logout.setForeground(NAVY);
        logout.setBackground(Color.WHITE);
        logout.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });
        logout.setBorder(new EmptyBorder(10,12,12,12));
        side.add(logout);

        return side;
    }

    private JButton menu(String text, Icon icon, java.awt.event.ActionListener al) {
        JButton b = new JButton(text, icon);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        b.setForeground(Color.BLACK);
        b.setBackground(Color.WHITE);
        b.setFocusPainted(false);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setBorder(new EmptyBorder(10, 16, 10, 12));
        b.setFont(b.getFont().deriveFont(Font.PLAIN, 14f));
        b.addActionListener(al);
        b.addChangeListener(e -> b.setBackground(b.getModel().isRollover() ? new Color(0xE6F0FF) : Color.WHITE));
        return b;
    }

    // ---------------- Topbar ----------------
    private JComponent buildTopbar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(0x1B3C7A));
        top.setBorder(new EmptyBorder(10, 18, 10, 18));

        JLabel hello = new JLabel("Xin chào, " + user.getName() + "!");
        hello.setForeground(Color.WHITE);
        hello.setFont(hello.getFont().deriveFont(Font.BOLD, 20f));
        top.add(hello, BorderLayout.WEST);

        return top;
    }

    // ---------------- Dashboard (HOME) ----------------
    private JComponent buildDashboard() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(PAGE_BG);
        wrap.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel cards = new JPanel(new GridLayout(1, 3, 16, 16));
        cards.setOpaque(false);
        cards.add(card("Đăng ký học", UIManager.getIcon("OptionPane.questionIcon"), this::openRegister));
        cards.add(card("Thời khóa biểu", UIManager.getIcon("OptionPane.warningIcon"), this::openTimetable));
        cards.add(card("Tin tức", UIManager.getIcon("OptionPane.informationIcon"), this::showHome));
        wrap.add(cards, BorderLayout.NORTH);

        JPanel news = new JPanel(new BorderLayout());
        news.setBackground(CARD_BG);
        news.setBorder(new TitledBorder(new LineBorder(new Color(0xDDE3F0),1,true), "Tin tức"));
        JTextArea ta = new JTextArea("""
                • Thông báo: Đăng ký học kỳ 2025A bắt đầu từ 01/09.
                • Lịch thi dự kiến sẽ công bố vào tuần tới.
                """);
        ta.setEditable(false);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        ta.setForeground(Color.BLACK);
        news.add(new JScrollPane(ta), BorderLayout.CENTER);

        wrap.add(news, BorderLayout.CENTER);
        return wrap;
    }

    private JComponent card(String title, Icon icon, Runnable onClick){
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(new LineBorder(new Color(0xDDE3F0), 1, true));
        card.setPreferredSize(new Dimension(180, 120));

        JLabel iconLb = new JLabel(icon, SwingConstants.CENTER);
        iconLb.setBorder(new EmptyBorder(16, 12, 8, 12));

        JLabel titleLb = new JLabel(title, SwingConstants.CENTER);
        titleLb.setBorder(new EmptyBorder(0, 12, 12, 12));
        titleLb.setForeground(Color.BLACK);
        titleLb.setFont(titleLb.getFont().deriveFont(Font.BOLD, 14f));

        card.add(iconLb, BorderLayout.CENTER);
        card.add(titleLb, BorderLayout.SOUTH);

        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { onClick.run(); }
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { card.setBorder(new LineBorder(ACCENT, 2, true)); }
            @Override public void mouseExited (java.awt.event.MouseEvent e) { card.setBorder(new LineBorder(new Color(0xDDE3F0),1,true)); }
        });

        return card;
    }

    // ---------------- Chuyển màn ----------------
    private void openRegister(){
        contentCards.show(content, "REGISTER");
    }
    private void openTimetable(){
        contentCards.show(content, "TIMETABLE");
    }
    private void showHome(){
        contentCards.show(content, "HOME");
    }

    // Tuỳ chọn
    private void openFinance(){
        JOptionPane.showMessageDialog(this, "Chức năng tài chính (đang phát triển).");
    }
    private void showProfile(){
        JOptionPane.showMessageDialog(this, "Hồ sơ sinh viên: " + user.getName());
    }
}
