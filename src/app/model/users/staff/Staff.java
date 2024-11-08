package app.model.users.staff;

import app.model.ISerializable;
import app.model.users.User;
import app.utils.LoggerUtils;
import java.util.ArrayList;
import java.util.List;

public abstract class Staff extends User {

    private static int staffUuid = 1;
    protected int staffId;
    protected int age;

    public static void setStaffUuid(int value) {
        staffUuid = value;
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

    public Staff(
        List<String> userRow,
        List<String> staffRow
    ) throws Exception {
        super(userRow); // General User Information
        // LoggerUtils.info(String.join(", ", staffRow));
        this.staffId = Integer.parseInt(staffRow.get(0));
        this.age = Integer.parseInt(staffRow.get(2));
        Staff.setStaffUuid(Math.max(Staff.staffUuid, this.staffId)+1);
    }

    @Override
    public List<String> serialize() {
        List<String> accRow = super.serialize();

        List<String> row = new ArrayList<>();
        
        // Staff
        row.add(String.valueOf(this.getStaffId()));
        row.add(String.valueOf(this.getUserId()));
        row.add(String.valueOf(this.getAge()));
        // Dept
        row.add(String.valueOf(this.getRoleId()));
        row.add(String.valueOf(this.getStaffId()));
        
        accRow.addAll(row);
        return accRow;
    }

    public int getStaffId() {
        return staffId;
    }

    public int getAge() {
        return age;
    }
}