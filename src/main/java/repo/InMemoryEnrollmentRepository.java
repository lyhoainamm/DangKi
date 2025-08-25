package repo;

import model.Enrollment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InMemoryEnrollmentRepository {
    private static final InMemoryEnrollmentRepository I = new InMemoryEnrollmentRepository();
    public static InMemoryEnrollmentRepository getInstance(){ return I; }

    private final List<Enrollment> list = new CopyOnWriteArrayList<>();

    public void save(Enrollment e){
        // tránh trùng
        for (var ex : list){
            if (ex.getStudentId().equals(e.getStudentId()) && ex.getSectionId().equals(e.getSectionId())) return;
        }
        list.add(e);
    }

    public void delete(String studentId, String sectionId){
        list.removeIf(x -> x.getStudentId().equals(studentId) && x.getSectionId().equals(sectionId));
    }

    /** 
     * Tìm các đăng ký của SV theo học kỳ (cần SectionRepository để tra term của section).
     */
    public List<Enrollment> findByStudentAndTerm(String studentId, String term, SectionRepository sections){
        List<Enrollment> rs = new ArrayList<>();
        for (var e : list){
            if (!e.getStudentId().equals(studentId)) continue;
            var sec = sections.findById(e.getSectionId());
            if (sec != null && term.equals(sec.getTerm())) rs.add(e);
        }
        return rs;
    }

    // ✅ OVERLOAD cho code cũ đang gọi 2 tham số (khỏi sửa RegistrationService)
    public List<Enrollment> findByStudentAndTerm(String studentId, String term){
        return findByStudentAndTerm(studentId, term, InMemorySectionRepository.getInstance());
    }

    // ✅ Dùng để kiểm tra đã đăng ký 1 section cụ thể chưa
    public Enrollment findByStudentAndSection(String studentId, String sectionId){
        for (var e : list){
            if (e.getStudentId().equals(studentId) && e.getSectionId().equals(sectionId)) return e;
        }
        return null;
    }

    // ✅ Đếm & liệt kê theo lớp (đã dùng cho cột ĐK/SC và nút “Xem SV của lớp”)
    public int countBySection(String sectionId){
        int c = 0;
        for (var e : list) if (e.getSectionId().equals(sectionId)) c++;
        return c;
    }

    public List<Enrollment> findBySection(String sectionId){
        List<Enrollment> rs = new ArrayList<>();
        for (var e : list) if (e.getSectionId().equals(sectionId)) rs.add(e);
        return rs;
    }
}
