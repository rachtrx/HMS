package app.model.user_credentials;

import app.constants.exceptions.MissingAppointmentException;
import app.model.appointments.Appointment;
import app.model.users.AppointmentManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
* Patient medical record.
*
* @author Rachmiel Teo (@rachtrx)
* @version 1.0
* @since 2024-10-17
*/
public class MedicalRecord implements AppointmentManager {

    // Constructor START
    private int patientId;
    private List<Appointment> appointments;
    // TODO: diagnoses and treatments

    public MedicalRecord(
        String patientId,
        List<Appointment> appointments
    ) {
        this.patientId = Integer.parseInt(patientId);
        this.appointments = appointments;
    }

    public MedicalRecord(
        int patientId
    ) {
        this.patientId = patientId;
        this.appointments = new ArrayList<>();
    }
    // Constructor END

    // Getters and Setters START

    public int getPatientId() {
        return this.patientId;
    }

    @Override
    public List<Appointment> getAppointments() {
        return appointments;
    }

    @Override
    public void addAppointment(Appointment appointment) {
        this.appointments.add(appointment);
    }

    @Override
    public void deleteAppointment(int appointmentId) {
        Iterator<Appointment> iterator = appointments.iterator();
        while (iterator.hasNext()) {
            Appointment appointment = iterator.next();
            if (appointment.getAppointmentId() == appointmentId) {
                iterator.remove();
                break;
            }
        }
    }

    public void print() throws MissingAppointmentException {
        this.printAppointmentHistory();
    }

    public void printAppointmentHistory() throws MissingAppointmentException {
    
        // Check if the appointment history is empty
        if (appointments.isEmpty()) {
            throw new MissingAppointmentException("No appointments found.");
        }
    
        System.out.println("Completed Appointments with Prescriptions:");
        // for (AppointmentOutcomeRecord record : appointmentHistory) {
        //     record.printDetails();
        // }
    }
}