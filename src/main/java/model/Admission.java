package model;
import java.time.LocalDateTime;

public class Admission {
  private final String id, fullName, citizenId, major, cohort; // khóa/niên khóa
  private AdmissionStatus status = AdmissionStatus.APPLIED;
  private final LocalDateTime createdAt = LocalDateTime.now();
  public Admission(String id, String fullName, String citizenId, String major, String cohort){
    this.id=id; this.fullName=fullName; this.citizenId=citizenId; this.major=major; this.cohort=cohort;
  }
  public String getId(){return id;} public String getFullName(){return fullName;}
  public String getCitizenId(){return citizenId;} public String getMajor(){return major;}
  public String getCohort(){return cohort;} public AdmissionStatus getStatus(){return status;}
  public void setStatus(AdmissionStatus s){status=s;}
}
