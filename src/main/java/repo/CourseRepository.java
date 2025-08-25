package repo;
import model.Course;
import java.util.Collection;

public interface CourseRepository {
    Course findByCode(String code);
    void save(Course c);
    Collection<Course> findAll();
}
