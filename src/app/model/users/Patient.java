package app.model.users;

import app.constants.BloodType;
import app.model.ISerializable;
import app.model.appointments.Appointment;
import app.model.users.user_credentials.Email;
import app.model.users.user_credentials.PhoneNumber;
import app.utils.DateTimeUtil;
import app.utils.EnumUtils;
import app.utils.LoggerUtils;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Patient extends User implements AppointmentManager {

    private static int patientUuid = 1;
    private final int patientId;

    public static void setPatientUuid(int value) {
        patientUuid = value;
    }

    private final PhoneNumber mobileNumber;
    private final PhoneNumber homeNumber;
    private final Email email;
    private final LocalDate dateOfBirth;
    private BloodType bloodType;
    private final MedicalRecord medicalRecord;
    
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
        add(this); // TODO move to factory method?
        LoggerUtils.info("Patient created");
    }
    
    // IMPT medical record is built from patient's appointmentHistory
    protected Patient(
            List<String> userRow,
            List<String> patientRow,
            List<Appointment> appointments
    ) throws Exception {
        super(userRow);
        LoggerUtils.info(String.join(", ", patientRow));
        String patientIdStr = patientRow.get(0);
        this.patientId = Integer.parseInt(patientIdStr);
        this.mobileNumber = new PhoneNumber(patientRow.get(2));
        this.homeNumber = new PhoneNumber(patientRow.get(3));
        this.email = new Email(patientRow.get(4));
        this.dateOfBirth = DateTimeUtil.parseShortDate(patientRow.get(5));
        this.bloodType = EnumUtils.fromString(BloodType.class, patientRow.get(6)); // TODO
        Patient.setPatientUuid(Math.max(Patient.patientUuid, this.patientId) + 1);
        this.medicalRecord = new MedicalRecord(patientIdStr, appointments);
        LoggerUtils.info("Patient " + this.getName() + " created");
    }

    @Override
    public List<String> serialize() {
        List<String> accRow = super.serialize();
        
        List<String> row = new ArrayList<>();

        row.add(String.valueOf(this.getRoleId()));
        row.add(this.getMobileNumber().toString());
        row.add(this.getHomeNumber().toString());
        row.add(this.getEmail());
        row.add(DateTimeUtil.printShortDate(this.getDateOfBirth()));
        row.add(this.getBloodType());

        accRow.addAll(row);
        return accRow;
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
        update(this);
    }

    public void setMobileNumber(String mobileNumber) throws Exception {
        this.mobileNumber.setNumber(mobileNumber);
        update(this);
    }

    public Integer getHomeNumber() {
        return homeNumber.getNumber();
    }

    public void setHomeNumber(Integer homeNumber) throws Exception {
        this.homeNumber.setNumber(homeNumber);
        update(this);
    }

    public void setHomeNumber(String homeNumber) throws Exception {
        this.homeNumber.setNumber(homeNumber);
        update(this);
    }

    public String getEmail() {
        return email.getEmail();
    }

    public void setEmail(String email) throws Exception {
        this.email.setEmail(email);
        update(this);
    }

    // No need to set
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
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
            throw new Exception(String.format(
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