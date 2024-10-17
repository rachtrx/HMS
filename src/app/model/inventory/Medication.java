package app.model.inventory;

import app.constants.exceptions.NonNegativeException;

/**
* Order for each medication. Tracks any consumption of a medication type.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class Medication {
    private static int uuid = 1;
    private final int id = Medication.uuid++;
    private int stock;
    private String name;

    public Medication(int stock, String name) {
        this.stock = stock;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public int getStock() {
        return this.stock;
    }

    public void setStock(int stock) throws NonNegativeException {
        if (this.stock < 0) {
            throw new NonNegativeException("Stock cannot be less than 0");
        }
        this.stock = stock;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
