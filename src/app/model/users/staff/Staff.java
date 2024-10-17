package app.model.users.staff;

import app.model.users.User;

public abstract class Staff extends User implements  {

    protected int staffId;
    protected char staffRole;
    protected int age;

    public Staff(String staffId, String name, char gender, String age, String hospitalId) {
        super(hospitalId, "password", name, gender); // General User Information
        this.staffId = Integer.parseInt(staffId);
        this.age = Integer.parseInt(age);
    }

    public String getStaffId() {
        return Character.toString(staffRole) + staffId;
    }
}