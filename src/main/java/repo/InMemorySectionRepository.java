package repo;

import model.Section;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemorySectionRepository implements SectionRepository {
    private static final InMemorySectionRepository INSTANCE = new InMemorySectionRepository();
    public static InMemorySectionRepository getInstance(){ return INSTANCE; }

    // Lưu theo id lớp
    private final Map<String, Section> byId = new ConcurrentHashMap<>();

    @Override
    public Section findById(String id){ return byId.get(id); }

    @Override
    public void save(Section s){ byId.put(s.getId(), s); }

    // ✅ Thêm: xóa lớp theo id (phục vụ AdminSectionsPanel)
    public void delete(String id){ byId.remove(id); }

    // ✅ Thêm: trả về tất cả lớp (phục vụ Dashboard đếm tổng lớp)
    public Collection<Section> findAll(){ return byId.values(); }

    @Override
    public Collection<Section> findByTerm(String term){
        List<Section> list = new ArrayList<>();
        for (Section s: byId.values()) {
            if (term.equals(s.getTerm())) list.add(s);
        }
        list.sort(Comparator.comparing(Section::getCourseCode).thenComparing(Section::getId));
        return list;
    }

    @Override
    public Collection<Section> findByCourseAndTerm(String courseCode, String term){
        List<Section> list = new ArrayList<>();
        for (Section s: byId.values()) {
            if (term.equals(s.getTerm()) && courseCode.equals(s.getCourseCode())) list.add(s);
        }
        list.sort(Comparator.comparing(Section::getId));
        return list;
    }
}
