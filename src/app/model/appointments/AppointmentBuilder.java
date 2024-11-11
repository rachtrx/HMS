package app.model.appointments;

import app.model.Builder;
import app.model.appointments.Appointment.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.List;

public class AppointmentBuilder extends Builder<Appointment> {
    private AppointmentOutcomeRecord outcomeRecord;
    private List<String> eventRow;
    private List<String> apptRow;

    public AppointmentBuilder(List<String> row, AppointmentOutcomeRecord outcomeRecord) throws Exception {
        super(row);
        this.outcomeRecord = outcomeRecord;
    }

    public AppointmentBuilder(Appointment instance) {
        super(instance);
    }

    public List<String> getEventRow() {
        return this.eventRow;
    }

    public List<String> getApptRow() {
        return this.apptRow;
    }

    @Override
    public void setSubRows() {
        this.eventRow = this.getSubRow(0, 3);
        this.apptRow = this.getSubRow(3, this.row.size());
    }
    
    @Override
    public Appointment deserialize() throws Exception {
        Appointment a = new Appointment(eventRow, apptRow, this.outcomeRecord);
        if (a.getTimeslot().isBefore(LocalDateTime.now()) && a.getAppointmentStatus() == AppointmentStatus.PENDING) {
            a.setAppointmentStatus(AppointmentStatus.CANCELLED);
        }
        return a;
    }
}
