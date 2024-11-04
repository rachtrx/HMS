package app.model.users;

import app.constants.BloodType;
import app.model.appointments.Appointment;
import app.model.user_credentials.Email;
import app.model.user_credentials.MedicalRecord;
import app.model.user_credentials.PhoneNumber;
import app.utils.DateTimeUtil;
import app.utils.EnumUtils;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Patient extends User implements AppointmentManager{

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
    private final MedicalRecord medicalRecord;

    // IMPT medical record is built from patient's appointmentHistory

    public Patient(
        List<String> patientRow,
        List<String> userRow,
        MedicalRecord medicalRecord
    ) throws Exception {
        super(userRow);
        this.patientId = Integer.parseInt(patientRow.get(0));
        this.mobileNumber = new PhoneNumber(patientRow.get(2));
        this.homeNumber = new PhoneNumber(patientRow.get(3));
        this.email = new Email(patientRow.get(4));
        this.dateOfBirth = DateTimeUtil.parseShortDate(patientRow.get(5));
        this.bloodType = EnumUtils.fromString(BloodType.class, patientRow.get(6)); // TODO
        Patient.setPatientUuid(Math.max(Patient.patientUuid, this.patientId)+1);
        this.medicalRecord = medicalRecord;
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
        this.medicalRecord = new MedicalRecord(this.patientId); // Medical record created upon instantiation of patient
    }

    public MedicalRecord getMedicalRecord() {
        return medicalRecord;
    }

    @Override
    public List<Appointment> getAppointments() {
        return this.getMedicalRecord().getAppointments();
    }

    @Override
    public void addAppointment(Appointment appointment) {
        this.getMedicalRecord().addAppointment(appointment);
    }

    @Override
    public void deleteAppointment(int appointmentId) {
        this.getMedicalRecord().deleteAppointment(appointmentId);
    }

    @Override
    public int getRoleId() {
        return this.patientId;
    };

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

    // public void addAppointmentRecord(AppointmentOutcomeRecord appointmentRecord) {
    //     this.medicalRecord.addAppointmentRecord(appointmentRecord);
    // }

    public void print() {
        System.out.println(String.join(
            "\n",
            String.format("1. Patient Name: %s", this.getName()),
            String.format("2. Patient ID: %d", this.getRoleId()),
            String.format("3. Date of Birth: %s", DateTimeUtil.printLongDate(this.dateOfBirth)),
            String.format("4. Gender: %s", this.getGender()),
            String.format("5. Mobile number: +65%d", this.getMobileNumber()),
            String.format("6. Home number: +65%d", this.getHomeNumber()),
            String.format("7. Email: %s", this.getEmail()),
            String.format("8. Blood Type: %s", this.getBloodType())
        ));
    }

    // public void printMedicalRecord() throws MissingAppointmentException {
    //     this.print();
    //     medicalRecord.printAppointmentHistory();
    // }

    

    
}