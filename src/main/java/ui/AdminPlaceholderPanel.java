package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/** Panel trống có tiêu đề – thay dần bằng panel thật của bạn. */
public class AdminPlaceholderPanel extends JPanel {

    private static final Color PAGE_BG = new Color(0xF3F6FC);
    private static final Color CARD_BG = Color.WHITE;

    public AdminPlaceholderPanel(String title){
        setLayout(new BorderLayout());
        setBackground(PAGE_BG);
        setBorder(new EmptyBorder(16,16,16,16));

        var box = new JPanel(new BorderLayout());
        box.setBackground(CARD_BG);
        box.setBorder(new LineBorder(new Color(0xDDE3F0),1,true));
        var lb = new JLabel("👉 " + title, SwingConstants.LEFT);
        lb.setBorder(new EmptyBorder(12,12,12,12));
        lb.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        box.add(lb, BorderLayout.NORTH);

        var note = new JTextArea("""
                Đây là khung layout cho: %s.

                • Bạn có thể thay panel này bằng panel thật (VD: bảng học phần, form thêm/sửa, v.v.).
                • Nếu đã có AdminFrame quản lý học phần/lớp, hãy tách phần JPanel bên trong và nhét vào đây.
                """.formatted(title));
        note.setEditable(false);
        note.setLineWrap(true);
        note.setWrapStyleWord(true);
        note.setForeground(Color.BLACK);
        note.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        box.add(new JScrollPane(note), BorderLayout.CENTER);

        add(box, BorderLayout.CENTER);
    }
}
