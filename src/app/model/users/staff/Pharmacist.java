package app.model.users.staff;

import java.util.List;

public class Pharmacist extends Staff {

    private static int pharmacistUuid = 1;
    private final int pharmacistId;

    public static void setPharmacistUuid(int value) {
        pharmacistUuid = value;
    }

    public Pharmacist(
        List<String> pharmacistRow,
        List<String> staffRow,
        List<String> userRow
    ) throws Exception {
        super(staffRow, userRow);
        this.pharmacistId = Integer.parseInt(pharmacistRow.get(0));
        Pharmacist.setPharmacistUuid(Math.max(Pharmacist.pharmacistUuid, this.pharmacistId)+1);
    }

    public Pharmacist(
        String username, 
        String password, 
        String name, 
        String gender, 
        String age
    ) throws Exception {
        super(username, password, name, gender, age);
        this.pharmacistId = Pharmacist.pharmacistUuid++;
    }

    @Override
    public int getRoleId() {
        return this.pharmacistId;
    };
}
