package ui;

import model.Enrollment;
import model.Section;
import model.User;
import service.RegistrationService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Thời khóa biểu tuần (Mon → Sun), khung giờ 06:00–20:00.
 * Dữ liệu lấy từ RegistrationService theo user + term.
 */
public class TimetablePanel extends JPanel {

    // Màu sắc
    private static final Color BG_PAGE    = new Color(0xF3F6FC);
    private static final Color GRID_LINE  = new Color(0xE3E8F3);
    private static final Color SLOT_BLUE  = new Color(0x8BB1FF);
    private static final Color SLOT_ORANGE= new Color(0xFFB37A);
    private static final Color SLOT_GREEN = new Color(0x8FD19E);
    private static final Color TEXT_DARK  = new Color(0x19233C);
    private static final Font  FONT_SMALL = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    private static final Font  FONT_BOLD  = new Font(Font.SANS_SERIF, Font.BOLD, 12);

    // Kích thước lưới
    private static final int HEADER_H = 40;      // cao hàng tiêu đề ngày
    private static final int HOUR_W   = 70;      // rộng cột giờ
    private static final int HOUR_H   = 60;      // cao 1 giờ
    private static final int START_HOUR = 6;     // bắt đầu từ 06:00
    private static final int END_HOUR   = 20;    // kết thúc 20:00

    private final User user;
    private final RegistrationService svc = new RegistrationService();

    private final JComboBox<String> cbTerm = new JComboBox<>(new String[]{"2025A","2025B"});
    private final JPanel canvas = new JPanel(null); // absolute layout để đặt ô lớp

    public TimetablePanel(User user){
        this.user = user;
        setLayout(new BorderLayout());
        setBackground(BG_PAGE);
        setBorder(new EmptyBorder(12,12,12,12));

        add(buildTop(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);

        refresh();
    }

    private JComponent buildTop(){
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel title = new JLabel("Lịch cá nhân (tuần)", SwingConstants.LEFT);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        title.setForeground(TEXT_DARK);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0));
        right.setOpaque(false);
        right.add(new JLabel("Học kỳ:"));
        right.add(cbTerm);
        JButton btn = new JButton("Làm mới");
        btn.addActionListener(e -> refresh());
        right.add(btn);

