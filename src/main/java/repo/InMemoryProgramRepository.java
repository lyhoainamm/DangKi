package repo;
import model.*; import java.util.*; import java.util.concurrent.ConcurrentHashMap;

public class InMemoryProgramRepository {
  private static final InMemoryProgramRepository I=new InMemoryProgramRepository();
  public static InMemoryProgramRepository getInstance(){return I;}
  private final Map<String, List<ProgramRequirement>> reqByProgram=new ConcurrentHashMap<>();
  private final List<CourseCoreq> coreqs = Collections.synchronizedList(new ArrayList<>());
  public void addRequirement(ProgramRequirement r){ reqByProgram.computeIfAbsent(r.getProgramCode(), k->new ArrayList<>()).add(r); }
  public List<ProgramRequirement> requirementsOf(String program){ return reqByProgram.getOrDefault(program, List.of()); }
  public void addCoreq(CourseCoreq c){ coreqs.add(c); }
  public List<CourseCoreq> coreqsOf(String course){ return coreqs.stream().filter(x->x.getCourseCode().equals(course)).toList(); }
}
