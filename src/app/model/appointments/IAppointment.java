
import java.util.ArrayList;

import app.db.AppointmentTable;
import app.model.appointments.AppointmentDisplay;

public interface IAppointment {

    public AppointmentTable appointmentParse;
    public AppointmentDisplay appointmentDisplay;

    public ArrayList<Appointment> getAppointments();
    
}