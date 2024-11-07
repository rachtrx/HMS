package app.model.users.staff;

import app.utils.LoggerUtils;
import java.util.List;

public class Admin extends Staff {

    private static int adminUuid = 1;
    protected int adminId;
    
    public static void setAdminUuid(int value) {
        adminUuid = value;
    }

    public Admin(
        String username, 
        String password, 
        String name, 
        String gender, 
        String age
    ) throws Exception {
        super(username, password, name, gender, age);
        this.adminId = Admin.adminUuid++;
        add(this); // TODO move to factory method?
        LoggerUtils.info("Admin created");
    }

    protected Admin(
        List<String> userRow,
        List<String> staffRow,
        List<String> adminRow
    ) throws Exception {
        super(userRow, staffRow);
        LoggerUtils.info(String.join(", ", adminRow));
        this.adminId = Integer.parseInt(adminRow.get(0));
        Admin.setAdminUuid(Math.max(Admin.adminUuid, this.adminId)+1);
        LoggerUtils.info("Admin " + this.getName() + " created");
    } 

    @Override
    public int getRoleId() {
        return this.adminId;
    };
}