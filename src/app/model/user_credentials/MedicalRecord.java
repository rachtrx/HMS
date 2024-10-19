package app.model.user_credentials;

import app.constants.exceptions.AppointmentNotFound;
import app.model.appointments.AppointmentOutcomeRecord;
import app.model.users.Patient;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MedicalRecord {

    // Constructor START
    private Patient patient; // Composition: Patient and MR both mapped to each other, but MR is always created from Patient
    private List<AppointmentOutcomeRecord> appointmentHistory;
    // TODO: diagnoses and treatments

    public MedicalRecord(
        Patient patient,
        List<AppointmentOutcomeRecord> appointmentHistory
    ) {
        this.patient = patient;
        this.appointmentHistory = appointmentHistory;
    }

    public MedicalRecord(
        Patient patient
    ) {
        this.patient = patient;
        this.appointmentHistory = new ArrayList<>();
    }
    // Constructor END

    // Getters and Setters START
    
    /** 
     * @return int
     */
    public int getPatientId() {
        return this.patient.getPatientId();
    }
    
    /** 
     * @return int
     */
    public int getMobileNumber() {
        return this.patient.getMobileNumber();
    }

    
    /** 
     * @param mobileNumber
     */
    public void setMobileNumber(Integer mobileNumber) throws Exception {
        this.patient.setMobileNumber(mobileNumber);
    }

    
    /** 
     * @return int
     */
    public int getHomeNumber() {
        return this.patient.getHomeNumber();
    }

    
    /** 
     * @param homeNumber
     */
    public void setHomeNumber(Integer homeNumber) throws Exception {
        this.patient.setHomeNumber(homeNumber);
    }

    
    /** 
     * @return String
     */
    public String getEmail() {
        return this.patient.getEmail();
    }

    
    /** 
     * @param email
     */
    public void setEmail(String email) throws Exception {
        this.patient.setEmail(email);
    }

    
    /** 
     * @return LocalDate
     */
    public LocalDate getDateOfBirth() {
        return this.patient.getDateOfBirth();
    }

    
    /** 
     * @return BloodType
     */
     public String getBloodType() {
        return this.patient.getBloodType();
    }
    // Getters and Setters END


    public void print() throws AppointmentNotFound {
        this.patient.print();
        this.printAppointmentHistory();
    }

    public void printAppointmentHistory() throws AppointmentNotFound {
    
        // Check if the appointment history is empty
        if (appointmentHistory.isEmpty()) {
            throw new AppointmentNotFound("No appointments found.");
        }
    
        System.out.println("Completed Appointments with Prescriptions:");
        for (AppointmentOutcomeRecord record : appointmentHistory) {
            record.printDetails();
        }
    }
}