package repo;
import model.Course;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryCourseRepository implements CourseRepository {
    private static final InMemoryCourseRepository INSTANCE = new InMemoryCourseRepository();
    public static InMemoryCourseRepository getInstance(){ return INSTANCE; }
    private final Map<String, Course> byCode = new ConcurrentHashMap<>();
    @Override public Course findByCode(String code){ return byCode.get(code); }
    @Override public void save(Course c){ byCode.put(c.getCode(), c); }
    @Override public Collection<Course> findAll(){ return byCode.values(); }
    public void delete(String code){ byCode.remove(code); }
}
