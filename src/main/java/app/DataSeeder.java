package app;

import repo.InMemoryUserRepository;
import repo.InMemoryStudentRepository;
import repo.InMemorySectionRepository;
import repo.InMemoryCourseRepository;
import model.User;
import model.Student;
import model.Section;
import model.Role;
import model.Course;
import model.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

public class DataSeeder {
    public static void seed() {
        var users = InMemoryUserRepository.getInstance();
        var students = InMemoryStudentRepository.getInstance();
        var courses = InMemoryCourseRepository.getInstance();
        var sections = InMemorySectionRepository.getInstance();

        // Users & Students
        var admin = new User("u0", "admin", "123", "Quản trị", Role.ADMIN);
        users.save(admin);

        var s1 = new Student("s1", "s1", "123", "Nguyễn Văn A", 20);
        s1.getCompletedCourses().add("CS101"); // đã đậu CS101
        students.save(s1);
        users.save(s1);

        var s2 = new Student("s2", "s2", "123", "Trần Thị B", 18);
        students.save(s2);
        users.save(s2);

        // Courses
        courses.save(new Course("CS101", "Nhập môn Lập trình", 3, Set.of()));
        courses.save(new Course("CS102", "Cấu trúc dữ liệu", 3, Set.of("CS101")));
        courses.save(new Course("MATH101", "Giải tích 1", 4, Set.of()));
        courses.save(new Course("PHYS101", "Vật lý đại cương", 3, Set.of()));

        // Sections (term 2025A)
        sections.save(new Section("SEC-001", "CS101", "2025A", DayOfWeek.MONDAY, LocalTime.of(8,0), LocalTime.of(10,0), "A101", 40, "ThS. Hùng"));
        sections.save(new Section("SEC-002", "CS101", "2025A", DayOfWeek.WEDNESDAY, LocalTime.of(13,0), LocalTime.of(15,0), "A102", 40, "ThS. Hùng"));

        sections.save(new Section("SEC-101", "CS102", "2025A", DayOfWeek.MONDAY, LocalTime.of(10,0), LocalTime.of(12,0), "B201", 35, "TS. Lan"));

        sections.save(new Section("SEC-301", "MATH101", "2025A", DayOfWeek.TUESDAY, LocalTime.of(8,0), LocalTime.of(10,0), "C301", 50, "PGS. Minh"));

        sections.save(new Section("SEC-401", "PHYS101", "2025A", DayOfWeek.MONDAY, LocalTime.of(9,0), LocalTime.of(11,0), "D401", 30, "TS. Phúc")); // cố tình chồng 9-11

        // term 2025B (để đổi kỳ)
        sections.save(new Section("SEC-901", "CS101", "2025B", DayOfWeek.THURSDAY, LocalTime.of(9,0), LocalTime.of(11,0), "A101", 40, "ThS. Hùng"));
    }
}
