package service;
import model.*; import repo.*; 
import java.util.*;
import repo.InMemoryEnrollmentRepository;
import repo.InMemorySectionRepository;

public class GradeService {
  private final InMemoryGradeRepository repo = InMemoryGradeRepository.getInstance();
  private final SectionRepository sections = InMemorySectionRepository.getInstance();
  private final CourseRepository courses = InMemoryCourseRepository.getInstance();

  public void enterGrade(String studentId, String sectionId, GradeComponent comp, double score, double weight){
    repo.upsert(new Grade(studentId, sectionId, comp, score, weight));
  }

  public double courseTotal(String studentId, String sectionId){
    double sum=0, w=0;
    for(var g: repo.findBySection(sectionId)) if(g.getStudentId().equals(studentId)){ sum+=g.getScore()*g.getWeight(); w+=g.getWeight(); }
    return w>0? sum/w : 0.0;
  }

  public double toGPA4(double score100){
    if(score100>=85) return 4.0;
    if(score100>=70) return 3.0;
    if(score100>=55) return 2.0;
    if(score100>=40) return 1.0;
    return 0.0;
  }

  public double termGPA(String studentId, String term){
    double pts=0; int credits=0;
    var rs = InMemoryEnrollmentRepository.getInstance()
        .findByStudentAndTerm(studentId, term, InMemorySectionRepository.getInstance());
    for(var e: rs){
      double s100 = courseTotal(studentId, e.getSectionId());
      var sec = sections.findById(e.getSectionId());
      var c = courses.findByCode(sec.getCourseCode());
      pts += toGPA4(s100) * c.getCredits();
      credits += c.getCredits();
    }
    return credits>0? pts/credits : 0.0;
  }
}
