package repo;
import model.Admission; import java.util.*; import java.util.concurrent.ConcurrentHashMap;
public class InMemoryAdmissionRepository implements AdmissionRepository{
  private static final InMemoryAdmissionRepository I=new InMemoryAdmissionRepository();
  public static InMemoryAdmissionRepository getInstance(){return I;}
  private final Map<String, Admission> byId=new ConcurrentHashMap<>();
  public void save(Admission a){byId.put(a.getId(), a);}
  public Admission findById(String id){return byId.get(id);}
  public Collection<Admission> findAll(){return new ArrayList<>(byId.values());}
}
