package model;
import java.time.LocalDateTime;
public class ExamSchedule {
  private final String sectionId; private final LocalDateTime start; private final int durationMinutes;
  public ExamSchedule(String sectionId, LocalDateTime start, int durationMinutes){
    this.sectionId=sectionId; this.start=start; this.durationMinutes=durationMinutes; }
  public String getSectionId(){return sectionId;} public LocalDateTime getStart(){return start;}
  public int getDurationMinutes(){return durationMinutes;}
}
