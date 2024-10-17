package app.model.inventory;

import app.constants.exceptions.NonNegativeException;

/**
* Order for each medication. Tracks any consumption of a medication type.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class MedicationOrder {
    private Medication medication;
    private int quantity;

    public MedicationOrder(Medication medication, int quantity) {
        this.medication = medication;
        this.quantity = quantity;
    }

    public Medication getMedication() {
        return this.medication;
    }

    public void setMedication(Medication medication) {
        this.medication = medication;
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
}
