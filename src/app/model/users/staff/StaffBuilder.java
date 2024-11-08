package app.model.users.staff;

import app.model.Builder;
import app.model.appointments.DoctorEvent;
import java.util.List;

public class StaffBuilder extends Builder<Staff> {

    public enum Role {
        DOCTOR, PHARMACIST, ADMIN
    }

    private Role role;
    private List<String> userRow;
    private List<String> staffRow;
    private List<String> deptRow;
    private List<DoctorEvent> doctorEvents;

    public StaffBuilder(List<String> row, Role role) throws Exception {
        super(row);
        this.role = role;
    }

    public StaffBuilder(List<String> row, Role role, List<DoctorEvent> doctorEvents) throws Exception {
        super(row);
        if (role != Role.DOCTOR) throw new AssertionError();
        this.role = role;
        this.doctorEvents = doctorEvents;
    }

    public StaffBuilder(Staff instance) {
        super(instance);
    }
    
    public List<String> getUserRow() {
        return this.userRow;
    }

    public List<String> getStaffRow() {
        return this.staffRow;
    }

    public List<String> getdeptRow() {
        return this.deptRow;
    }

    @Override
    public void setSubRows() {
        this.userRow = this.getSubRow(0, 6);
        this.staffRow = this.getSubRow(6, 8);
        this.deptRow = this.getSubRow(8, this.row.size());
    }
    
    @Override
    public Staff deserialize() throws Exception {
        switch (this.role) {
            case DOCTOR:
                return new Doctor(userRow, staffRow, deptRow, doctorEvents);
            case PHARMACIST:
                return new Pharmacist(userRow, staffRow, deptRow);
            case ADMIN:
                return new Admin(userRow, staffRow, deptRow);
            default:
                throw new AssertionError();
        }
    }
}
