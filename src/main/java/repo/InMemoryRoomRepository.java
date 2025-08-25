package repo;
import model.*; import java.util.*; import java.util.concurrent.ConcurrentHashMap;

public class InMemoryRoomRepository {
  private static final InMemoryRoomRepository I=new InMemoryRoomRepository();
  public static InMemoryRoomRepository getInstance(){return I;}
  private final Map<String, Room> rooms=new ConcurrentHashMap<>();
  private final List<RoomBooking> bookings=Collections.synchronizedList(new ArrayList<>());
  public void saveRoom(Room r){ rooms.put(r.getId(), r); }
  public Collection<Room> findAll(){ return rooms.values(); }
  public void addBooking(RoomBooking b){ bookings.add(b); }
  public List<RoomBooking> findBookingsByRoom(String roomId){ return bookings.stream().filter(x->x.getRoomId().equals(roomId)).toList(); }
}
