package repo;
import model.Section;
import java.util.Collection;

public interface SectionRepository {
    Section findById(String id);
    void save(Section s);
    Collection<Section> findByTerm(String term);
    Collection<Section> findByCourseAndTerm(String courseCode, String term);
}
