package app.model.appointments;

import java.time.LocalDate;
import java.util.ArrayList;

import app.model.inventory.Medication;

/**
* Appointments' outcome record.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class AppointmentOutcomeRecord {
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
    private LocalDate appointmentDate;
    private ServiceType serviceType;
    private Prescription prescription;
    private String consultationNotes;
}
