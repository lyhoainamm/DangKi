package service;
import model.*; import repo.*; import java.time.*; import java.util.*;

public class SchedulingService {
  private final InMemoryRoomRepository rooms = InMemoryRoomRepository.getInstance();

  public boolean isRoomFree(String roomId, LocalDate date, LocalTime start, LocalTime end){
    for(var b: rooms.findBookingsByRoom(roomId)){
      if(!b.getDate().equals(date)) continue;
      boolean overlap = start.isBefore(b.getEnd()) && end.isAfter(b.getStart());
      if(overlap) return false;
    }
    return true;
  }
}
