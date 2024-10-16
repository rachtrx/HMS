package app.model.users;

import app.utils.DateTimeUtil;
import java.time.LocalDate;

public class MedicalRecord {
    
    // private int recordId;
    private int recordId;
    private int patientId;
    private String name;
    private LocalDate doB;
    private char gender;
    private String bloodType;
    // private int number;
    private String email;

    public MedicalRecord(int recordId, String patientId, String name, String doB, char gender, String bloodType, String email) {
        this.recordId = recordId;
        this.patientId = Integer.parseInt(patientId);
        this.name = name; // TODO should be in User?
        this.doB = DateTimeUtil.parseShortDate(doB);
        this.gender = gender;
        this.bloodType = bloodType;
        this.email = email;
    }

    public String getGender() {
        return gender == 'F' ? "Female" : "Male";
    }

    public int getPatientId() {
        return patientId;
    }
    
    // public void setPhoneNumber(int number) {
    //     this.number = number;
    // }

    public void setEmailAddress(String email) {
        this.email = email;
    }

    public void print() {

        System.out.printf("""
            =================================================================
                           Medical Record (Record ID : %d)            
            =================================================================
            Patient ID : %s  
            Name : %s  
            Date of Birth : %s
            Gender : %s
            Contact Information : %s
            Blood Type : %s
            Past Diagnosis : 
            Treatment :  
            "================================================================="
                        """,this.recordId,
                            this.patientId,
                            this.name,
                            DateTimeUtil.printLongDate(this.doB),
                            this.gender,
                            this.email,
                            this.bloodType);
    }

    public void getTreatmentPlans() {
        // TODO
    }

    public void getDiagnoses() {
        // TODO
    }
}