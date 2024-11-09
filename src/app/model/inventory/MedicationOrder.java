package app.model.inventory;

import app.constants.exceptions.NonNegativeException;
import app.db.DatabaseManager;
import app.model.ISerializable;
import app.utils.LoggerUtils;
import java.util.ArrayList;
import java.util.List;

/**
* Order for each medication. Tracks any consumption of a medication type.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class MedicationOrder implements ISerializable {

    private static int uuid = 1;
    private final int id;

    public static void setUuid(int value) {
        uuid = value;
    }

    private final int medicationId;
    private final int quantity;
    private final int prescriptionId;

    private MedicationOrder(int medicationId, int quantity, int prescriptionId) throws NonNegativeException {
        this.id = MedicationOrder.uuid++;
        this.medicationId = medicationId;
        if (quantity < 0) {
            throw new NonNegativeException("Quantity cannot be less than zero");
        }
        this.quantity = quantity;
        this.prescriptionId = prescriptionId;
    }

    public static MedicationOrder create(int medicationId, int quantity, int prescriptionId) throws NonNegativeException {
        if (quantity < 0) {
            throw new NonNegativeException("Quantity cannot be less than zero");
        }
        MedicationOrder order = new MedicationOrder(medicationId, quantity, prescriptionId);
        DatabaseManager.add(order);
        LoggerUtils.info("Order created");
        return order;
    }

    protected MedicationOrder(List<String> row) {
        // LoggerUtils.info(String.join(", ", row));
        this.id = Integer.parseInt(row.get(0));
        this.prescriptionId = Integer.parseInt(row.get(1));
        this.medicationId = Integer.parseInt(row.get(2));
        this.quantity = Integer.parseInt(row.get(3));
        MedicationOrder.setUuid(Math.max(MedicationOrder.uuid, this.id+1));
    }

    @Override
    public List<String> serialize() {
        List<String> row = new ArrayList<>();
        row.add(String.valueOf(this.getId()));
        row.add(String.valueOf(this.getPrescriptionId()));
        row.add(String.valueOf(this.getMedicationId()));
        row.add(String.valueOf(this.getQuantity()));
        return row;
    }

    public int getId() {
        return id;
    }

    public int getPrescriptionId() {
        return prescriptionId;
    }

    public int getMedicationId() {
        return this.medicationId;
    }

    public int getQuantity() {
        return this.quantity;
    }
}
