package app.model.appointments;

import app.utils.EnumUtils;
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

    private final int appointmentId;
    private final ServiceType serviceType;
    private final Prescription prescription;
    private final String consultationNotes;

    public AppointmentOutcomeRecord(
        int appointmentId,
        String serviceType,
        Prescription prescription,
        String consultationNotes
    ) {
        this.id = AppointmentOutcomeRecord.uuid++;
        this.appointmentId = appointmentId;
        this.serviceType = EnumUtils.fromString(ServiceType.class, serviceType);
        this.prescription = prescription;
        this.consultationNotes = consultationNotes;
    }

    public AppointmentOutcomeRecord(
        List<String> row,
        Prescription prescription
    ) {
        this.id = Integer.parseInt(row.get(0));
        this.appointmentId = Integer.parseInt(row.get(1));
        this.serviceType = EnumUtils.fromString(ServiceType.class, row.get(2));
        this.prescription = prescription;
        this.consultationNotes = row.get(3);
        AppointmentOutcomeRecord.setUuid(Math.max(AppointmentOutcomeRecord.uuid, this.id)+1);
    }

    public int getId() {
        return id;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public String getServiceType() {
        return serviceType.toString();
    }

    public Prescription getPrescription(){
        return prescription;
    }

    public String getConsultationNotes() {
        return consultationNotes;
    }
}
