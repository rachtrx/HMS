package app.model.appointments;

import java.util.ArrayList;

import app.db.AppointmentTable;

public interface IAppointment {

    public AppointmentTable appointmentParse;
    public AppointmentDisplay appointmentDisplay;

    public ArrayList<Appointment> getAppointments();
    
}