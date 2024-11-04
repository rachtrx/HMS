package app.model.users.staff;

import app.model.appointments.Appointment;
import app.model.appointments.DoctorEvent;
import app.model.users.AppointmentManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Doctor extends Staff implements AppointmentManager {

    private static int doctorUuid = 1;
    private final int doctorId;
    private final List<DoctorEvent> doctorEvents;

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

    @Override
    public void addAppointment(Appointment a) { // TODO use interface for Both Doctor and Patient?
        this.doctorEvents.add(a);
    }

    @Override
    public List<Appointment> getAppointments() {
        return doctorEvents.stream()
                           .filter(event -> event instanceof Appointment)
                           .map(event -> (Appointment) event)
                           .collect(Collectors.toList());
    }

    @Override
    public void deleteAppointment(int appointmentId) {
        Iterator<DoctorEvent> iterator = doctorEvents.iterator();
        while (iterator.hasNext()) {
            DoctorEvent event = iterator.next();
            if (event instanceof Appointment && ((Appointment) event).getAppointmentId() == appointmentId) {
                iterator.remove();
                break;
            }
        }
    }

    public void printAvailability() {
        // TODO
    }

    @Override
    public int getRoleId() {
        return this.doctorId;
    };

    public List<DoctorEvent> getDoctorEvents() {
        return doctorEvents;
    }    

    
}
