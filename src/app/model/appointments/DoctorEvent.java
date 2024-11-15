package app.model.appointments;

import app.constants.exceptions.InvalidTimeslotException;
import app.db.DatabaseManager;
import app.model.ISerializable;
import app.utils.DateTimeUtils;
import app.utils.LoggerUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DoctorEvent implements ISerializable {

    private static int uuid = 1;
    private final int id;
    public final String filename = "src/resources/Doctor_Event_List.csv";

    public static void setUuid(int value) {
        uuid = value;
    }

    private int doctorId;
    private Timeslot timeslot;

    protected DoctorEvent(int doctorId, LocalDateTime timeSlot) throws InvalidTimeslotException {
        this.id = DoctorEvent.uuid++;
        this.doctorId = doctorId;
        this.timeslot = new Timeslot(timeSlot);
    }

    public static DoctorEvent create(int doctorId, LocalDateTime timeSlot) throws InvalidTimeslotException {
        DoctorEvent event = new DoctorEvent(doctorId, timeSlot);
        DatabaseManager.add(event);
        LoggerUtils.info("Event created");
        return event;
    }

    protected DoctorEvent(List<String> row) throws InvalidTimeslotException {
        // LoggerUtils.info(String.join(", ", row));
        this.id = Integer.parseInt(row.get(0));
        this.doctorId = Integer.parseInt(row.get(1));
        this.timeslot = new Timeslot(DateTimeUtils.parseShortDateTime(row.get(2)));
        DoctorEvent.setUuid(Math.max(DoctorEvent.uuid, this.id+1));
        // LoggerUtils.info(String.valueOf(DoctorEvent.uuid));
    }

    public static DoctorEvent deserialize (List<String> row) throws Exception {
        return new DoctorEvent(row);
    }

    @Override
    public List<String> serialize() {
        List<String> row = new ArrayList<>();
        row.add(String.valueOf(this.getId()));
        row.add(String.valueOf(this.getDoctorId())); // doctor event id
        row.add(String.valueOf(DateTimeUtils.printShortDateTime(this.getTimeslot())));
        return row;
    }

    public int getId() {
        return id;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
        DatabaseManager.update(this);
    }

    public LocalDateTime getTimeslot() {
        return this.timeslot.getTimeSlot();
    }

    public void setTimeslot(Timeslot timeslot) {
        this.timeslot = timeslot;
        DatabaseManager.update(this);
    }

    public boolean isAppointment() {
        return this instanceof Appointment;
    }
}
