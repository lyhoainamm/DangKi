package repo;
import model.Admission;
 import java.util.Collection;
public interface AdmissionRepository {
  void save(Admission a); Admission findById(String id);
  Collection<Admission> findAll();
}
