package ui;

import model.User;

import javax.swing.*;
import java.awt.*;

public class AdminManagePanel extends JPanel {
    private final JTabbedPane tabs = new JTabbedPane();

    public AdminManagePanel(User admin){
        setLayout(new BorderLayout());

        tabs.addTab("Học phần",     new AdminCoursesPanel());
        tabs.addTab("Lớp học phần", new AdminSectionsPanel());
        tabs.addTab("Sinh viên",     new AdminStudentsPanel());

        add(tabs, BorderLayout.CENTER);
    }

    public void showCoursesTab(){  tabs.setSelectedIndex(0); }
    public void showSectionsTab(){ tabs.setSelectedIndex(1); }
    public void showStudentsTab(){ tabs.setSelectedIndex(2); }
}
