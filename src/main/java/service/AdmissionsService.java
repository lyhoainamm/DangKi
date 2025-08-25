package service;

import model.*;
import repo.*;

import java.util.concurrent.atomic.AtomicInteger;

public class AdmissionsService {
    private final AdmissionRepository repo = InMemoryAdmissionRepository.getInstance();
    private final StudentRepository students = InMemoryStudentRepository.getInstance();
    private final StudentProfileRepository profiles = InMemoryStudentProfileRepository.getInstance();
    // ✅ kho user dùng cho đăng nhập
    private final InMemoryUserRepository users = InMemoryUserRepository.getInstance();

    // ✅ bộ đếm tự động, bắt đầu từ 1
    private static final AtomicInteger COUNTER = new AtomicInteger(1);

    public Admission createApplication(String fullName, String citizenId, String major, String cohort){
        // ID tự động: 01, 02, 03,...
        String id = String.format("%02d", COUNTER.getAndIncrement());
        var a = new Admission(id, fullName, citizenId, major, cohort);
        repo.save(a);
        return a;
    }

    public void accept(String admissionId){
        var a = repo.findById(admissionId);
        if (a != null) a.setStatus(AdmissionStatus.ACCEPTED);
    }

    public Student enroll(String admissionId){
        var a = repo.findById(admissionId);
        if (a == null) return null;
        a.setStatus(AdmissionStatus.ENROLLED);

        // Sinh mã SV & tài khoản đăng nhập
        String sid = "S" + a.getId();                  // ví dụ: S01
        String defaultPass = "123";

        // 1) Lưu đối tượng Student (nghiệp vụ)
        var s = new Student(sid, sid, defaultPass, a.getFullName(), 20);
        students.save(s);

        // 2) Lưu User để đăng nhập (AuthService sẽ đọc từ đây)
        // Chú ý: dùng đúng constructor User(...) của dự án bạn
        // Nếu User(String username, String password, Role role, String name):
        users.save(new User(sid, sid, defaultPass, a.getFullName(), Role.STUDENT));
        // Nếu constructor của bạn khác thứ tự, hãy điền lại cho khớp.

        // 3) Lưu hồ sơ mở rộng
        var p = new StudentProfile(sid);
        p.setMajor(a.getMajor());
        p.setCohort(a.getCohort());
        profiles.save(p);

        return s;
    }

    public void assignClass(String studentId, String classCode){
        var p = profiles.find(studentId);
        if (p != null) p.setClassCode(classCode);
    }
}
