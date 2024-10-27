package app.model.user_credentials;

import app.constants.exceptions.MissingAppointmentException;
import app.model.appointments.AppointmentOutcomeRecord;
import java.util.ArrayList;
import java.util.List;

/**
* Patient medical record.
*
* @author Rachmiel Teo (@rachtrx)
* @version 1.0
* @since 2024-10-17
*/
public class MedicalRecord {

    // Constructor START
    private List<AppointmentOutcomeRecord> appointmentHistory;
    private int patientId;
    // TODO: diagnoses and treatments

    public MedicalRecord(
        int patientId,
        List<AppointmentOutcomeRecord> appointmentHistory
    ) {
        this.patientId = patientId;
        this.appointmentHistory = appointmentHistory;
    }

    public MedicalRecord(
        int patientId
    ) {
        this(patientId, new ArrayList<>());
    }
    // Constructor END

    // Getters and Setters START

    public int getPatientId() {
        return this.patientId;
    }

    public List<AppointmentOutcomeRecord> getAppointmentOutcomes() {
        return appointmentHistory;
    }

    public void addAppointmentOutcome(AppointmentOutcomeRecord outcome) {
        this.appointmentHistory.add(outcome);
    }

    public void print() throws MissingAppointmentException {
        this.printAppointmentHistory();
    }

    public void printAppointmentHistory() throws MissingAppointmentException {
    
        // Check if the appointment history is empty
        if (appointmentHistory.isEmpty()) {
            throw new MissingAppointmentException("No appointments found.");
        }
    
        System.out.println("Completed Appointments with Prescriptions:");
        // for (AppointmentOutcomeRecord record : appointmentHistory) {
        //     record.printDetails();
        // }
    }
}