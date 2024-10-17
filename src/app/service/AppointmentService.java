package app.service;

import app.constants.exceptions.InvalidTimeslotException;
import app.constants.exceptions.ItemNotFoundException;
import app.constants.exceptions.UserNotFound;
import app.model.appointments.Appointment;
import app.model.appointments.Timeslot;
import app.model.users.Patient;
import app.model.users.User;
import app.model.users.staff.Doctor;
import app.utils.DateTimeUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

/**
* CRUD functionality for appointments.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class AppointmentService extends Service {
    private ArrayList<Appointment> appointments;

    public AppointmentService(
        ArrayList<Appointment> appointments,
        User user
    ) {
        super(user);
        this.appointments = appointments;
    }

    public ArrayList<Appointment> getAppointments() throws UserNotFound {
        if (this.isLoggedIn()) {
            return (ArrayList<Appointment>) this.appointments
                .stream()
                .filter(appointment -> {
                    if (this.getUser() instanceof Patient) {
                        return appointment.getPatientId() == this.getUser().id;
                    } else if (this.getUser() instanceof Doctor) {
                        return appointment.getDoctorId() == this.getUser().id;
                    }
                    throw new AssertionError();
                }).collect(Collectors.toList());
        }
        throw new UserNotFound();
    }

    private Appointment findAppointmentById(int appointmentId) throws ItemNotFoundException, UserNotFound {
        Optional<Appointment> result = this.getAppointments()
            .stream()
            .filter(appointment -> appointment.getId() == appointmentId)
            .findFirst();
        if (result.isPresent()) {
            return result.get();
        }
        throw new ItemNotFoundException(
            String.format("No appointment with ID %d exists", appointmentId)
        );
    }

    public ArrayList<Timeslot> getAvailableAppointmentSlots() throws InvalidTimeslotException{
        LocalDateTime dateTimeNow = LocalDateTime.now();
        LocalDateTime dateTimeOffset = dateTimeNow.plusHours(1);
        LocalTime offsetTime = LocalTime.of(dateTimeOffset.getHour(), dateTimeOffset.getMinute());
        Timeslot nextAvailableTimeslot = new Timeslot(LocalDateTime.of(
            (
                offsetTime.isAfter(Timeslot.lastSlotStartTime) ?
                DateTimeUtil.getNextWorkingDay(dateTimeNow) :
                dateTimeOffset
            ).toLocalDate(),
            (
                dateTimeNow.toLocalTime().isBefore(Timeslot.firstSlotStartTime) ||
                dateTimeNow.toLocalTime().isAfter(Timeslot.lastSlotStartTime)
            ) ? Timeslot.firstSlotStartTime : dateTimeNow.toLocalTime()
        ));
    }

    public void scheduleAppointment(Appointment appointment) {
        this.appointments.add(appointment);
    }

    public void rescheduleAppointment (int appointmentId) throws ItemNotFoundException, UserNotFound {
        Appointment appointment = this.findAppointmentById(appointmentId);
        
    }

    public void cancelAppointment(int appointmentId) throws ItemNotFoundException, UserNotFound {
        Appointment appointment = this.findAppointmentById(appointmentId);
        appointment.cancel();
    }

    public void confirmAppointment(int appointmentId) throws ItemNotFoundException, UserNotFound {
        Appointment appointment = this.findAppointmentById(appointmentId);
        appointment.confirm();
    }

    public void completeAppointment(int appointmentId) throws ItemNotFoundException, UserNotFound {
        Appointment appointment = this.findAppointmentById(appointmentId);
        appointment.completed();
    }
}
