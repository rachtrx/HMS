package app.model.appointments;

import app.db.DatabaseManager;
import app.model.ISerializable;
import app.model.inventory.Medication;
import app.model.inventory.MedicationOrder;
import app.service.MedicationService;
import app.utils.EnumUtils;
import app.utils.LoggerUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
* Appointments' outcome record.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class Prescription implements ISerializable {

    private static int uuid = 1;
    private final int id;
    private final List<MedicationOrder> medicationOrders;
    private PrescriptionStatus status;
    private final int outcomeId;

    public static int getUuid() {
        return uuid;
    }

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
        READY {
            @Override
            public String toString() {
                return "Ready";
            }
        },
        DISPENSED {
            @Override
            public String toString() {
                return "Dispensed";
            }
        };
    }

    public List<MedicationOrder> getMedicationOrders() {
        return medicationOrders;
    }

    // public String getMedicationOrdersString() {
    //     return this.medicationOrders
    //         .stream()
    //         .map(order -> {
    //             Medication medication = MedicationService.getMedication(order.getMedicationId());
    //             if (medication == null) {
    //                 return null;
    //             }
    //             return String.format(
    //                 "- Medication: %s\n- Quantity: %d",
    //                 medication.getName(),
    //                 order.getQuantity()
    //             );
    //         }).filter(line -> line != null)
    //         .collect(Collectors.joining("\n"));
    // }

    private Prescription(int outcomeId, List<MedicationOrder> medicationOrders, PrescriptionStatus status) {
        this.id = Prescription.uuid++;
        this.outcomeId = outcomeId;
        this.medicationOrders = medicationOrders;
        this.status = status;
    }

    public static Prescription create(int outcomeId, List<MedicationOrder> medicationOrders, PrescriptionStatus status) {
        Prescription prescription = new Prescription(outcomeId, medicationOrders, status);
        DatabaseManager.add(prescription);
        LoggerUtils.info("Prescription created");
        return prescription;
    }

    public Prescription(List<String> row, List<MedicationOrder> medicationOrders) {
        // LoggerUtils.info(String.join(", ", row));
        this.id = Integer.parseInt(row.get(0));
        this.status = EnumUtils.fromString(PrescriptionStatus.class, row.get(1));
        this.outcomeId = Integer.parseInt(row.get(2));
        this.medicationOrders = medicationOrders;
        Prescription.setUuid(Math.max(Prescription.uuid, this.id+1));
    }

    @Override
    public List<String> serialize() {
        List<String> row = new ArrayList<>();
        row.add(String.valueOf(this.getId()));
        row.add(this.getStatus().toString());
        row.add(String.valueOf(this.getOutcomeId()));
        return row;
    }

    public int getId() {
        return id;
    }

    public int getOutcomeId() {
        return outcomeId;
    }

    public PrescriptionStatus getStatus() {
        return this.status;
    }

    public void setStatus(PrescriptionStatus status) {
        this.status = status;
        DatabaseManager.update(this);
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