        top.add(title, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);
        top.setBorder(new EmptyBorder(0,0,8,0));
        return top;
    }

    private JComponent buildBody(){
        // Lưới: vẽ bằng 2 lớp — layer grid (vẽ line) và layer canvas (đặt ô lớp)
        JPanel grid = new JPanel(){
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.WHITE);
                g2.fillRect(0,0,getWidth(),getHeight());

                int totalHours = END_HOUR - START_HOUR;
                int gridH = HEADER_H + totalHours * HOUR_H;
                int colW = (getWidth() - HOUR_W) / 7;

                // nền
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, getWidth(), gridH);

                // cột giờ (trái)
                g2.setColor(new Color(0xF7F9FD));
                g2.fillRect(0, 0, HOUR_W, gridH);

                // đường ngang theo giờ
                g2.setColor(GRID_LINE);
                for (int i=0;i<=totalHours;i++){
                    int y = HEADER_H + i*HOUR_H;
                    g2.drawLine(HOUR_W, y, getWidth(), y);
                }
                // đường dọc theo ngày
                for (int d=0; d<=7; d++){
                    int x = HOUR_W + d*colW;
                    g2.drawLine(x, HEADER_H, x, gridH);
                }

                // header ngày
                g2.setColor(TEXT_DARK);
                String[] days = {"Mon","Tue","Wed","Thu","Fri","Sat","Sun"};
                for (int d=0; d<7; d++){
                    int x = HOUR_W + d*colW;
                    g2.setFont(FONT_BOLD);
                    g2.drawString(days[d], x+8, HEADER_H-12);
                }

                // dải header nền nhẹ
                g2.setColor(new Color(0xEEF2FB));
                g2.fillRect(HOUR_W, 0, getWidth()-HOUR_W, HEADER_H);
                g2.setColor(GRID_LINE);
                g2.drawLine(0, HEADER_H, getWidth(), HEADER_H);

                // nhãn giờ
                g2.setFont(FONT_SMALL);
                g2.setColor(TEXT_DARK);
                for (int h=START_HOUR; h<=END_HOUR; h++){
                    int y = HEADER_H + (h-START_HOUR)*HOUR_H;
                    String label = String.format("%02d:00", h);
                    g2.drawString(label, 8, y+4);
                }
            }
        };
        grid.setLayout(new BorderLayout());

        // canvas đặt ô lớp (null layout)
        canvas.setOpaque(false);

        // đặt canvas lên grid (LayeredPane-like)
        JLayeredPane layered = new JLayeredPane();
        layered.setLayout(new OverlayLayout(layered));
        layered.add(canvas, Integer.valueOf(1));
        layered.add(grid,   Integer.valueOf(0));

        // cuộn theo chiều dọc
        JScrollPane sp = new JScrollPane(layered);
        sp.getVerticalScrollBar().setUnitIncrement(16);

        // kích thước ảo cho layered
        int totalHours = END_HOUR - START_HOUR;
        int prefH = HEADER_H + totalHours*HOUR_H;
        layered.setPreferredSize(new Dimension(1100, prefH));
        grid.setPreferredSize(new Dimension(1100, prefH));
        canvas.setPreferredSize(new Dimension(1100, prefH));

        return sp;
    }

    private String term(){ return (String) cbTerm.getSelectedItem(); }

    /** Vẽ lại toàn bộ slot */
    private void refresh(){
        canvas.removeAll();

        List<Enrollment> ens = (List<Enrollment>) svc.listEnrollments(user.getId(), term());
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");

        int totalHours = END_HOUR - START_HOUR;
        int gridH = HEADER_H + totalHours*HOUR_H;
        int width = Math.max(canvas.getParent().getWidth(), 1100);
        int colW = (width - HOUR_W) / 7;

        // Tạo ô lớp
        int idx = 0;
        for (Enrollment e : ens){
            Section s = svc.findSection(e.getSectionId());
            if (s == null) continue;

            int dayIdx = dayToCol(s.getDayOfWeek());
            if (dayIdx < 0) continue;

            int x = HOUR_W + dayIdx * colW + 8;
            int y = HEADER_H + minutesFromStart(s.getStart()) * HOUR_H / 60 + 2;
            int h = Math.max(28, (minutesBetween(s.getStart(), s.getEnd()) * HOUR_H / 60) - 4);
            int w = colW - 16;

            JPanel slot = new JPanel(new BorderLayout());
            slot.setBounds(x, y, w, h);
            slot.setBorder(new LineBorder(new Color(0x335), 1, true));
            slot.setBackground(pickColor(idx++));
            slot.setOpaque(true);

            String title = s.getCourseCode() + " - " + s.getId();
            String detail = fmt.format(s.getStart()) + " - " + fmt.format(s.getEnd())
                    + "  ·  " + s.getRoom() + "  ·  " + s.getLecturer();

            JLabel lbTitle = new JLabel("<html><b>" + title + "</b></html>");
            lbTitle.setFont(FONT_BOLD);
            lbTitle.setBorder(new EmptyBorder(4,6,2,6));
            JLabel lbDetail = new JLabel(detail);
            lbDetail.setFont(FONT_SMALL);
            lbDetail.setBorder(new EmptyBorder(0,6,4,6));

            slot.add(lbTitle, BorderLayout.NORTH);
            slot.add(lbDetail, BorderLayout.CENTER);

            // tooltip chi tiết
            slot.setToolTipText("<html>" + title + "<br>" + detail + "</html>");

            canvas.add(slot);
        }

        // đồng bộ kích thước sau khi biết bề rộng viewport
        SwingUtilities.invokeLater(() -> {
            int w = canvas.getParent().getWidth();
            int col = (w - HOUR_W) / 7;
            canvas.setPreferredSize(new Dimension(w, gridH));
            canvas.revalidate();
            canvas.repaint();
        });
    }

    private int dayToCol(DayOfWeek d){
        // Mon=0 ... Sun=6
        int v = d.getValue(); // Mon=1 ... Sun=7
        return v - 1;
    }
    private int minutesFromStart(LocalTime t){
        return (t.getHour() - START_HOUR) * 60 + t.getMinute();
    }
    private int minutesBetween(LocalTime a, LocalTime b){
        return (b.getHour()-a.getHour())*60 + (b.getMinute()-a.getMinute());
    }
    private Color pickColor(int i){
        return switch (i % 3){
            case 0 -> SLOT_BLUE;
            case 1 -> SLOT_ORANGE;
            default -> SLOT_GREEN;
        };
    }
}
