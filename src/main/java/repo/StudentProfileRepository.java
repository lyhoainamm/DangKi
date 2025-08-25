package repo;
import model.StudentProfile;
public interface StudentProfileRepository { void save(StudentProfile p); StudentProfile find(String studentId); }
