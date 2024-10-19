package app.model.inventory;

import java.util.List;

import app.constants.exceptions.NonNegativeException;
import app.model.appointments.AppointmentOutcomeRecord;
import app.model.appointments.Prescription;
import app.model.users.User;

/**
* Order for each medication. Tracks any consumption of a medication type.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class MedicationOrder {

    private static int uuid = 1;
    private final int id;

    public static void setUuid(int value) {
        uuid = value;
    }

    private int medicationId;
    private int quantity;
    private int prescriptionId;

    public MedicationOrder(int medicationId, int quantity, int prescriptionId) {
        this.id = MedicationOrder.uuid++;
        this.medicationId = medicationId;
        this.quantity = quantity;
        this.prescriptionId = prescriptionId;

    }

    public MedicationOrder(List<String> row) {
        this.id = Integer.parseInt(row.get(0));
        this.prescriptionId = Integer.parseInt(row.get(1));
        this.medicationId = Integer.parseInt(row.get(2));
        this.quantity = Integer.parseInt(row.get(3));
        MedicationOrder.setUuid(Math.max(MedicationOrder.uuid, this.id)+1);
    }

    public int getId() {
        return id;
    }

    public int getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(int prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public int getMedicationId() {
        return this.medicationId;
    }

    public void setMedicationId(int medicationId) {
        this.medicationId = medicationId;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int quantity) throws NonNegativeException {
        if (quantity < 0) {
            throw new NonNegativeException("Quantity cannot be less than zero");
        }
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return medication.toString() + ", Quantity: " + quantity;
    }
}
