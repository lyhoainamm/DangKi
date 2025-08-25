package repo;
import model.Grade; import java.util.*; import java.util.concurrent.CopyOnWriteArrayList;
public class InMemoryGradeRepository {
  private static final InMemoryGradeRepository I=new InMemoryGradeRepository();
  public static InMemoryGradeRepository getInstance(){return I;}
  private final List<Grade> grades = new CopyOnWriteArrayList<>();
  public void upsert(Grade g){
    grades.removeIf(x->x.getStudentId().equals(g.getStudentId()) && x.getSectionId().equals(g.getSectionId()) && x.getComp()==g.getComp());
    grades.add(g);
  }
  public List<Grade> findByStudentAndTerm(String studentId, String term, repo.SectionRepository sectionRepo){
    return grades.stream().filter(g->term.equals(sectionRepo.findById(g.getSectionId()).getTerm())).filter(g->g.getStudentId().equals(studentId)).toList();
  }
  public List<Grade> findBySection(String sectionId){ return grades.stream().filter(g->g.getSectionId().equals(sectionId)).toList(); }
}
