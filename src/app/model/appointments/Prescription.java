package app.model.appointments;

import app.model.inventory.Medication;
import app.model.inventory.MedicationOrder;
import java.util.ArrayList;

/**
* Appointments' outcome record.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class Prescription {
    public static enum PrescriptionStatus {
        PENDING {
            @Override
            public String toString() {
                return "Pending";
            }
        },
        DISPENSED {
            @Override
            public String toString() {
                return "Dispensed";
            }
        }
    }

    private ArrayList<MedicationOrder> medicationOrders;
    private PrescriptionStatus status;

    public Prescription(ArrayList<MedicationOrder> medicationOrders, PrescriptionStatus status) {
        this.medicationOrders = medicationOrders;
        this.status = status;
    }

    public PrescriptionStatus getStatus() {
        return this.status;
    }

    public void setStatus(PrescriptionStatus status) {
        this.status = status;
    }
}
