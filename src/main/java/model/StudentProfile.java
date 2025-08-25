package model;
public class StudentProfile {
  private final String studentId; // map với User/Student.id
  private String faculty, major, cohort, classCode, phone, email;
  public StudentProfile(String studentId){ this.studentId=studentId; }
  public String getStudentId(){return studentId;}
  public String getFaculty(){return faculty;} public void setFaculty(String v){faculty=v;}
  public String getMajor(){return major;} public void setMajor(String v){major=v;}
  public String getCohort(){return cohort;} public void setCohort(String v){cohort=v;}
  public String getClassCode(){return classCode;} public void setClassCode(String v){classCode=v;}
  public String getPhone(){return phone;} public void setPhone(String v){phone=v;}
  public String getEmail(){return email;} public void setEmail(String v){email=v;}
}
