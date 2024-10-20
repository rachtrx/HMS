package app.model.appointments;

import app.constants.exceptions.InvalidTimeslotException;
import app.utils.DateTimeUtil;

import java.time.LocalDateTime;
import java.util.List;

public class DoctorEvent {

    private static int uuid = 1;
    private final int id;

    public static void setUuid(int value) {
        uuid = value;
    }

    private int doctorId;
    private Timeslot timeslot;

    public DoctorEvent(List<String> row) throws InvalidTimeslotException {
        this.id = Integer.parseInt(row.get(0));
        this.doctorId = Integer.parseInt(row.get(1));
        this.timeslot = new Timeslot(DateTimeUtil.parseShortDateTime(row.get(2)));
    }

    public DoctorEvent(int doctorId, LocalDateTime timeSlot) throws InvalidTimeslotException {
        this.id = DoctorEvent.uuid++;
        this.doctorId = doctorId;
        this.timeslot = new Timeslot(timeSlot);
    }

    public int getId() {
        return id;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public LocalDateTime getTimeslot() {
        return this.timeslot.getTimeSlot();
    }

    public void setTimeslot(Timeslot timeslot) {
        this.timeslot = timeslot;
    }

    public boolean isAppointment(DoctorEvent obj) {
        return obj instanceof Appointment;
    }
}
