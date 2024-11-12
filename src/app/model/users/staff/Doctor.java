package app.model.users.staff;

import app.db.DatabaseManager;
import app.model.appointments.Appointment;
import app.model.appointments.DoctorEvent;
import app.model.users.AppointmentManager;
import app.utils.LoggerUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Doctor extends Staff implements AppointmentManager {

    private static int doctorUuid = 1;
    private final int doctorId;
    private final List<DoctorEvent> doctorEvents;

    public static void setDoctorUuid(int value) {
        doctorUuid = value;
    }

    private Doctor(
        String username, 
        String name, 
        String gender, 
        String age
    ) throws Exception {
        super(username, name, gender, age);
        this.doctorId = Doctor.doctorUuid++;
        this.doctorEvents = new ArrayList<>();
    }

    public static Doctor create(String username, String name, String gender, String age) throws Exception {
        Doctor doctor = new Doctor(username, name, gender, age);
        DatabaseManager.add(doctor);
        LoggerUtils.info("Doctor created");
        return doctor;
    }

    protected Doctor(
        List<String> userRow,
        List<String> staffRow,
        List<String> doctorRow,
        List<DoctorEvent> doctorEvents
    ) throws Exception {
        super(userRow, staffRow);
        // LoggerUtils.info(String.join(", ", doctorRow));
        this.doctorId = Integer.parseInt(doctorRow.get(0));
        Doctor.setDoctorUuid(Math.max(Doctor.doctorUuid, this.doctorId+1));

        if (doctorEvents == null) {
            this.doctorEvents = new ArrayList<>();
        } else {
            this.doctorEvents = doctorEvents;
        }

        LoggerUtils.info("Doctor " + this.getName() + " created");
    }

    @Override
    public void addAppointment(Appointment a) { // TODO use interface for Both Doctor and Patient?
        this.doctorEvents.add(a);
    }

    public List<DoctorEvent> getDoctorEvents() {
        return doctorEvents;
    }

    public void addDoctorEvent(DoctorEvent e) {
        this.doctorEvents.add(e);
    }

    public void deleteDoctorEvent(LocalDateTime timeslot) {
        Optional<DoctorEvent> target = this.doctorEvents
            .stream()
            .filter(event -> event.getTimeslot().equals(timeslot))
            .findFirst();
        if (target.isPresent()) {
            this.deleteDoctorEvent(target.get());
        }
    }

    public void deleteDoctorEvent(DoctorEvent e) {
        this.doctorEvents.remove(e);
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

    @Override
    public String toString() {
        return String.join(
            "\n",
            String.format("Doctor ID: %d", this.getRoleId()),
            super.toString()
        );
    }
}
