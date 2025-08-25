package ui;

import model.Role;
import model.User;
import service.AuthService;

import javax.swing.*;
import java.awt.*;
import ui.AdminPortalFrame;

public class LoginFrame extends JFrame {
    private final JTextField tfUser = new JTextField(15);
    private final JPasswordField pfPass = new JPasswordField(15);
    private final AuthService auth = new AuthService();

    public LoginFrame(){
        super("Đăng nhập - Đăng ký học tín chỉ");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(360, 200);
        setLocationRelativeTo(null);

        var panel = new JPanel(new GridBagLayout());
        var g = new GridBagConstraints();
        g.insets = new Insets(6,6,6,6);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx=0; g.gridy=0; panel.add(new JLabel("Tài khoản:"), g);
        g.gridx=1; panel.add(tfUser, g);
        g.gridx=0; g.gridy=1; panel.add(new JLabel("Mật khẩu:"), g);
        g.gridx=1; panel.add(pfPass, g);

        var btnLogin = new JButton("Đăng nhập");
        btnLogin.addActionListener(e -> doLogin());
        g.gridx=1; g.gridy=2; panel.add(btnLogin, g);

        add(panel);
    }

    private void doLogin(){
        String u = tfUser.getText().trim();
        String p = new String(pfPass.getPassword());
        User user = auth.login(u, p);
        if (user == null){
            JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (user.getRole() == Role.STUDENT){
            // ✅ Sinh viên: vào Portal mới
            new PortalFrame(user).setVisible(true);
            dispose();
        } else {
            // ✅ Quản trị: vào AdminFrame như cũ
            new AdminPortalFrame(user).setVisible(true);
            dispose();
        }
    }
}
