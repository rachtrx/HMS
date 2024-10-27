package app.model.users.staff;

import java.util.List;

public class Admin extends Staff {

    private static int adminUuid = 1;
    protected int adminId;
    
    public static void setAdminUuid(int value) {
        adminUuid = value;
    }

    public Admin(
        List<String> adminRow,
        List<String> staffRow,
        List<String> userRow
    ) throws Exception {
        super(staffRow, userRow);
        this.adminId = Integer.parseInt(adminRow.get(0));
        Admin.setAdminUuid(Math.max(Admin.adminUuid, this.adminId)+1);
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
    }

    @Override
    public int getRoleId() {
        return this.adminId;
    };
}