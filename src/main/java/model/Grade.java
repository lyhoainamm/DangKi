package model;
import model.GradeComponent;


public class Grade {
    private final String studentId, sectionId;
    private final GradeComponent comp;
    private double score;
    private double weight;

    public Grade(String studentId, String sectionId, GradeComponent comp, double score, double weight){
        this.studentId = studentId;
        this.sectionId = sectionId;
        this.comp = comp;
        this.score = score;
        this.weight = weight;
    }

    public String getStudentId(){ return studentId; }
    public String getSectionId(){ return sectionId; }
    public GradeComponent getComp(){ return comp; }
    public double getScore(){ return score; }
    public double getWeight(){ return weight; }
    public void setScore(double s){ this.score = s; }
    public void setWeight(double w){ this.weight = w; }
}
