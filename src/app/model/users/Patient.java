package app.model.users;

import app.constants.Gender;
import app.constants.exceptions.AppointmentNotFound;
import app.constants.exceptions.MedicalRecordNotFound;
import app.model.appointments.Appointment;
import app.model.appointments.Appointment.AppointmentStatus;
import app.model.appointments.Prescription;
import app.model.personal_details.MedicalRecord;
import app.model.user_credentials.Password;
import app.model.user_credentials.Username;
import java.util.ArrayList;

public class Patient extends User {

    private static int uuid = 1;

    private ArrayList<MedicalRecord> medicalRecords;
    private ArrayList<Appointment> appointmentHistory;

    public Patient(
        Username username,
        Password password,
        String name,
        Gender gender
    ) {
        this(username, password, name, gender, new ArrayList<>(), new ArrayList<>());
    }

    public Patient(
        Username username,
        Password password,
        String name,
        Gender gender,
        ArrayList<MedicalRecord> medicalRecords,
        ArrayList<Appointment> appointmentHistory
    ) {
        super(username, password, name, gender);
        this.medicalRecords = medicalRecords;
        this.appointmentHistory = appointmentHistory;
    }

    public ArrayList<MedicalRecord> getMedicalRecords() {
        return medicalRecords;
    }

    public void setMedicalRecords(ArrayList<MedicalRecord> medicalRecords) {
        this.medicalRecords = medicalRecords;
    }

    @Override
    protected int generateUUID() {
        return Patient.uuid++;
    };

    public int getPatientId() {
        return this.id;
    }

    public void displayUserMenu() {
        // TODO
    }

    public void printMedicalRecord() throws MedicalRecordNotFound {
        if (medicalRecords.size() <= 0) {
            throw new MedicalRecordNotFound();
        }

        medicalRecords.get(medicalRecords.size()-1).print();
    }

    public void printTreatmentPlans() throws AppointmentNotFound{
        if (appointmentHistory.size() > 0) {
            throw new AppointmentNotFound();
        }

        ArrayList<Prescription> = appointmentHistory
            .stream()
            .filter(appointment -> appointment.getAppointmentStatus() == AppointmentStatus.COMPLETED)
            .map(appointment -> appointment.getPrescription());
    }
}