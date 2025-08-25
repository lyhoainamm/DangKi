package service;

import model.*;
import repo.*;
import util.ScheduleUtil;
import java.util.*;
import java.util.stream.Collectors;
import repo.InMemoryEnrollmentRepository;

public class RegistrationService {
    private final CourseRepository courses = InMemoryCourseRepository.getInstance();
    private final SectionRepository sections = InMemorySectionRepository.getInstance();
    private final InMemoryEnrollmentRepository enrollments = InMemoryEnrollmentRepository.getInstance();
    private final StudentRepository students = InMemoryStudentRepository.getInstance();

    public Collection<Course> listAllCourses(){ return courses.findAll().stream()
            .sorted(Comparator.comparing(Course::getCode)).collect(Collectors.toList()); }

    public Collection<Section> listSectionsByCourseAndTerm(String courseCode, String term){
        return sections.findByCourseAndTerm(courseCode, term);
    }

    public Collection<Section> listSectionsByTerm(String term){ return sections.findByTerm(term); }

    public Collection<Enrollment> listEnrollments(String studentId, String term){
        return enrollments.findByStudentAndTerm(studentId, term);
    }

    public Course findCourse(String code){ return courses.findByCode(code); }
    public Section findSection(String id){ return sections.findById(id); }
    public Student findStudent(String id){ return students.findById(id); }

    public String register(String studentId, String sectionId){
        var student = students.findById(studentId);
        var section = sections.findById(sectionId);
        if (student == null || section == null) return "Không tìm thấy sinh viên hoặc lớp học phần";

        var term = section.getTerm();

        if (enrollments.findByStudentAndSection(studentId, sectionId) != null)
            return "Bạn đã đăng ký lớp này rồi";

        int current = enrollments.countBySection(sectionId);
        if (current >= section.getCapacity()) return "Lớp đã đầy";

        int currentCredits = listEnrollments(studentId, term).stream()
                .map(e -> courses.findByCode(sections.findById(e.getSectionId()).getCourseCode()).getCredits())
                .reduce(0, Integer::sum);
        var course = courses.findByCode(section.getCourseCode());
        if (currentCredits + course.getCredits() > student.getMaxCredits())
            return "Vượt quá giới hạn tín chỉ cho phép";

        if (!student.getCompletedCourses().containsAll(course.getPrerequisites())) {
            return "Chưa đủ học phần tiên quyết: " + course.getPrerequisites();
        }

        for (var e : listEnrollments(studentId, term)){
            var other = sections.findById(e.getSectionId());
            if (ScheduleUtil.isOverlap(section, other))
                return "Trùng lịch với lớp " + other.getId() + " (" + other.getCourseCode() + ")";
        }

        var enrollmentId = UUID.randomUUID().toString();
        enrollments.save(new Enrollment(enrollmentId, studentId, sectionId, term));
        return null; // null = success
    }

    public String drop(String studentId, String sectionId){
    var e = enrollments.findByStudentAndSection(studentId, sectionId);
    if (e == null) return "Bạn chưa đăng ký lớp này";
    // Xóa bằng khóa kép (studentId, sectionId)
    enrollments.delete(studentId, sectionId);
    return null;
}

    // ======= BỔ SUNG CHO TÍNH NĂNG ĐẾM & XEM DANH SÁCH =======
    public int countRegistered(String sectionId){
    return InMemoryEnrollmentRepository.getInstance().countBySection(sectionId);
    }

    public java.util.List<model.Enrollment> listEnrollmentsBySection(String sectionId){
    return InMemoryEnrollmentRepository.getInstance().findBySection(sectionId);
    }

}
