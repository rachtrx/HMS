package app.model.users;

import app.model.appointments.Appointment;
import java.util.List;

public interface AppointmentManager {
    void addAppointment(Appointment appointment);
    List<Appointment> getAppointments();

    void deleteAppointment(int appointmentId);
}

