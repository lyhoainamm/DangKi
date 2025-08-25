package repo;
import model.Student;

public interface StudentRepository {
    Student findById(String id);
    void save(Student s);
}
