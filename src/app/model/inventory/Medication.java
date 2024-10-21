package app.model.inventory;

import app.constants.exceptions.NonNegativeException;
import java.util.List;

/**
* Order for each medication. Tracks any consumption of a medication type.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class Medication {
    private static int uuid = 1;
    private final int id;
    
    private int stock;
    private final String name;
    private int lowAlertLevel;

    /**
     * @param stock
     * @param name
     * @param lowAlertLevel
     */
    public Medication(int id, String name, int stock, int lowAlertLevel) {
        this.id = Medication.uuid++;
        this.name = name;
        this.stock = stock;
        this.lowAlertLevel = lowAlertLevel;
    }

    public Medication(List<String> data) {
        this.id = Integer.parseInt(data.get(0));
        this.name = data.get(1);
        this.stock = Integer.parseInt(data.get(2));
        this.lowAlertLevel = Integer.parseInt(data.get(3));
    }

    public static void setStartId(int i) {
        uuid = i;
    }

    /**
     * @return
     */
    public int getId() {
        return this.id;
    }

    /**
     * @return
     */
    public int getStock() {
        return this.stock;
    }

    /**
     * @return
     */
    public int getLowAlertLevel() {
        return lowAlertLevel;
    }

    /**
     * @param lowAlertLevel
     */
    public void setLowAlertLevel(int lowAlertLevel) {
        this.lowAlertLevel = lowAlertLevel;
    }

    /**
     * @param stock
     * @throws NonNegativeException
     */
    public void setStock(int stock) throws NonNegativeException {
        if (this.stock < 0) {
            throw new NonNegativeException("Stock cannot be less than 0");
        }
        this.stock = stock;
    }

    /**
     * @return
     */
    public String getName() {
        return this.name;
    }
}
