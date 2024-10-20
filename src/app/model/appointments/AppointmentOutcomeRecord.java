package app.model.appointments;

import app.utils.DateTimeUtil;
import app.utils.EnumUtils;
import java.time.LocalDateTime;
import java.util.List;

/**
* Appointments' outcome record.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class AppointmentOutcomeRecord {
    private static int uuid = 1;
    private final int id;

    public static void setUuid(int value) {
        uuid = value;
    }

    public static enum ServiceType {
        XRAY {
            @Override
            public String toString() {
                return "X-Ray";
            }
        },
        CONSULTATION {
            @Override
            public String toString() {
                return "Consultation";
            }
        },
        BLOOD_TEST {
            @Override
            public String toString() {
                return "Blood Test";
            }
        },
    }

    private final Appointment appointment;
    private final ServiceType serviceType;
    private final Prescription prescription;
    private final String consultationNotes;

    public AppointmentOutcomeRecord(
        Appointment appointment,
        String serviceType,
        Prescription prescription,
        String consultationNotes
    ) {
        this.id = AppointmentOutcomeRecord.uuid++;
        this.appointment = appointment;
        this.serviceType = EnumUtils.fromString(ServiceType.class, serviceType);
        this.prescription = prescription;
        this.consultationNotes = consultationNotes;
    }

    public AppointmentOutcomeRecord(
        List<String> row,
        Appointment appointment,
        Prescription prescription
    ) {
        this.id = Integer.parseInt(row.get(0));
        this.appointment = appointment;
        this.serviceType = EnumUtils.fromString(ServiceType.class, row.get(2));
        this.prescription = prescription;
        this.consultationNotes = row.get(3);
        AppointmentOutcomeRecord.setUuid(Math.max(AppointmentOutcomeRecord.uuid, this.id)+1);
    }

    public int getId() {
        return id;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public LocalDateTime getAppointmentDate() {
        return appointment.getTimeslot();
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public Prescription getPrescription(){
        return prescription;
    }

    public String getConsultationNotes() {
        return consultationNotes;
    }

    public void printDetails() {
        System.out.println("Appointment Date: " + DateTimeUtil.printShortDateTime(appointment.getTimeslot()));
        System.out.println("Service Type: " + serviceType.toString());
        System.out.println("Prescription Details: " + prescription.toString());
        System.out.println("Consultation Notes: " + consultationNotes);
    }
}
