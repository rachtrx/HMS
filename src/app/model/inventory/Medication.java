package app.model.inventory;

import app.constants.exceptions.NonNegativeException;
import app.db.DatabaseManager;
import app.model.ISerializable;
import app.service.MedicationService;
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
public class Medication implements ISerializable {
    private static int uuid = 1;
    private final int id;
    
    private final String name;
    private int stock;
    private int lowAlertLevel;

    private List<Request> requestList;

    public static void setUuid(int value) {
        uuid = value;
    }

    /**
     * @param stock
     * @param name
     * @param lowAlertLevel
     */
    private Medication(String name, String stock, String lowAlertLevel) {
        this.id = Medication.uuid++;
        this.name = name;
        this.stock = Integer.parseInt(stock);
        this.lowAlertLevel = Integer.parseInt(lowAlertLevel);
        this.requestList = new ArrayList<>();
    }

    public static Medication create(String name, String stock, String lowAlertLevel) {
        Medication medication = new Medication(name, stock, lowAlertLevel);
        MedicationService.addMedication(List.of(medication));
        DatabaseManager.add(medication);
        LoggerUtils.info("Medication created");
        return medication;
    }

    protected Medication(List<String> row, List<Request> requests) {
        // LoggerUtils.info(String.join(", ", row));
        this.id = Integer.parseInt(row.get(0));
        this.name = row.get(1);
        this.stock = Integer.parseInt(row.get(2));
        this.lowAlertLevel = Integer.parseInt(row.get(3));
        this.requestList = new ArrayList<>();
        this.requestList = requests;
        Medication.setUuid(Math.max(Medication.uuid, this.id+1));
    }

    @Override
    public List<String> serialize() {
        List<String> row = new ArrayList<>();
        row.add(String.valueOf(this.getId()));
        row.add(String.valueOf(this.getName()));
        row.add(String.valueOf(this.getStock()));
        row.add(String.valueOf(this.getLowAlertLevel()));
        return row;
    }
    
    public List<Request> getRequestList() {
        return requestList;
    }

    public void setRequestList(List<Request> requestList) {
        this.requestList = requestList;
    }

    public void addRequest(Request r) {
        this.requestList.add(r);
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
        DatabaseManager.update(this);
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
        DatabaseManager.update(this);
    }

    /**
     * @return
     */
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return String.join("\n",
                "Medication Details:",
                "- ID: " + id,
                "- Name: " + name,
                "- Stock: " + stock,
                "- Low Alert Level: " + lowAlertLevel,
                "- Requests Pending: " + requestList.size()
        );
    }
}
