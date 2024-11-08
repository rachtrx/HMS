package app.model.appointments;

import app.db.DatabaseManager;
import app.utils.EnumUtils;
import app.utils.LoggerUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
        };

        // Method to check if the status is in the allowed statuses
        public static boolean isIn(AppointmentStatus status, AppointmentStatus... allowedStatuses) {
            return Arrays.asList(allowedStatuses).contains(status);
        }
    }

    private static int appointmentUuid = 1;
    private final int appointmentId;
    public final String filename = "src/resources/Appointment_List.csv";

    public static void setAppointmentUuid(int value) {
        appointmentUuid = value;
    }

    private final int patientId;
    private AppointmentStatus appointmentStatus;
    private AppointmentOutcomeRecord appointmentOutcome;

    // Pending Appointment
    private Appointment(
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

    public static Appointment create(
        int doctorId,
        LocalDateTime timeslot,
        int patientId
    ) throws Exception {
        Appointment a = new Appointment(doctorId, timeslot, patientId);
        LoggerUtils.info("Appointment created");
        DatabaseManager.add(a);
        return a;
    }

    protected Appointment(
        List<String> doctorEventRow, List<String> row,
        AppointmentOutcomeRecord appointmentOutcome
    ) throws Exception {
        super(doctorEventRow);
        // LoggerUtils.info(String.join(", ", row));
        this.appointmentId = Integer.parseInt(row.get(0));
        this.patientId = Integer.parseInt(row.get(2));
        this.appointmentStatus = EnumUtils.fromString(AppointmentStatus.class, row.get(3));
        Appointment.setAppointmentUuid(Math.max(Appointment.appointmentUuid, this.appointmentId)+1);
        this.appointmentOutcome = appointmentOutcome;
        // LoggerUtils.info(String.valueOf(Appointment.appointmentUuid));
    }

    @Override
    public List<String> serialize() {
        List<String> accRow = super.serialize();

        List<String> row = new ArrayList<>();
        row.add(String.valueOf(this.getAppointmentId()));
        row.add(String.valueOf(this.getId())); // doctor event id
        row.add(String.valueOf(this.getPatientId()));
        row.add(String.valueOf(this.getAppointmentStatus().toString()));

        accRow.addAll(row);

        return accRow;
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

    public void setAppointmentStatus(AppointmentStatus appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
        DatabaseManager.update(this);
    }

    public void cancel() {
        this.setAppointmentStatus(AppointmentStatus.CANCELLED);
    }

    public void confirm() {
        this.setAppointmentStatus(AppointmentStatus.CONFIRMED);
    }

    public void completed() {
        this.setAppointmentStatus(AppointmentStatus.COMPLETED);
    }

    public AppointmentOutcomeRecord getAppointmentOutcome() {
        return appointmentOutcome;
    }

    public void setAppointmentOutcome(AppointmentOutcomeRecord appointmentOutcome) {
        this.appointmentOutcome = appointmentOutcome;
        DatabaseManager.update(this);
    }
}
