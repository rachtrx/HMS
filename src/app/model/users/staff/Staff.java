package app.model.users.staff;

import app.model.users.User;
import java.util.List;

public abstract class Staff extends User {

    private static int staffUuid = 1;
    protected int staffId;
    protected int age;
    // protected char staffRole;

    public static void setStaffUuid(int value) {
        staffUuid = value;
    }
    
    public Staff(
        List<String> staffRow,
        List<String> userRow
    ) throws Exception {
        super(userRow); // General User Information
        this.staffId = Integer.parseInt(staffRow.get(0));
        this.age = Integer.parseInt(staffRow.get(2));
        Staff.setStaffUuid(Math.max(Staff.staffUuid, this.staffId)+1);
    }

    public Staff(
        String username,
        String password, 
        String name, 
        String gender,
        String age
    ) throws Exception {
        super(username, password, name, gender); // General User Information
        this.age = Integer.parseInt(age);
        this.staffId = Staff.staffUuid++;
    }

    public int getStaffId() {
        return staffId;
    }

    public int getAge() {
        return age;
    }
}