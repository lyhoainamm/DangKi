package ui;

import model.Course;
import model.Section;
import model.User;
import service.RegistrationService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class RegisterPortalPanel extends JPanel {
    private static final Color CARD_BG = Color.WHITE;
    private static final Color ACCENT  = new Color(0xFF884D);

    private final User user;
    private final RegistrationService svc = new RegistrationService();

    // top stats
    private final JLabel lbSumRegistered = new JLabel("0");
    private final JLabel lbMaxCredits    = new JLabel("-");
    private final JLabel lbMinCredits    = new JLabel("-");

    // filters
    private final JComboBox<String> cbTerm = new JComboBox<>(new String[]{"2025A","2025B"});
    private final JTextField tfSearch = new JTextField(24);
    private final JPanel pnlWeekdays  = new JPanel(new GridLayout(2,4,6,4));

    // center
    private final DefaultListModel<Course> courseModel = new DefaultListModel<>();
    private final JList<Course> lstCourses = new JList<>(courseModel);

    // right
    private final JPanel pnlSectionCards = new JPanel();

    public RegisterPortalPanel(User user){
        this.user = user;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(8,8,8,8));

        add(buildTop(), BorderLayout.NORTH);
        add(buildMain(), BorderLayout.CENTER);

        loadCourses();
        if (!courseModel.isEmpty()) lstCourses.setSelectedIndex(0);
        refreshRightCards();
    }

    private JComponent buildTop(){
        JPanel top = new JPanel(new GridLayout(1,3,12,12));
        top.setBorder(new EmptyBorder(8,8,8,8));
        top.add(statCard("Tổng lớp đã đăng ký", lbSumRegistered));
        top.add(statCard("TC tối đa (theo HP)", lbMaxCredits));
        top.add(statCard("TC tối thiểu", lbMinCredits));
        return top;
    }
    private JComponent statCard(String title, JLabel value){
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(new LineBorder(new Color(220,225,235),1,true));
        JLabel t = new JLabel("  " + title);
        t.setFont(t.getFont().deriveFont(Font.BOLD, 13f));
        t.setForeground(Color.BLACK);
        value.setHorizontalAlignment(SwingConstants.CENTER);
        value.setFont(value.getFont().deriveFont(Font.BOLD, 22f));
        value.setForeground(Color.BLACK);
        card.add(t, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        return card;
    }

    private JComponent buildMain(){
    // ==== Khung 3 cột: Lọc (trái) — Học phần (giữa) — Card lớp (phải)
    JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    split.setResizeWeight(0.12);              // lọc ~22% chiều rộng
    split.setContinuousLayout(true);

    // ===== LEFT: Lọc gọn =====
    JPanel left = new JPanel();
    left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
    left.setBorder(new EmptyBorder(8, 8, 8, 8));
    left.setBackground(getBackground());
    left.setPreferredSize(new Dimension(160, 0));     // ▼ rộng ~260px

    // Hàng 1: Học kỳ
    JPanel row1 = new JPanel(new BorderLayout(6,0));
    row1.setOpaque(false);
    row1.add(new JLabel("Học kỳ:"), BorderLayout.WEST);
    row1.add(cbTerm, BorderLayout.CENTER);
    row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
    left.add(row1);
    left.add(Box.createVerticalStrut(8));

    // Hàng 2: Tìm kiếm + nút
    JPanel row2 = new JPanel(new BorderLayout(6,0));
    row2.setOpaque(false);
    tfSearch.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
    JButton btnFind = new JButton("Tìm");
    btnFind.addActionListener(e -> filterCourses());
    row2.add(new JLabel("Tìm kiếm:"), BorderLayout.WEST);
    row2.add(tfSearch, BorderLayout.CENTER);
    row2.add(btnFind, BorderLayout.EAST);
    row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
    left.add(row2);
    left.add(Box.createVerticalStrut(10));

    // Nhóm “Thứ học” 2x3 nhỏ gọn
    JPanel wd = new JPanel(new BorderLayout());
    wd.setBorder(new TitledBorder("Thứ học"));
    wd.setOpaque(false);
    pnlWeekdays.removeAll();
    pnlWeekdays.setOpaque(false);
    pnlWeekdays.setLayout(new GridLayout(2, 3, 8, 4));   // ▼ 2 hàng x 3 cột
    for (int i=2;i<=7;i++){
        JCheckBox cb = new JCheckBox("Thứ " + i);
        cb.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12)); // ▼ chữ nhỏ gọn
        pnlWeekdays.add(cb);
    }
    wd.add(pnlWeekdays, BorderLayout.CENTER);
    left.add(wd);

    JButton apply = new JButton("Áp dụng bộ lọc");
    apply.addActionListener(e -> refreshRightCards());
    apply.setAlignmentX(Component.LEFT_ALIGNMENT);
    left.add(Box.createVerticalStrut(6));
    left.add(apply);
    left.add(Box.createVerticalGlue()); // đẩy phần còn lại xuống

    // ===== CENTER: Danh sách học phần
    JPanel center = new JPanel(new BorderLayout());
    center.setBorder(new TitledBorder("Học phần"));
    lstCourses.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    lstCourses.addListSelectionListener(e -> { if(!e.getValueIsAdjusting()) refreshRightCards(); });
    lstCourses.setCellRenderer(new DefaultListCellRenderer(){
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus){
            super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
            if (value instanceof model.Course c) setText(c.getCode()+" - "+c.getName());
            setForeground(Color.BLACK);
            return this;
        }
    });
    center.add(new JScrollPane(lstCourses), BorderLayout.CENTER);

    // ===== RIGHT: Card lớp học phần
    JPanel right = new JPanel(new BorderLayout());
    right.setBorder(new TitledBorder("Lớp học phần"));
    pnlSectionCards.setLayout(new BoxLayout(pnlSectionCards, BoxLayout.Y_AXIS));
    right.add(new JScrollPane(pnlSectionCards){{
        setBorder(new LineBorder(new Color(230,230,230)));
    }}, BorderLayout.CENTER);

    // Ghép 3 cột
    split.setLeftComponent(new JScrollPane(left){{
        setBorder(new LineBorder(new Color(230,230,230)));
        setPreferredSize(new Dimension(260, 0)); // đảm bảo divider đúng vị trí
    }});
    JSplitPane rightSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, center, right);
    rightSplit.setResizeWeight(0.38);           // giữa ~38%, phải ~40%
    rightSplit.setContinuousLayout(true);
    split.setRightComponent(rightSplit);

    // Đặt vị trí thanh chia ngay từ đầu (sau khi add vào container)
    SwingUtilities.invokeLater(() -> {
        split.setDividerLocation(260);          // trái cố định ~260px
        rightSplit.setDividerLocation(0.40);    // tuỳ màn hình
    });

    return split;
}


    // data & actions
    private void loadCourses(){
        courseModel.clear();
        var list = new ArrayList<>(svc.listAllCourses());
        list.sort(Comparator.comparing(Course::getCode));
        list.forEach(courseModel::addElement);
    }
    private void filterCourses(){
        String q = tfSearch.getText().trim().toLowerCase();
        courseModel.clear();
        var list = new ArrayList<>(svc.listAllCourses());
        list.sort(Comparator.comparing(Course::getCode));
        for (Course c : list){
            String hay = (c.getCode()+" "+c.getName()).toLowerCase();
            if (q.isBlank() || hay.contains(q)) courseModel.addElement(c);
        }
        if (!courseModel.isEmpty()) lstCourses.setSelectedIndex(0);
    }
    private String term(){ return (String) cbTerm.getSelectedItem(); }

    private void refreshRightCards(){
        pnlSectionCards.removeAll();

        Course c = lstCourses.getSelectedValue();
        if (c == null){
            pnlSectionCards.add(new JLabel("  Chọn một học phần để xem lớp học phần."));
            pnlSectionCards.revalidate(); pnlSectionCards.repaint(); return;
        }

        List<Section> secs = new ArrayList<>(svc.listSectionsByCourseAndTerm(c.getCode(), term()));
        var allowed = selectedWeekdays();
        if (!allowed.isEmpty()){
            secs = secs.stream().filter(s -> allowed.contains(s.getDayOfWeek().getValue())).collect(Collectors.toList());
        }

        if (secs.isEmpty()){
            pnlSectionCards.add(new JLabel("  Không có lớp thỏa điều kiện."));
        } else {
            for (Section s : secs) pnlSectionCards.add(sectionCard(s));
        }
        pnlSectionCards.add(Box.createVerticalGlue());
        pnlSectionCards.revalidate(); pnlSectionCards.repaint();

        // update top
        lbSumRegistered.setText(String.valueOf(secs.stream().mapToInt(sc -> svc.countRegistered(sc.getId())).sum()));
        lbMaxCredits.setText(String.valueOf(c.getCredits()));
        lbMinCredits.setText("-");
    }
    private Set<Integer> selectedWeekdays(){
        Set<Integer> set = new HashSet<>();
        for (Component comp : pnlWeekdays.getComponents()){
            if (comp instanceof JCheckBox cb && cb.isSelected()){
                try { set.add(Integer.parseInt(cb.getText().replace("Thứ ","").trim())); } catch (Exception ignored){}
            }
        }
        return set;
    }

    private JComponent sectionCard(Section s){
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(new LineBorder(new Color(220,225,235),1,true));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JPanel head = new JPanel(new FlowLayout(FlowLayout.LEFT,8,6)); head.setOpaque(false);
        JLabel title = new JLabel(s.getCourseCode()+" - "+s.getId());
        title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));
        head.add(title);
        head.add(new JLabel("Thứ: " + s.getDayOfWeek().getValue()));
        card.add(head, BorderLayout.NORTH);

        JPanel body = new JPanel(new GridLayout(2,3,8,4)); body.setOpaque(false);
        body.add(info("Bắt đầu:", fmt.format(s.getStart())));
        body.add(info("Kết thúc:", fmt.format(s.getEnd())));
        body.add(info("Phòng:", s.getRoom()));
        body.add(info("GV:", s.getLecturer()));
        int reg = svc.countRegistered(s.getId());
        body.add(info("Tổng/ĐK:", s.getCapacity() + " / " + reg));
        body.add(new JLabel());
        card.add(body, BorderLayout.CENTER);

        JPanel foot = new JPanel(new FlowLayout(FlowLayout.RIGHT,8,6)); foot.setOpaque(false);
        JButton btDetail = new JButton("Xem chi tiết");
        JButton btAdd    = new JButton("Đăng kí ");
        btAdd.setBackground(ACCENT);
        btAdd.addActionListener(e -> doRegister(s));
        btDetail.addActionListener(e -> JOptionPane.showMessageDialog(this,
            "Mã lớp: "+s.getId()+"\nHọc kỳ: "+s.getTerm()+"\nThứ: "+s.getDayOfWeek()+
            "\nGiờ: "+fmt.format(s.getStart())+" - "+fmt.format(s.getEnd())+
            "\nPhòng: "+s.getRoom()+"\nGV: "+s.getLecturer()+"\nSức chứa: "+s.getCapacity()
        ));
        foot.add(btDetail); foot.add(btAdd);
        card.add(foot, BorderLayout.SOUTH);

        return card;
    }
    private JComponent info(String k, String v){
        JPanel p = new JPanel(new BorderLayout()); p.setOpaque(false);
        JLabel lk = new JLabel(k); lk.setForeground(Color.GRAY);
        JLabel lv = new JLabel(v); lv.setForeground(Color.BLACK);
        p.add(lk, BorderLayout.NORTH); p.add(lv, BorderLayout.CENTER);
        return p;
    }
    private void doRegister(Section s){
        String msg = svc.register(user.getId(), s.getId());
        if (msg == null){
            JOptionPane.showMessageDialog(this, "Đăng ký thành công lớp " + s.getId());
            refreshRightCards();
        } else {
            JOptionPane.showMessageDialog(this, msg, "Không thể đăng ký", JOptionPane.WARNING_MESSAGE);
        }
    }
}
