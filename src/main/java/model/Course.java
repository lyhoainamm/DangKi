package model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Course {
    private final String code;
    private String name;
    private int credits;
    private final Set<String> prerequisites = new HashSet<>();

    public Course(String code, String name, int credits, Set<String> prereq) {
        this.code = code; this.name = name; this.credits = credits;
        if (prereq != null) this.prerequisites.addAll(prereq);
    }
    public String getCode() { return code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }
    public Set<String> getPrerequisites() { return prerequisites; }

    @Override public boolean equals(Object o){ return (o instanceof Course c) && Objects.equals(code, c.code);}
    @Override public int hashCode(){ return Objects.hash(code);}
    @Override public String toString(){ return code + " - " + name + " ("+credits+" TC)";}
}
