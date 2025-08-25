package model;
public class CourseCoreq {
  private final String courseCode, coreqCode;
  public CourseCoreq(String c, String r){ courseCode=c; coreqCode=r; }
  public String getCourseCode(){return courseCode;} public String getCoreqCode(){return coreqCode;}
}
