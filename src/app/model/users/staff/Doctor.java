package app.model.users.staff;

import java.util.List;

public class Doctor extends Staff {

    private static int doctorUuid = 1;
    private final int doctorId;

    public static void setDoctorUuid(int value) {
        doctorUuid = value;
    }

    public Doctor(
        List<String> doctorRow,
        List<String> staffRow,
        List<String> userRow
    ) throws Exception {
        super(staffRow, userRow);
        this.doctorId = Integer.parseInt(doctorRow.get(0));
        Doctor.setDoctorUuid(Math.max(Doctor.doctorUuid, this.doctorId)+1);
    }

    public Doctor(
        String username, 
        String password, 
        String name, 
        String gender, 
        String age
    ) throws Exception {
        super(username, password, name, gender, age);
        this.doctorId = Doctor.doctorUuid++;
    }

    public void printAvailability() {
        // TODO
    }

    @Override
    public int getRoleId() {
        return this.doctorId;
    };
}
