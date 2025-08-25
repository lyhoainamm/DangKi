package model;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;

public class Section {
    private final String id;
    private final String courseCode;
    private final String term; // ví dụ 2025A
    private final DayOfWeek dayOfWeek;
    private final LocalTime start;
    private final LocalTime end;
    private final String room;
    private final int capacity;
    private final String lecturer;

    public Section(String id, String courseCode, String term, DayOfWeek dayOfWeek,
                   LocalTime start, LocalTime end, String room, int capacity, String lecturer) {
        this.id = id; this.courseCode = courseCode; this.term = term; this.dayOfWeek = dayOfWeek;
        this.start = start; this.end = end; this.room = room; this.capacity = capacity; this.lecturer = lecturer;
    }

    public String getId() { return id; }
    public String getCourseCode() { return courseCode; }
    public String getTerm() { return term; }
    public DayOfWeek getDayOfWeek() { return dayOfWeek; }
    public LocalTime getStart() { return start; }
    public LocalTime getEnd() { return end; }
    public String getRoom() { return room; }
    public int getCapacity() { return capacity; }
    public String getLecturer() { return lecturer; }

    @Override public boolean equals(Object o){ return (o instanceof Section s) && Objects.equals(id, s.id);}
    @Override public int hashCode(){ return Objects.hash(id);}
}
