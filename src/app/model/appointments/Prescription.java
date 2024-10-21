package app.model.appointments;

import app.model.inventory.MedicationOrder;
import app.utils.EnumUtils;
import java.util.List;

/**
* Appointments' outcome record.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class Prescription {

    private static int uuid = 1;
    private final int id;

    public static void setUuid(int value) {
        uuid = value;
    }

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

    private final List<MedicationOrder> medicationOrders;
    private PrescriptionStatus status;
    private int outcomeId;

    public Prescription(int outcomeId, List<MedicationOrder> medicationOrders, PrescriptionStatus status) {
        this.id = Prescription.uuid++;
        this.outcomeId = outcomeId;
        this.medicationOrders = medicationOrders;
        this.status = status;
    }

    public Prescription(List<String> row, List<MedicationOrder> medicationOrders) {
        this.id = Integer.parseInt(row.get(0));
        this.status = EnumUtils.fromString(PrescriptionStatus.class, row.get(1));
        this.outcomeId = Integer.parseInt(row.get(2));
        this.medicationOrders = medicationOrders;
        Prescription.setUuid(Math.max(Prescription.uuid, this.id)+1);
    }

    public int getId() {
        return id;
    }

    public int getOutcomeId() {
        return outcomeId;
    }

    public void setOutcomeId(int outcomeId) {
        this.outcomeId = outcomeId;
    }

    public PrescriptionStatus getStatus() {
        return this.status;
    }

    public void setStatus(PrescriptionStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Prescription Status: ").append(status).append("\n");
        sb.append("Medication Orders:\n");
        for (MedicationOrder order : medicationOrders) {
            sb.append(" - ").append(order).append("\n");
        }
        return sb.toString();
    }
}
