package app.model.users.staff;

import app.model.appointments.DoctorEvent;
import java.util.ArrayList;
import java.util.List;

public class Doctor extends Staff {

    private List<DoctorEvent> doctorEvents;

    private static int doctorUuid = 1;
    private final int doctorId;

    public static void setDoctorUuid(int value) {
        doctorUuid = value;
    }

    public Doctor(
        List<String> doctorRow,
        List<String> staffRow,
        List<String> userRow,
        List<DoctorEvent> doctorEvents
    ) throws Exception {
        super(staffRow, userRow);
        this.doctorId = Integer.parseInt(doctorRow.get(0));
        Doctor.setDoctorUuid(Math.max(Doctor.doctorUuid, this.doctorId)+1);
        this.doctorEvents = doctorEvents;
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
        this.doctorEvents = new ArrayList<>();
    }

    public int getDoctorId() {
        return doctorId;
    }

    public List<DoctorEvent> getDoctorEvents() {
        return doctorEvents;
    }

    public void printAvailability() {
        // TODO
    }
}
