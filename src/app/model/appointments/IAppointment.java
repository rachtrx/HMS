
import java.util.ArrayList;

import app.model.appointments.AppointmentDisplay;
import app.model.appointments.AppointmentParse;

public interface IAppointment {

    public AppointmentParse appointmentParse;
    public AppointmentDisplay appointmentDisplay;

    public ArrayList<Appointment> getAppointments();
    
}