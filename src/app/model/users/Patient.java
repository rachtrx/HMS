package app.model.users;

import app.constants.BloodType;
import app.db.DatabaseManager;
import app.model.appointments.Appointment;
import app.model.users.user_credentials.Email;
import app.model.users.user_credentials.PhoneNumber;
import app.utils.EnumUtils;
import app.utils.LoggerUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Patient extends User implements AppointmentManager {

    private static int patientUuid = 1;
    private final int patientId;

    public static void setPatientUuid(int value) {
        patientUuid = value;
    }

    private final PhoneNumber mobileNumber;
    private final PhoneNumber homeNumber;
    private final Email email;
    private BloodType bloodType;
    private final MedicalRecord medicalRecord;
    
    private Patient(
        String username,
        String name,
        String gender,
        String dateOfBirth,
        String mobileNumber,
        String homeNumber,
        String email,
        String bloodType
    ) throws Exception {
        super(username, name, gender, dateOfBirth);
        this.patientId = Patient.patientUuid++;
        this.mobileNumber = new PhoneNumber(mobileNumber);
        this.homeNumber = new PhoneNumber(homeNumber);
        this.email = new Email(email);
        this.bloodType = EnumUtils.fromString(BloodType.class, bloodType);
        this.medicalRecord = new MedicalRecord(this.patientId); // Medical record created upon instantiation of patient
    }

    public static Patient create(String username, String name, String gender, String dateOfBirth, String mobileNumber, String homeNumber, String email, String bloodType) throws Exception {
        Patient patient = new Patient(username, name, gender, dateOfBirth, mobileNumber, homeNumber, email, bloodType);
        DatabaseManager.add(patient);
        LoggerUtils.info("Patient created");
        return patient;
    }
    
    // IMPT medical record is built from patient's appointmentHistory
    protected Patient(
            List<String> userRow,
            List<String> patientRow,
            List<Appointment> appointments
    ) throws Exception {
        super(userRow);
        // LoggerUtils.info(String.join(", ", patientRow));
        String patientIdStr = patientRow.get(0);
        this.patientId = Integer.parseInt(patientIdStr);
        this.mobileNumber = new PhoneNumber(patientRow.get(2));
        this.homeNumber = new PhoneNumber(patientRow.get(3));
        this.email = new Email(patientRow.get(4));
        this.bloodType = EnumUtils.fromString(BloodType.class, patientRow.get(5)); // TODO
        Patient.setPatientUuid(Math.max(Patient.patientUuid, this.patientId+1));
        this.medicalRecord = new MedicalRecord(patientIdStr, appointments);
        LoggerUtils.info("Patient " + this.getName() + " created");
    }

    @Override
    public List<String> serialize() {
        List<String> accRow = super.serialize();
        
        List<String> row = new ArrayList<>();

        row.add(String.valueOf(this.getRoleId()));
        row.add(String.valueOf(this.getUserId()));
        row.add(this.getMobileNumber().toString());
        row.add(this.getHomeNumber().toString());
        row.add(this.getEmail());
        row.add(this.getBloodType());

        accRow.addAll(row);
        return accRow;
    }


    public MedicalRecord getMedicalRecord() {
        return medicalRecord;
    }

    @Override
    public List<Appointment> getAppointments() {
        return this.getMedicalRecord().getAppointments()
            .stream()
            .sorted(Comparator.comparing(Appointment::getTimeslot))
            .collect(Collectors.toList());
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
        DatabaseManager.update(this);
    }

    public void setMobileNumber(String mobileNumber) throws Exception {
        this.mobileNumber.setNumber(mobileNumber);
        DatabaseManager.update(this);
    }

    public Integer getHomeNumber() {
        return homeNumber.getNumber();
    }

    public void setHomeNumber(Integer homeNumber) throws Exception {
        this.homeNumber.setNumber(homeNumber);
        DatabaseManager.update(this);
    }

    public void setHomeNumber(String homeNumber) throws Exception {
        this.homeNumber.setNumber(homeNumber);
        DatabaseManager.update(this);
    }

    public String getEmail() {
        return email.getEmail();
    }

    public void setEmail(String email) throws Exception {
        this.email.setEmail(email);
        DatabaseManager.update(this);
    }

    public String getBloodType() {
        return bloodType.toString();
    }

    public void setBloodType(String bloodType) throws Exception {
        try {
            for (BloodType bt : BloodType.values()) {
                if (bt.toString().equalsIgnoreCase(bloodType)) {
                    this.setBloodType(bt);
                    return;
                }
            }
            this.setBloodType(BloodType.valueOf(bloodType.trim().toUpperCase()));
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format(
                "Invalid blood type. Try one of: %s",
                String.join(
                    ", ",
                    Stream.of(BloodType.values())
                        .map(Object::toString)
                        .collect(Collectors.toList())
                )
            ));
        }
    }

    public void setBloodType(BloodType bloodType) {
        this.bloodType = bloodType;
    }

    // public void addAppointmentRecord(AppointmentOutcomeRecord appointmentRecord) {
    //     this.medicalRecord.addAppointmentRecord(appointmentRecord);
    // }

    @Override
    public String toString() {
        return String.join(
            "\n",
            String.format("Patient ID: %d", this.getRoleId()),
            super.toString(),
            String.format("Mobile number: +65%d", this.getMobileNumber()),
            String.format("Home number: +65%d", this.getHomeNumber()),
            String.format("Email: %s", this.getEmail()),
            String.format("Blood Type: %s", this.getBloodType())
        );
    }

    // public void printMedicalRecord() throws MissingAppointmentException {
    //     this.print();
    //     medicalRecord.printAppointmentHistory();
    // }

    

    
}