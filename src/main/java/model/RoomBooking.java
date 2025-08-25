package model;
import java.time.LocalDate; import java.time.LocalTime;
public class RoomBooking {
  private final String roomId, sectionId; private final LocalDate date; private final LocalTime start, end;
  public RoomBooking(String roomId,String sectionId,LocalDate date,LocalTime start,LocalTime end){
    this.roomId=roomId; this.sectionId=sectionId; this.date=date; this.start=start; this.end=end;}
  public String getRoomId(){return roomId;} public LocalDate getDate(){return date;}
  public LocalTime getStart(){return start;} public LocalTime getEnd(){return end;}
}
