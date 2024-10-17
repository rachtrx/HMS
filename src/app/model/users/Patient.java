package app.model.users;

import app.constants.BloodType;
import app.constants.Gender;
import app.constants.exceptions.AppointmentNotFound;
import app.constants.exceptions.MedicalRecordNotFound;
import app.model.appointments.Appointment;
import app.model.appointments.Appointment.AppointmentStatus;
import app.model.appointments.AppointmentOutcomeRecord;
import app.model.appointments.Prescription;
import app.model.user_credentials.Email;
import app.model.user_credentials.MedicalRecord;
import app.model.user_credentials.Password;
import app.model.user_credentials.PhoneNumber;
import app.model.user_credentials.Username;
import app.utils.DateTimeUtil;

import java.time.LocalDate;
import java.util.ArrayList;

public class Patient extends User {

    private static int uuid = 1;

    public static int getUuid() {
        return uuid;
    }

    public static void setUuid(int uuid) {
        Patient.uuid = uuid;
    }

    private final PhoneNumber mobileNumber;
    private final PhoneNumber homeNumber;
    private final Email email;
    private final LocalDate dateOfBirth;
    private final BloodType bloodType;
    private final ArrayList<Appointment> appointments;
    private final MedicalRecord medicalRecord;

    // IMPT medical record is built from patient's appointmentHistory

    public Patient(
        Username username,
        Password password,
        String name,
        Gender gender,
        PhoneNumber mobileNumber,
        PhoneNumber homeNumber,
        Email email,
        LocalDate dateOfBirth,
        BloodType bloodType,
        ArrayList<Appointment> appointments, // UPON appointment COMPLETED, push into appointment history
        ArrayList<AppointmentOutcomeRecord> appointmentHistory
    ) {
        super(username, password, name, gender);
        this.mobileNumber = mobileNumber;
        this.homeNumber = homeNumber;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.bloodType = bloodType;
        this.appointments = appointments;
        this.medicalRecord = new MedicalRecord(this, appointmentHistory);
    }

    public Patient(
        Username username,
        Password password,
        String name,
        Gender gender,
        PhoneNumber mobileNumber,
        PhoneNumber homeNumber,
        Email email,
        LocalDate dateOfBirth,
        BloodType bloodType,
        ArrayList<Appointment> appointments
    ) {
        super(username, password, name, gender);
        this.mobileNumber = mobileNumber;
        this.homeNumber = homeNumber;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.bloodType = bloodType;
        this.appointments = appointments;
        this.medicalRecord = new MedicalRecord(this, new ArrayList<>());
    }

    @Override
    protected int generateUUID() {
        return Patient.uuid++;
    };

    public int getPatientId() {
        return this.id;
    }

    public int getMobileNumber() {
        return mobileNumber.getNumber();
    }

    public void setMobileNumber(Integer mobileNumber) throws Exception {
        this.mobileNumber.setNumber(mobileNumber);
    }

    public int getHomeNumber() {
        return homeNumber.getNumber();
    }

    public void setHomeNumber(Integer homeNumber) throws Exception {
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
    public ArrayList<Appointment> getAppointments() {
        return appointments;
    }

    public void addAppointment(Appointment appointment) {
        this.appointments.add(appointment);
    }

    // No need to set
    public MedicalRecord getMedicalRecord() {
        return medicalRecord;
    }

    public void addAppointmentRecord(AppointmentOutcomeRecord appointmentRecord) {
        this.medicalRecord.addAppointmentRecord(appointmentRecord);
    }

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

    public void printMedicalRecord() throws AppointmentNotFound {
        this.print();
        medicalRecord.printAppointmentHistory();
    }

    

    
}