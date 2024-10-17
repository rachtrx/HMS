package app.model.appointments;

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
    private final int patientId;
    private final int doctorId;
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

    // Pending Appointment
    public Appointment(
        int patientId,
        int doctorId,
        AppointmentStatus appointmentStatus,
        Timeslot timeslot
    ) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentStatus = appointmentStatus;
        this.timeslot = timeslot;
    }

    public int getId() {
        return this.id;
    }

    public int getPatientId() {
        return this.patientId;
    }

    public int getDoctorId() {
        return this.doctorId;
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

    public Timeslot getTimeslot() {
        return this.timeslot;
    }

    public void setTimeslot(Timeslot timeslot) {
        this.timeslot = timeslot;
    }

    public AppointmentOutcomeRecord getAppointmentOutcome() {
        return this.appointmentOutcome;
    }

    public void setAppointmentOutcome(AppointmentOutcomeRecord appointmentOutcome) {
        this.appointmentOutcome = appointmentOutcome;
    }    
}
