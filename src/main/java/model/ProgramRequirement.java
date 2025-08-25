package model;
public class ProgramRequirement {
  private final String id, programCode, courseCode;
  private final ProgramRequirementType type;
  private final String group; // dùng cho nhóm tự chọn
  public ProgramRequirement(String id,String programCode,String courseCode,ProgramRequirementType type,String group){
    this.id=id; this.programCode=programCode; this.courseCode=courseCode; this.type=type; this.group=group;
  }
  public String getProgramCode(){return programCode;} public String getCourseCode(){return courseCode;}
  public ProgramRequirementType getType(){return type;} public String getGroup(){return group;}
}
