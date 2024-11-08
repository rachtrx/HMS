package app.model.users.staff;

import app.db.DatabaseManager;
import app.utils.LoggerUtils;
import java.util.List;

public class Pharmacist extends Staff {

    private static int pharmacistUuid = 1;
    private final int pharmacistId;

    public static void setPharmacistUuid(int value) {
        pharmacistUuid = value;
    }

    private Pharmacist(
        String username, 
        String password, 
        String name, 
        String gender, 
        String age
    ) throws Exception {
        super(username, password, name, gender, age);
        this.pharmacistId = Pharmacist.pharmacistUuid++;
    }

    public static Pharmacist create(String username, String password, String name, String gender, String age) throws Exception {
        Pharmacist pharmacist = new Pharmacist(username, password, name, gender, age);
        DatabaseManager.add(pharmacist);
        LoggerUtils.info("Pharmacist created");
        return pharmacist;
    }

    public Pharmacist(
        List<String> userRow,
        List<String> staffRow,
        List<String> pharmacistRow
    ) throws Exception {
        super(userRow, staffRow);
        // LoggerUtils.info(String.join(", ", pharmacistRow));
        this.pharmacistId = Integer.parseInt(pharmacistRow.get(0));
        Pharmacist.setPharmacistUuid(Math.max(Pharmacist.pharmacistUuid, this.pharmacistId)+1);
        LoggerUtils.info("Pharmacist " + this.getName() + " created");
    }

    @Override
    public int getRoleId() {
        return this.pharmacistId;
    };

    @Override
    public String toString() {
        return String.join(
            "\n",
            String.format("Pharmacist ID: %d", this.getRoleId()),
            super.toString()
        );
    }
}
