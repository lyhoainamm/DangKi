package repo;

import model.Student;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStudentRepository implements StudentRepository {
    private static final InMemoryStudentRepository I = new InMemoryStudentRepository();
    public static InMemoryStudentRepository getInstance(){ return I; }

    // Lưu SV theo id
    private final ConcurrentHashMap<String, Student> byId = new ConcurrentHashMap<>();

    @Override
    public void save(Student s){
        byId.put(s.getId(), s);
    }

    @Override
    public Student findById(String id){
        return byId.get(id);
    }

    // ✅ bổ sung cho AdminStudentsPanel / các chỗ liệt kê
    public Collection<Student> findAll(){
        return byId.values();
    }

    // (tuỳ chọn) tiện ích khác nếu cần
    public boolean exists(String id){ return byId.containsKey(id); }
    public void delete(String id){ byId.remove(id); }
}

