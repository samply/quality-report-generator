package de.samply.reporter.context;

import java.util.HashSet;
import java.util.Set;

public class Error {

  private String error;
  private Set<String> patientIds = new HashSet<>();

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public Set<String> getPatientIds() {
    return patientIds;
  }

  public void setPatientIds(Set<String> patientIds) {
    this.patientIds = patientIds;
  }

  public void addPatientId (String patientId){
    this.patientIds.add(patientId);
  }

}
