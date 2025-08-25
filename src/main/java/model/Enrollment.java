package model;

import java.time.LocalDateTime;

public class Enrollment {
    private final String id;
    private final String studentId;
    private final String sectionId;
    private final String term;
    private final LocalDateTime createdAt;

    public Enrollment(String id, String studentId, String sectionId, String term) {
        this.id = id; this.studentId = studentId; this.sectionId = sectionId; this.term = term;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getStudentId() { return studentId; }
    public String getSectionId() { return sectionId; }
    public String getTerm() { return term; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
