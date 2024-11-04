package app.model.appointments;

import app.utils.EnumUtils;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
* Appointment.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class Appointment extends DoctorEvent {

    public enum AppointmentStatus {
    public static enum AppointmentStatus {
        PENDING {
            @Override
            public String toString() {
                return "Pending";
            }
        },
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
        },
        PENDING {
            @Override
            public String toString() {
                return "Pending";
            }
        };

        // Method to check if the status is in the allowed statuses
        public static boolean isIn(AppointmentStatus status, AppointmentStatus... allowedStatuses) {
            return Arrays.asList(allowedStatuses).contains(status);
        }
    }

    private static int appointmentUuid = 1;
    private final int appointmentId;
    public static void setAppointmentUuid(int value) {
        appointmentUuid = value;
    }

    private final int patientId;
    private AppointmentStatus appointmentStatus;
    private AppointmentOutcomeRecord appointmentOutcome;

    // Pending Appointment
    public Appointment(
        int doctorId,
        LocalDateTime timeslot,
        int patientId
    ) throws Exception {
        super(doctorId, timeslot);
        this.appointmentId = Appointment.appointmentUuid++;
        this.patientId = patientId;
        this.appointmentStatus = AppointmentStatus.PENDING;
        this.appointmentOutcome = null;
    }

    public Appointment(
        List<String> row, List<String> doctorEventRow,
        AppointmentOutcomeRecord appointmentOutcome
    ) throws Exception {
        super(doctorEventRow);
        this.appointmentId = Integer.parseInt(row.get(0));
        this.patientId = Integer.parseInt(row.get(2));
        this.appointmentStatus = EnumUtils.fromString(AppointmentStatus.class, row.get(3));
        Appointment.setAppointmentUuid(Math.max(Appointment.appointmentUuid, this.appointmentId)+1);
        this.appointmentOutcome = appointmentOutcome;
        
    }

    public Integer getAppointmentId() {
        return appointmentId;
    }

    public Integer getPatientId() {
        return this.patientId;
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

    public AppointmentOutcomeRecord getAppointmentOutcome() {
        return appointmentOutcome;
    }

    public void setAppointmentOutcome(AppointmentOutcomeRecord appointmentOutcome) {
        this.appointmentOutcome = appointmentOutcome;
    }
}
