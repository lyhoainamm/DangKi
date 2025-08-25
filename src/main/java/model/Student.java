package model;

import model.Role;
import java.util.HashSet;
import java.util.Set;
import model.User;

public class Student extends User {
    private int maxCredits;
    private final Set<String> completedCourses = new HashSet<>();

    public Student(String id, String username, String password, String name, int maxCredits) {
        super(id, username, password, name, Role.STUDENT);
        this.maxCredits = maxCredits;
    }

    public int getMaxCredits() { return maxCredits; }
    public void setMaxCredits(int maxCredits) { this.maxCredits = maxCredits; }
    public Set<String> getCompletedCourses() { return completedCourses; }
}
