package util;
import model.Section;

public class ScheduleUtil {
    public static boolean isOverlap(Section a, Section b){
        if (!a.getDayOfWeek().equals(b.getDayOfWeek())) return false;
        // chồng lấn khi start < other.end && end > other.start
        return a.getStart().isBefore(b.getEnd()) && a.getEnd().isAfter(b.getStart());
    }
}
