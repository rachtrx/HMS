package app.model.users;

import app.constants.BloodType;
import app.constants.Gender;
import app.model.personal_details.BasicPersonalDetails;
import app.model.personal_details.Email;
import app.model.personal_details.PhoneNumber;
import app.utils.DateTimeUtil;
import java.time.LocalDate;

public class MedicalRecord extends BasicPersonalDetails {

    // Constructor START
    private static int uuid = 1;
    
    private final int id = MedicalRecord.uuid++;
    private int patientId;
    private PhoneNumber mobileNumber;
    private PhoneNumber homeNumber;
    private Email email;
    private LocalDate dateOfBirth;
    private BloodType bloodType;
    // TODO: diagnoses and treatments

    public MedicalRecord(
        int patientId,
        String name,
        PhoneNumber mobileNumber,
        PhoneNumber homeNumber,
        Email email,
        LocalDate dateOfBirth,
        Gender gender,
        BloodType bloodType
    ) {
        super(name, gender);
        this.patientId = patientId;
        this.mobileNumber = mobileNumber;
        this.homeNumber = homeNumber;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.bloodType = bloodType;
    }

    public MedicalRecord(
        Patient patient,
        PhoneNumber mobileNumber,
        PhoneNumber homeNumber,
        Email email,
        LocalDate dateOfBirth,
        BloodType bloodType
    ) {
        this(
            patient.getPatientId(),
            patient.getName(),
            mobileNumber,
            homeNumber,
            email,
            dateOfBirth,
            patient.getGender(),
            bloodType
        );
    }
    // Constructor END

    // Getters and Setters START
    /** 
     * @return int
     */
    
    /** 
     * @return int
     */
    
    /** 
     * @return int
     */
    public int getPatientId() {
        return this.patientId;
    }

    
    /** 
     * @param patientId
     */
    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    
    /** 
     * @return int
     */
    public int getMobileNumber() {
        return this.mobileNumber.getNumber();
    }

    
    /** 
     * @param mobileNumber
     */
    public void setMobileNumber(PhoneNumber mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    
    /** 
     * @return int
     */
    public int getHomeNumber() {
        return this.homeNumber.getNumber();
    }

    
    /** 
     * @param homeNumber
     */
    public void setHomeNumber(PhoneNumber homeNumber) {
        this.homeNumber = homeNumber;
    }

    
    /** 
     * @return String
     */
    public String getEmail() {
        return this.email.getEmail();
    }

    
    /** 
     * @param email
     */
    public void setEmail(Email email) {
        this.email = email;
    }

    
    /** 
     * @return LocalDate
     */
    public LocalDate getDateOfBirth() {
        return this.dateOfBirth;
    }

    
    /** 
     * @param dateOfBirth
     */
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    
    /** 
     * @return BloodType
     */
    
     public BloodType getBloodType() {
        return this.bloodType;
    }

    
    /** 
     * @param bloodType
     */
    public void setBloodType(BloodType bloodType) {
        this.bloodType = bloodType;
    }
    // Getters and Setters END

    public void print() {
        System.out.println(String.join(
            "\n",
            String.format("Patient ID: %d", this.getPatientId()),
            String.format("Name: %s", this.getName()),
            String.format("Date of Birth: %s", DateTimeUtil.printShortDate(this.dateOfBirth)),
            String.format("Gender: %s", this.getGender()),
            String.format("Mobile number: %d", this.getMobileNumber()),
            String.format("Home number: %d", this.getMobileNumber()),
            String.format("Email: %s", this.getEmail()),
            String.format("Blood Type: %s", this.getBloodType())
            // TODO Past Diagnoses and Treatments
        ));
    }

    public void getTreatmentPlans() {
        // TODO
    }

    public void getDiagnoses() {
        // TODO
    }
}