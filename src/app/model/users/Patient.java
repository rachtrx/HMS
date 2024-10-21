package app.model.users;

import app.constants.BloodType;
import app.constants.exceptions.MissingAppointmentException;
import app.model.appointments.Appointment;
import app.model.appointments.AppointmentOutcomeRecord;
import app.model.user_credentials.Email;
import app.model.user_credentials.MedicalRecord;
import app.model.user_credentials.PhoneNumber;
import app.utils.DateTimeUtil;
import app.utils.EnumUtils;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Patient extends User {

    private static int patientUuid = 1;
    private final int patientId;

    public static void setPatientUuid(int value) {
        patientUuid = value;
    }

    private final PhoneNumber mobileNumber;
    private final PhoneNumber homeNumber;
    private final Email email;
    private final LocalDate dateOfBirth;
    private final BloodType bloodType;
    private final List<Appointment> appointments;
    private final MedicalRecord medicalRecord;

    // IMPT medical record is built from patient's appointmentHistory
    
    public Patient(
        List<String> patientRow,
        List<String> userRow,
        List<Appointment> appointments,
        List<AppointmentOutcomeRecord> appointmentHistory
    ) throws Exception {
        super(userRow);
        this.patientId = Integer.parseInt(patientRow.get(0));
        this.mobileNumber = new PhoneNumber(patientRow.get(2));
        this.homeNumber = new PhoneNumber(patientRow.get(3));
        this.email = new Email(patientRow.get(4));
        this.dateOfBirth = DateTimeUtil.parseShortDate(patientRow.get(5));
        this.bloodType = EnumUtils.fromString(BloodType.class, patientRow.get(6)); // TODO
        this.appointments = appointments;
        this.medicalRecord = new MedicalRecord(this, appointmentHistory);
        Patient.setPatientUuid(Math.max(Patient.patientUuid, this.patientId)+1);
    }

    public Patient(
        String username,
        String password,
        String name,
        String patientId,
        String gender,
        String mobileNumber,
        String homeNumber,
        String email,
        String dateOfBirth,
        String bloodType
    ) throws Exception {
        super(username, password, name, gender);
        this.patientId = Patient.patientUuid++;
        this.mobileNumber = new PhoneNumber(mobileNumber);
        this.homeNumber = new PhoneNumber(homeNumber);
        this.email = new Email(email);
        this.dateOfBirth = DateTimeUtil.parseShortDate(dateOfBirth);
        this.bloodType = EnumUtils.fromString(BloodType.class, bloodType);
        this.appointments = new ArrayList<>();
        this.medicalRecord = new MedicalRecord(this, new ArrayList<>());
    }

    public int getPatientId() {
        return this.patientId;
    }

    public Integer getMobileNumber() {
        return mobileNumber.getNumber();
    }

    public void setMobileNumber(Integer mobileNumber) throws Exception {
        this.mobileNumber.setNumber(mobileNumber);
    }

    public void setMobileNumber(String mobileNumber) throws Exception {
        this.mobileNumber.setNumber(mobileNumber);
    }

    public Integer getHomeNumber() {
        return homeNumber.getNumber();
    }

    public void setHomeNumber(Integer homeNumber) throws Exception {
        this.homeNumber.setNumber(homeNumber);
    }

    public void setHomeNumber(String homeNumber) throws Exception {
        this.homeNumber.setNumber(homeNumber);
    }

    public String getEmail() {
        return email.getEmail();
    }

    public void setEmail(String email) throws Exception {
        this.email.setEmail(email);
    }

    // No need to set
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getBloodType() {
        return bloodType.toString();
    }

    // No need to set
    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void addAppointment(Appointment appointment) {
        this.appointments.add(appointment);
    }

    // No need to set
    public MedicalRecord getMedicalRecord() {
        return medicalRecord;
    }

    // public void addAppointmentRecord(AppointmentOutcomeRecord appointmentRecord) {
    //     this.medicalRecord.addAppointmentRecord(appointmentRecord);
    // }

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
        ));
    }

    public void printMedicalRecord() throws MissingAppointmentException {
        this.print();
        medicalRecord.printAppointmentHistory();
    }

    

    
}