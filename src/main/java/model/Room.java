package model;
public class Room { private final String id; private final int capacity; private final String type;
  public Room(String id,int capacity,String type){this.id=id; this.capacity=capacity; this.type=type;}
  public String getId(){return id;} public int getCapacity(){return capacity;} public String getType(){return type;}
}
