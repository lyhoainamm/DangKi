package repo;
import model.Enrollment;
import java.util.Collection;

public interface EnrollmentRepository {
    void save(Enrollment e);
    void delete(String enrollmentId);
    Enrollment findByStudentAndSection(String studentId, String sectionId);
    Collection<Enrollment> findByStudentAndTerm(String studentId, String term);
    int countBySection(String sectionId);
}
