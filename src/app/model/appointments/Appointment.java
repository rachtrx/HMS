package app.model.appointments;

import java.time.LocalDate;

/**
* Appointment.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class Appointment {
    public static enum AppointmentStatus {
        CONFIRMED {
            @Override
            public String toString() {
                return "Confirmed";
            }
        },
        CANCELLED {
            @Override
            public String toString() {
                return "Cancelled";
            }
        },
        COMPLETED {
            @Override
            public String toString() {
                return "Completed";
            }
        }
        
    }

    private static int uuid = 1;
    
    private final int id = Appointment.uuid++;
    private int patientId;
    private int doctorId;
    private AppointmentStatus appointmentStatus;
    private Timeslot timeslot;
    private AppointmentOutcomeRecord appointmentOutcome;
    
    public Appointment(
        int patientId,
        int doctorId,
        AppointmentStatus appointmentStatus,
        Timeslot timeslot,
        AppointmentOutcomeRecord appointmentOutcome
    ) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentStatus = appointmentStatus;
        this.timeslot = timeslot;
        this.appointmentOutcome = appointmentOutcome;
    }

    public int getId() {
        return this.id;
    }

    public int getPatientId() {
        return this.patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getDoctorId() {
        return this.doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public AppointmentStatus getAppointmentStatus() {
        return this.appointmentStatus;
    }

    public void cancel() {
        this.appointmentStatus = AppointmentStatus.CANCELLED;
    }

    public void confirm() {
        this.appointmentStatus = AppointmentStatus.CONFIRMED;
    }

    public void completed() {
        this.appointmentStatus = AppointmentStatus.COMPLETED;
    }

    public LocalDate getAppointmentDateTime() {
        return this.appointmentDateTime;
    }

    public void setAppointmentDateTime(LocalDate appointmentDateTime) {
        this.appointmentDateTime = appointmentDateTime;
    }

    public AppointmentOutcomeRecord getAppointmentOutcome() {
        return this.appointmentOutcome;
    }

    public void setAppointmentOutcome(AppointmentOutcomeRecord appointmentOutcome) {
        this.appointmentOutcome = appointmentOutcome;
    }    
}
