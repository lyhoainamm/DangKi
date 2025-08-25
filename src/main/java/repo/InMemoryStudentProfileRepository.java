package repo;
import model.StudentProfile; import java.util.concurrent.ConcurrentHashMap;
public class InMemoryStudentProfileRepository implements StudentProfileRepository{
  private static final InMemoryStudentProfileRepository I=new InMemoryStudentProfileRepository();
  public static InMemoryStudentProfileRepository getInstance(){return I;}
  private final ConcurrentHashMap<String, StudentProfile> map=new ConcurrentHashMap<>();
  public void save(StudentProfile p){map.put(p.getStudentId(), p);}
  public StudentProfile find(String id){return map.get(id);}
   public void delete(String studentId){ map.remove(studentId); } 
}

